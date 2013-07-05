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
package org.codeqinvest.web.investment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.codeqinvest.investment.roi.RoiDistribution;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class RoiDistributionFilterTest {

  private RoiDistributionFilter roiDistributionFilter;

  @Before
  public void setUp() throws Exception {
    roiDistributionFilter = new RoiDistributionFilter();
  }

  @Test
  public void filterCorrectNumberOfBestRois() {
    Map<String, Integer> roiByArtefact = Maps.newHashMap();
    roiByArtefact.put("A", 10);
    roiByArtefact.put("B", 10);
    roiByArtefact.put("C", 10);
    List<RoiDistribution> roiDistribution = Arrays.asList(new RoiDistribution(0, roiByArtefact));
    assertThat(roiDistributionFilter.filterHighestRoi(roiDistribution, 2).iterator().next().getRoiByArtefact().keySet()).containsOnly("A", "B");
  }

  @Test
  public void notFailWhenNumberOfArtefactsToFilterIsBiggerThanActualArtefacts() {
    Map<String, Integer> roiByArtefact = Maps.newHashMap();
    roiByArtefact.put("A", 10);
    List<RoiDistribution> roiDistribution = Arrays.asList(new RoiDistribution(0, roiByArtefact));
    assertThat(roiDistributionFilter.filterHighestRoi(roiDistribution, 2).iterator().next().getRoiByArtefact().keySet()).containsOnly("A");
  }

  @Test
  public void filterBestRoisFromOneDistribution() {
    Map<String, Integer> roiByArtefact = Maps.newHashMap();
    roiByArtefact.put("A", 10);
    roiByArtefact.put("B", 11);
    roiByArtefact.put("C", 9);
    roiByArtefact.put("D", 12);
    List<RoiDistribution> roiDistribution = Arrays.asList(new RoiDistribution(0, roiByArtefact));
    assertThat(roiDistributionFilter.filterHighestRoi(roiDistribution, 3).iterator().next().getRoiByArtefact().keySet()).containsOnly("A", "B", "D");
  }

  @Test
  public void filterBestRoisFromManyDistributions() {
    Map<String, Integer> roiByArtefactOneInvest = Maps.newHashMap();
    roiByArtefactOneInvest.put("A", 10);
    roiByArtefactOneInvest.put("B", 1);
    roiByArtefactOneInvest.put("C", 2);
    roiByArtefactOneInvest.put("D", 9);
    roiByArtefactOneInvest.put("E", 8);

    Map<String, Integer> roiByArtefactForTwoInvest = Maps.newHashMap();
    roiByArtefactForTwoInvest.put("A", 2);
    roiByArtefactForTwoInvest.put("B", 10);
    roiByArtefactForTwoInvest.put("C", 4);
    roiByArtefactForTwoInvest.put("D", 3);
    roiByArtefactForTwoInvest.put("E", 1);

    Map<String, Integer> roiByArtefactForThreeInvest = Maps.newHashMap();
    roiByArtefactForThreeInvest.put("A", 1);
    roiByArtefactForThreeInvest.put("B", 2);
    roiByArtefactForThreeInvest.put("C", 3);
    roiByArtefactForThreeInvest.put("D", 4);
    roiByArtefactForThreeInvest.put("E", 10);

    List<RoiDistribution> roiDistributions = Arrays.asList(
        new RoiDistribution(1, roiByArtefactOneInvest),
        new RoiDistribution(2, roiByArtefactForTwoInvest),
        new RoiDistribution(3, roiByArtefactForThreeInvest));

    List<RoiDistribution> distributions = Lists.newArrayList(roiDistributionFilter.filterHighestRoi(roiDistributions, 4));
    assertThat(distributions).hasSize(3);
    assertThat(distributions.get(0).getRoiByArtefact().keySet()).containsOnly("A", "B", "D", "E");
  }
}
