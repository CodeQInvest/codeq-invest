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
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

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

  public QualityInvestmentPlan computeInvestmentPlan(QualityAnalysis analysis, String basePackage, int investmentInMinutes) {
    double overallProfit = 0.0f;
    Multimap<Integer, QualityViolation> violationsByRemediationCosts = ArrayListMultimap.create();
    for (QualityViolation violation : analysis.getViolations()) {
      violationsByRemediationCosts.put(violation.getRemediationCosts(), violation);
    }

    List<Integer> allRemediationCosts = new ArrayList<Integer>();
    for (Integer remediationCost : violationsByRemediationCosts.keySet()) {
      int numberOfViolations = violationsByRemediationCosts.get(remediationCost).size();
      for (int i = 0; i < numberOfViolations; i++) {
        allRemediationCosts.add(remediationCost);
      }
    }
    Collections.sort(allRemediationCosts, new IntegerDescendingComparator());

    SortedSet<QualityInvestmentPlanEntry> investmentPlanEntries = Sets.newTreeSet(new InvestmentPlanEntryByProfitAndCostsComparator());

    int toInvest = investmentInMinutes;
    int invested = 0;
    for (int remediationCost : allRemediationCosts) {
      if (remediationCost <= toInvest) {

        invested += remediationCost;
        toInvest -= remediationCost;

        Collection<QualityViolation> violations = violationsByRemediationCosts.get(remediationCost);
        QualityViolation violationWithMostProfit = getViolationWithMostProfit(violations);

        int profit = (int) Math.round(profitCalculator.calculateProfit(violationWithMostProfit));
        overallProfit += profit;

        investmentPlanEntries.add(new QualityInvestmentPlanEntry(
            violationWithMostProfit.getRequirement().getMetricIdentifier(),
            violationWithMostProfit.getRequirement().getOperator() + " " + violationWithMostProfit.getRequirement().getThreshold(),
            violationWithMostProfit.getArtefact().getName(),
            profit,
            violationWithMostProfit.getRemediationCosts()));

        violationsByRemediationCosts.get(remediationCost).remove(violationWithMostProfit);
      }
    }

    return new QualityInvestmentPlan(basePackage, invested, (int) Math.round(overallProfit), calculateRoi(analysis, overallProfit), investmentPlanEntries);
  }

  private QualityViolation getViolationWithMostProfit(Collection<QualityViolation> violations) {
    QualityViolation mostProfit = null;
    double profitOfMostProfit = 0.0;
    for (QualityViolation violation : violations) {
      double profitOfCurrentViolation = profitCalculator.calculateProfit(violation);
      if (mostProfit == null || profitOfMostProfit < profitOfCurrentViolation) {
        mostProfit = violation;
        profitOfMostProfit = profitOfCurrentViolation;
      }
    }
    return mostProfit;
  }

  private int calculateRoi(QualityAnalysis analysis, double overallProfit) {
    int overallRemediationCosts = 0;
    for (QualityViolation violation : analysis.getViolations()) {
      overallRemediationCosts += violation.getRemediationCosts();
    }
    return (int) Math.round(overallProfit / (double) overallRemediationCosts * 100);
  }

  private static class InvestmentPlanEntryByProfitAndCostsComparator implements Comparator<QualityInvestmentPlanEntry> {

    @Override
    public int compare(QualityInvestmentPlanEntry entry, QualityInvestmentPlanEntry otherEntry) {
      if (entry.getProfitInMinutes() < otherEntry.getProfitInMinutes()) {
        return 1;
      } else if (entry.getProfitInMinutes() > otherEntry.getProfitInMinutes()) {
        return -1;
      } else if (entry.getRemediationCostsInMinutes() < otherEntry.getRemediationCostsInMinutes()) {
        return 1;
      } else if (entry.getRemediationCostsInMinutes() > otherEntry.getRemediationCostsInMinutes()) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  private static class IntegerDescendingComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer integer, Integer other) {
      return -1 * integer.compareTo(other);
    }
  }
}
