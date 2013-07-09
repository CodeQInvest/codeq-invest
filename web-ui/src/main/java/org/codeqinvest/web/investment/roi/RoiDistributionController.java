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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.investment.InvestmentAmountParser;
import org.codeqinvest.investment.InvestmentParsingException;
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author fmueller
 */
@Slf4j
@Controller
class RoiDistributionController {

  private static final int ROI_THRESHOLD = 0;

  private final ProjectRepository projectRepository;
  private final LastQualityAnalysisService lastQualityAnalysisService;
  private final RoiDistributionCalculator roiDistributionCalculator;
  private final InvestmentAmountParser investmentAmountParser;
  private final RoiDistributionFilter roiDistributionFilter;

  @Autowired
  RoiDistributionController(ProjectRepository projectRepository,
                            LastQualityAnalysisService lastQualityAnalysisService,
                            RoiDistributionCalculator roiDistributionCalculator,
                            InvestmentAmountParser investmentAmountParser,
                            RoiDistributionFilter roiDistributionFilter) {
    this.projectRepository = projectRepository;
    this.lastQualityAnalysisService = lastQualityAnalysisService;
    this.roiDistributionCalculator = roiDistributionCalculator;
    this.investmentAmountParser = investmentAmountParser;
    this.roiDistributionFilter = roiDistributionFilter;
  }

  @RequestMapping(value = "/projects/{projectId}/roidistribution", method = RequestMethod.GET)
  @ResponseBody
  EnhancedRoiDistribution retrieveRoiDistribution(@PathVariable long projectId, @RequestParam(required = false) String basePackage) throws InvestmentParsingException {
    Project project = projectRepository.findOne(projectId);
    QualityAnalysis lastAnalysis = lastQualityAnalysisService.retrieveLastSuccessfulAnalysis(project);

    Set<RoiDistribution> roiDistributions = Sets.newHashSet();
    for (int i = 0; i < RoiDistributionChartRepresentation.DEFAULT_INVESTMENTS.length; i++) {
      int investment = investmentAmountParser.parseMinutes(RoiDistributionChartRepresentation.DEFAULT_INVESTMENTS[i]);
      roiDistributions.add(roiDistributionCalculator.calculateRoiDistribution(lastAnalysis, basePackage, investment));
    }

    Collection<RoiDistributionChartRepresentation> bestRois = convertToChartRepresentations(roiDistributionFilter.filterHighestRoi(
        roiDistributions, RoiDistributionChartRepresentation.DEFAULT_INVESTMENTS.length + 1));

    return new EnhancedRoiDistribution(
        new TreeSet<RoiDistributionChartRepresentation>(filterChartDataByThreshold(convertToChartRepresentations(roiDistributions))),
        new TreeSet<RoiDistributionChartRepresentation>(filterChartDataByThreshold(bestRois)));
  }

  private Collection<RoiDistributionChartRepresentation> convertToChartRepresentations(Collection<RoiDistribution> roiDistributions) throws InvestmentParsingException {
    Map<String, RoiDistributionChartRepresentation> chartData = Maps.newHashMap();
    for (RoiDistribution roiDistribution : roiDistributions) {
      for (Map.Entry<String, Integer> roiOfArtefact : roiDistribution.getRoiByArtefact().entrySet()) {
        String artefact = getLastPackageName(roiOfArtefact.getKey());
        if (!chartData.containsKey(artefact)) {
          chartData.put(artefact, new RoiDistributionChartRepresentation(artefact));
        }
        final int i = findInvestmentIndex(roiDistribution);
        chartData.get(artefact).setValue(i, new ValueTuple(RoiDistributionChartRepresentation.DEFAULT_INVESTMENTS[i], roiOfArtefact.getValue()));
      }
    }
    return chartData.values();
  }

  private int findInvestmentIndex(RoiDistribution roiDistribution) throws InvestmentParsingException {
    for (int i = 0; i < RoiDistributionChartRepresentation.DEFAULT_INVESTMENTS.length; i++) {
      if (investmentAmountParser.parseMinutes(RoiDistributionChartRepresentation.DEFAULT_INVESTMENTS[i]) == roiDistribution.getInvestInMinutes()) {
        return i;
      }
    }
    return -1;
  }

  private Collection<RoiDistributionChartRepresentation> filterChartDataByThreshold(Collection<RoiDistributionChartRepresentation> chartData) {
    Set<RoiDistributionChartRepresentation> filteredChartData = Sets.newHashSet();
    for (RoiDistributionChartRepresentation roiDistribution : chartData) {
      for (ValueTuple value : roiDistribution.getValues()) {
        if (value.getY() > ROI_THRESHOLD) {
          filteredChartData.add(roiDistribution);
          break;
        }
      }
    }
    return filteredChartData;
  }

  private String getLastPackageName(String artefactName) {
    int indexOfLastPackageStart = artefactName.lastIndexOf(".");
    return indexOfLastPackageStart != -1
        ? artefactName.substring(indexOfLastPackageStart + 1)
        : artefactName;
  }
}
