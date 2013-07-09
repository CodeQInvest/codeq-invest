/*
 * Copyright 2013 Felix Müller
 *
 * This file is part of CodeQ Invest.
 *
 * CodeQ Invest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeQ Invest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeQ Invest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.codeqinvest.web.investment.roi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.codeqinvest.investment.roi.RoiDistribution;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class offers functionality to filter a collection of
 * roi distributions, e.g. to get only the best ROIs.
 *
 * @author fmueller
 */
@Component
class RoiDistributionFilter {

  /**
   * This method filters a certain number of artefacts with highest roi out of many
   * roi distributions.
   */
  Collection<RoiDistribution> filterHighestRoi(Collection<RoiDistribution> roiDistributions, int numberOfBestElements) {
    Map<Integer, RoiDistribution> filteredRoiDistributionsByInvest = Maps.newHashMap();
    for (RoiDistribution distribution : roiDistributions) {
      filteredRoiDistributionsByInvest.put(distribution.getInvestInMinutes(),
          new RoiDistribution(distribution.getInvestInMinutes(), new HashMap<String, Integer>()));
    }

    for (String artefact : findArtefactsWithHighestRoi(roiDistributions, numberOfBestElements)) {
      for (RoiDistribution distribution : roiDistributions) {
        Integer roi = distribution.getRoiByArtefact().get(artefact);
        filteredRoiDistributionsByInvest.get(distribution.getInvestInMinutes()).getRoiByArtefact().put(artefact, roi);
      }
    }
    return filteredRoiDistributionsByInvest.values();
  }

  private Set<String> findArtefactsWithHighestRoi(Collection<RoiDistribution> roiDistributions, int numberOfArtefactsToFilter) {
    final List<RoiDistribution> orderedDistributions = orderAscendingByInvest(roiDistributions);

    final int numberOfArtefacts = numberOfArtefactsToFilter < numberOfArtefacts(roiDistributions) ? numberOfArtefactsToFilter : numberOfArtefacts(roiDistributions);
    Set<String> bestArtefacts = Sets.newHashSet();
    for (int i = 0; i < numberOfArtefacts; i++) {
      int cursor = i;
      boolean addedArtefactWithHighRoi = false;
      do {
        RoiDistribution currentDistribution = orderedDistributions.get(cursor % orderedDistributions.size());
        for (String currentArtefact : orderDescendingByRoi(currentDistribution)) {
          if (!bestArtefacts.contains(currentArtefact)) {
            bestArtefacts.add(currentArtefact);
            addedArtefactWithHighRoi = true;
            break;
          }
        }
        cursor++;
      } while (!addedArtefactWithHighRoi);
    }
    return bestArtefacts;
  }

  private int numberOfArtefacts(Collection<RoiDistribution> roiDistributions) {
    Set<String> artefacts = Sets.newHashSet();
    for (RoiDistribution roiDistribution : roiDistributions) {
      artefacts.addAll(roiDistribution.getRoiByArtefact().keySet());
    }
    return artefacts.size();
  }

  private List<String> orderDescendingByRoi(final RoiDistribution roiDistribution) {
    List<String> ordered = Lists.newArrayList(roiDistribution.getRoiByArtefact().keySet());
    Collections.sort(ordered, new Comparator<String>() {
      @Override
      public int compare(String artefact, String other) {
        return -1 * roiDistribution.getRoiByArtefact().get(artefact).compareTo(roiDistribution.getRoiByArtefact().get(other));
      }
    });
    return ordered;
  }

  private List<RoiDistribution> orderAscendingByInvest(Collection<RoiDistribution> roiDistributions) {
    List<RoiDistribution> ordered = Lists.newArrayList(roiDistributions);
    Collections.sort(ordered, new Comparator<RoiDistribution>() {

      @Override
      public int compare(RoiDistribution roiDistribution, RoiDistribution other) {
        if (roiDistribution.getInvestInMinutes() < other.getInvestInMinutes()) {
          return -1;
        } else if (roiDistribution.getInvestInMinutes() > other.getInvestInMinutes()) {
          return 1;
        } else {
          return 0;
        }
      }
    });
    return ordered;
  }
}
