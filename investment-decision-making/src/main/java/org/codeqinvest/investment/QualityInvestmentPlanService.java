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
package org.codeqinvest.investment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.investment.profit.ProfitCalculator;
import org.codeqinvest.quality.QualityRequirement;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author fmueller
 */
@Slf4j
@Service
public class QualityInvestmentPlanService {

  private final ProfitCalculator profitCalculator;

  @Autowired
  public QualityInvestmentPlanService(ProfitCalculator profitCalculator) {
    this.profitCalculator = profitCalculator;
  }

  // TODO clean up this mess
  public QualityInvestmentPlan computeInvestmentPlan(QualityAnalysis analysis, String basePackage, int investmentInMinutes) {
    Multimap<Double, QualityViolation> violationsByProfit = ArrayListMultimap.create();
    for (QualityViolation violation : filterViolationsByArtefactNameStartingWith(basePackage, analysis.getViolations())) {
      double profit = profitCalculator.calculateProfit(violation);
      if (Math.round(profit) > 0) {
        violationsByProfit.put(profit, violation);
      }
    }

    List<Double> allProfits = new ArrayList<Double>();
    for (Double profit : violationsByProfit.keySet()) {
      int numberOfViolations = violationsByProfit.get(profit).size();
      for (int i = 0; i < numberOfViolations; i++) {
        allProfits.add(profit);
      }
    }
    Collections.sort(allProfits, new DescendingComparator<Double>());

    Set<QualityInvestmentPlanEntry> investmentPlanEntries = Sets.newTreeSet();
    int toInvest = investmentInMinutes;
    int invested = 0;

    for (double profit : allProfits) {
      List<QualityViolation> violations = new ArrayList<QualityViolation>(violationsByProfit.get(profit));
      Collections.sort(violations, new ViolationByRemediationCostsComparator());

      for (QualityViolation violation : violations) {
        int remediationCost = violation.getRemediationCosts();
        if (remediationCost <= toInvest) {

          invested += remediationCost;
          toInvest -= remediationCost;

          QualityRequirement requirement = violation.getRequirement();
          investmentPlanEntries.add(new QualityInvestmentPlanEntry(
              requirement.getMetricIdentifier(),
              requirement.getOperator() + " " + requirement.getThreshold(),
              violation.getArtefact().getName(),
              violation.getArtefact().getShortClassName(),
              (int) Math.round(profit),
              remediationCost));
        }
      }
    }

    final int overallProfit = calculateOverallProfit(investmentPlanEntries);
    return new QualityInvestmentPlan(basePackage,
        invested,
        overallProfit,
        calculateRoi(investmentPlanEntries, overallProfit),
        investmentPlanEntries);
  }

  private int calculateOverallProfit(Set<QualityInvestmentPlanEntry> investmentPlanEntries) {
    int overallProfit = 0;
    for (QualityInvestmentPlanEntry investmentPlanEntry : investmentPlanEntries) {
      overallProfit += investmentPlanEntry.getProfitInMinutes();
    }
    return overallProfit;
  }

  private Collection<QualityViolation> filterViolationsByArtefactNameStartingWith(String basePackage, List<QualityViolation> violations) {
    Collection<QualityViolation> filteredViolations = new ArrayList<QualityViolation>();
    for (QualityViolation violation : violations) {

      String artefactName = violation.getArtefact().getName();
      if (artefactName.startsWith(basePackage)) {

        String artefactPartWithoutBasePackage = artefactName.substring(basePackage.length());
        if (artefactPartWithoutBasePackage.isEmpty()
            || artefactName.substring(basePackage.length()).contains(".")
            || basePackage.isEmpty()) {

          filteredViolations.add(violation);
        }
      }
    }
    return filteredViolations;
  }

  private int calculateRoi(Set<QualityInvestmentPlanEntry> investmentPlanEntries, int overallProfit) {
    // TODO this method could be refactored into own service class
    int overallRemediationCosts = 0;
    for (QualityInvestmentPlanEntry investmentPlanEntry : investmentPlanEntries) {
      overallRemediationCosts += investmentPlanEntry.getRemediationCostsInMinutes();
    }
    return Math.round(overallProfit / (float) overallRemediationCosts * 100);
  }

  private static final class DescendingComparator<T extends Comparable<T>> implements Comparator<T> {

    @Override
    public int compare(T thisValue, T other) {
      return -1 * thisValue.compareTo(other);
    }
  }

  private static final class ViolationByRemediationCostsComparator implements Comparator<QualityViolation> {

    @Override
    public int compare(QualityViolation violation, QualityViolation otherViolation) {
      if (violation.getRemediationCosts() < otherViolation.getRemediationCosts()) {
        return -1;
      } else if (violation.getRemediationCosts() > otherViolation.getRemediationCosts()) {
        return 1;
      } else {
        return 0;
      }
    }
  }
}
