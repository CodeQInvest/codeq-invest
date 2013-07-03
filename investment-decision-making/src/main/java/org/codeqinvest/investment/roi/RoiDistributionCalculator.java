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
package org.codeqinvest.investment.roi;

import com.google.common.collect.Maps;
import org.codeqinvest.investment.QualityInvestmentPlan;
import org.codeqinvest.investment.QualityInvestmentPlanEntry;
import org.codeqinvest.investment.QualityInvestmentPlanService;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author fmueller
 */
@Service
public class RoiDistributionCalculator {

  private final QualityInvestmentPlanService qualityInvestmentPlanService;

  @Autowired
  public RoiDistributionCalculator(QualityInvestmentPlanService qualityInvestmentPlanService) {
    this.qualityInvestmentPlanService = qualityInvestmentPlanService;
  }

  public RoiDistribution calculateRoiDistribution(QualityAnalysis analysis, String basePackage, int investment) {
    final QualityInvestmentPlan qualityInvestmentPlan = qualityInvestmentPlanService.computeInvestmentPlan(analysis, basePackage, investment);
    return new RoiDistribution(investment, qualityInvestmentPlan.getRoi(), sumRoiByArtefact(qualityInvestmentPlan));
  }

  private Map<String, Integer> sumRoiByArtefact(QualityInvestmentPlan investmentPlan) {
    Map<String, Integer> roiByArtefact = Maps.newHashMap();
    for (String artefact : investmentPlan.getAllArtefactShortNames()) {
      Set<QualityInvestmentPlanEntry> entriesOfArtefact = investmentPlan.getAllEntriesOfArtefact(artefact);
      int profit = sumOfProfit(entriesOfArtefact);
      int remediationCosts = sumOfRemediationCosts(entriesOfArtefact);
      roiByArtefact.put(artefact, Math.round(profit / (float) remediationCosts * 100));
    }
    return roiByArtefact;
  }

  private int sumOfProfit(Set<QualityInvestmentPlanEntry> investmentPlanEntries) {
    int profit = 0;
    for (QualityInvestmentPlanEntry investmentPlanEntry : investmentPlanEntries) {
      profit += investmentPlanEntry.getProfitInMinutes();
    }
    return profit;
  }

  private int sumOfRemediationCosts(Set<QualityInvestmentPlanEntry> investmentPlanEntries) {
    int remediationCosts = 0;
    for (QualityInvestmentPlanEntry investmentPlanEntry : investmentPlanEntries) {
      remediationCosts += investmentPlanEntry.getRemediationCostsInMinutes();
    }
    return remediationCosts;
  }
}
