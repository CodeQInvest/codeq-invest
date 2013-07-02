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

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.investment.roi.RoiDistribution;
import org.codeqinvest.investment.roi.RoiDistributionCalculator;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.analysis.LastQualityAnalysisService;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author fmueller
 */
@Slf4j
@Controller
class RoiDistributionController {

  private static final int[] DEFAULT_INVESTMENTS = new int[]{60, 120, 240, 480, 960};

  private final ProjectRepository projectRepository;
  private final LastQualityAnalysisService lastQualityAnalysisService;
  private final RoiDistributionCalculator roiDistributionCalculator;

  @Autowired
  RoiDistributionController(ProjectRepository projectRepository,
                            LastQualityAnalysisService lastQualityAnalysisService,
                            RoiDistributionCalculator roiDistributionCalculator) {
    this.projectRepository = projectRepository;
    this.lastQualityAnalysisService = lastQualityAnalysisService;
    this.roiDistributionCalculator = roiDistributionCalculator;
  }

  @RequestMapping(value = "/projects/{projectId}/roidistribution", method = RequestMethod.GET)
  @ResponseBody
  SortedSet<RoiDistributionChartRepresentation> retrieveRoiDistribution(@PathVariable long projectId, @RequestParam(required = false) String basePackage) {
    Project project = projectRepository.findOne(projectId);
    QualityAnalysis lastAnalysis = lastQualityAnalysisService.retrieveLastSuccessfulAnalysis(project);

    Map<String, RoiDistributionChartRepresentation> chartData = Maps.newHashMap();
    for (int i = 0; i < DEFAULT_INVESTMENTS.length; i++) {
      int investment = DEFAULT_INVESTMENTS[i];
      RoiDistribution roiDistribution = roiDistributionCalculator.calculateRoiDistribution(lastAnalysis, basePackage, investment);
      for (Map.Entry<String, Integer> roiProportionOfArtefact : roiDistribution.getRoiProportionByArtefact().entrySet()) {

        ValueTuple value = new ValueTuple("" + investment, roiProportionOfArtefact.getValue());
        String artefact = roiProportionOfArtefact.getKey();
        if (!chartData.containsKey(artefact)) {
          chartData.put(artefact, new RoiDistributionChartRepresentation(artefact));
        }
        chartData.get(artefact).setValue(i, value);
      }
    }
    return new TreeSet<RoiDistributionChartRepresentation>(chartData.values());
  }

  @Data
  private static class RoiDistributionChartRepresentation implements Comparable<RoiDistributionChartRepresentation> {

    private final String key;
    private final ValueTuple[] values = new ValueTuple[DEFAULT_INVESTMENTS.length];

    RoiDistributionChartRepresentation(String key) {
      this.key = key;
      for (int i = 0; i < DEFAULT_INVESTMENTS.length; i++) {
        values[i] = new ValueTuple("" + DEFAULT_INVESTMENTS[i], 0);
      }
    }

    void setValue(int index, ValueTuple value) {
      values[index] = value;
    }

    @Override
    public int compareTo(RoiDistributionChartRepresentation roiDistributionChartRepresentation) {
      return key.compareToIgnoreCase(roiDistributionChartRepresentation.key);
    }
  }

  @Data
  private static class ValueTuple {

    private final String x;
    private final int y;
  }
}
