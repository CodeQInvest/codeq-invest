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

import org.codeqinvest.investment.profit.ProfitCalculator;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.QualityCriteria;
import org.codeqinvest.quality.QualityRequirement;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QualityInvestmentPlanServiceTest {

  private QualityInvestmentPlanService investmentPlanService;

  private QualityAnalysis analysis;

  @Before
  public void setUp() {
    ProfitCalculator profitCalculator = mock(ProfitCalculator.class);
    investmentPlanService = new QualityInvestmentPlanService(profitCalculator);

    QualityViolation violation1 = new QualityViolation(new Artefact("A", ""), createRequirementOnlyWithCriteria(new QualityCriteria("cc", "<", 5.0)), 10, 0, 0.0, "ncloc");
    QualityViolation violation2 = new QualityViolation(new Artefact("B", ""), createRequirementOnlyWithCriteria(new QualityCriteria("rfc", "<=", 50.0)), 60, 0, 0.0, "ncloc");
    QualityViolation violation3 = new QualityViolation(new Artefact("C", ""), createRequirementOnlyWithCriteria(new QualityCriteria("cov", ">", 80.0)), 10, 0, 0.0, "ncloc");
    analysis = QualityAnalysis.success(null, Arrays.asList(violation1, violation2, violation3));
    // overall rc = 10 + 40 + 30 = 80

    // possible overall profit = 300
    when(profitCalculator.calculateProfit(violation1)).thenReturn(100.0);
    when(profitCalculator.calculateProfit(violation2)).thenReturn(80.2);
    when(profitCalculator.calculateProfit(violation3)).thenReturn(120.1);
  }

  private QualityRequirement createRequirementOnlyWithCriteria(QualityCriteria criteria) {
    return new QualityRequirement(null, 0, 0, 0.0, "", criteria);
  }

  @Test
  public void zeroProfitWhenThereAreNoQualityViolations() {
    QualityAnalysis analysisWithoutViolations = QualityAnalysis.success(null, Collections.<QualityViolation>emptyList());
    QualityInvestmentPlan qualityInvestmentPlan = investmentPlanService.computeInvestmentPlan(analysisWithoutViolations, "", 100);
    assertThat(qualityInvestmentPlan.getProfitInMinutes())
        .as("When there are no quality violations the overall profit should be zero.")
        .isZero();
  }

  @Test
  public void calculatesOverallProfitForAllQualityViolations() {
    QualityInvestmentPlan qualityInvestmentPlan = investmentPlanService.computeInvestmentPlan(analysis, "", 80);
    assertThat(qualityInvestmentPlan.getProfitInMinutes())
        .as("The overall profit should be calculated based on all quality violations.")
        .isEqualTo(300);
    assertThat(qualityInvestmentPlan.getInvestmentInMinutes()).isEqualTo(80);
  }

  @Test
  public void whenInvestmentIsBiggerThanPossibleOverallProfitOnlyThePossibleProfitShouldBeCalculated() {
    QualityInvestmentPlan qualityInvestmentPlan = investmentPlanService.computeInvestmentPlan(analysis, "", 100);
    assertThat(qualityInvestmentPlan.getProfitInMinutes()).isEqualTo(300);
    assertThat(qualityInvestmentPlan.getInvestmentInMinutes()).isEqualTo(80);
  }

  @Test
  public void whenInvestmentIsSmallerThanPossibleOverallProfitOnlyTheInvestedAmountShouldBeCalculated() {
    QualityInvestmentPlan qualityInvestmentPlan = investmentPlanService.computeInvestmentPlan(analysis, "", 35);
    assertThat(qualityInvestmentPlan.getProfitInMinutes()).isEqualTo(220);
    assertThat(qualityInvestmentPlan.getInvestmentInMinutes()).isEqualTo(20);
  }

  @Test
  public void calculatesRoiBasedOnRemediationCostsAndProfit() {
    QualityInvestmentPlan qualityInvestmentPlan = investmentPlanService.computeInvestmentPlan(analysis, "", 80);
    assertThat(qualityInvestmentPlan.getRoi()).isEqualTo(375); // 300 / 80
  }

  @Test
  public void containsAllQualityViolationsSortedByProfitAndRemediationCosts() {
    QualityInvestmentPlan qualityInvestmentPlan = investmentPlanService.computeInvestmentPlan(analysis, "", 80);
    QualityInvestmentPlanEntry[] investments = qualityInvestmentPlan.getEntries().toArray(new QualityInvestmentPlanEntry[0]);
    assertThat(investments).hasSize(3);
    assertThat(investments[0].getArtefact()).isEqualTo("C");
    assertThat(investments[1].getArtefact()).isEqualTo("A");
    assertThat(investments[2].getArtefact()).isEqualTo("B");
  }
}
