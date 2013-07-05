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

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.codeqinvest.investment.QualityInvestmentPlan;
import org.codeqinvest.investment.QualityInvestmentPlanService;
import org.codeqinvest.quality.Artefact;
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
    Set<String> artefactsToAnalyze = mergeToPackageLevel(basePackage, analysis.getAllArtefacts());
    Map<String, Integer> roiByArtefact = Maps.newHashMap();
    for (String artefact : artefactsToAnalyze) {
      QualityInvestmentPlan investmentPlan = qualityInvestmentPlanService.computeInvestmentPlan(analysis, artefact, investment);
      roiByArtefact.put(artefact, investmentPlan.getRoi());
    }
    return new RoiDistribution(investment, roiByArtefact);
  }

  private Set<String> mergeToPackageLevel(String basePackage, Set<Artefact> allArtefacts) {
    Set<String> mergedPackageLevels = Sets.newHashSet();
    for (Artefact artefact : allArtefacts) {
      if (Strings.isNullOrEmpty(basePackage) || artefact.getName().startsWith(basePackage)) {
        mergedPackageLevels.add(getPackageLevel(basePackage, artefact.getName()));
      }
    }
    return mergedPackageLevels;
  }

  private String getPackageLevel(String packageName, String artefact) {
    int indexOfNextPackageStart = artefact.indexOf(".", packageName.length() + 1);
    return indexOfNextPackageStart != -1
        ? packageName + artefact.substring(packageName.length(), indexOfNextPackageStart)
        : artefact;
  }
}
