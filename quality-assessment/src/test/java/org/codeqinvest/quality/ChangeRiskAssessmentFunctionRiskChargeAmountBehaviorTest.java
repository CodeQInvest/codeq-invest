/*
 * Copyright 2013 - 2014 Felix Müller
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
package org.codeqinvest.quality;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ChangeRiskAssessmentFunctionRiskChargeAmountBehaviorTest {

  private final QualityProfile dummyProfile = new QualityProfile("quality-profile");

  private Set<RiskCharge> riskCharges;
  private double currentMetricValue;
  private double expectedRiskChargeAmount;

  public ChangeRiskAssessmentFunctionRiskChargeAmountBehaviorTest(List<RiskCharge> riskCharges, double currentMetricValue, double expectedRiskChargeAmount) {
    this.riskCharges = new HashSet<RiskCharge>();
    this.riskCharges.addAll(riskCharges);
    this.currentMetricValue = currentMetricValue;
    this.expectedRiskChargeAmount = expectedRiskChargeAmount;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    Object[][] testData = new Object[][]{
        {Arrays.asList(new RiskCharge(1.0, "<", 10.0)), 9.999, 1.0},
        {Arrays.asList(new RiskCharge(1.0, "<", 10.0)), 10.0, 0.0},
        {Arrays.asList(new RiskCharge(1.0, "<", 10.0), new RiskCharge(2.0, "<", 5.0)), 4.999, 2.0},
        {Arrays.asList(new RiskCharge(1.0, "<", 10.0), new RiskCharge(2.0, "<", 5.0), new RiskCharge(3.0, "<", 2.0)), 1.999, 3.0},
        {Arrays.asList(new RiskCharge(1.0, "<", -10.0), new RiskCharge(2.0, "<", -12.0)), -12.0001, 2.0},
        {Arrays.asList(new RiskCharge(1.0, ">", 10.0)), 10.001, 1.0},
        {Arrays.asList(new RiskCharge(1.0, ">", 10.0)), 10.0, 0.0},
        {Arrays.asList(new RiskCharge(1.0, ">", 10.0), new RiskCharge(2.0, ">", 15.0)), 15.001, 2.0},
        {Arrays.asList(new RiskCharge(1.0, ">", 10.0), new RiskCharge(2.0, ">", 15.0), new RiskCharge(3.0, ">", 20.0)), 20.001, 3.0},
        {Arrays.asList(new RiskCharge(1.0, ">", -20.0), new RiskCharge(2.0, ">", -10.0)), -9.999, 2.0},
        {Arrays.asList(new RiskCharge(1.0, "<=", 10.0), new RiskCharge(2.0, "<=", 5.0), new RiskCharge(3.0, "<=", 2.0)), 2.0, 3.0},
        {Arrays.asList(new RiskCharge(1.0, "<=", 10.0), new RiskCharge(3.0, "<=", 2.0), new RiskCharge(2.0, "<=", 5.0)), 2.001, 2.0},
        {Arrays.asList(new RiskCharge(1.0, ">=", 10.0), new RiskCharge(2.0, ">=", 15.0), new RiskCharge(3.0, ">=", 20.0)), 20.0, 3.0},
        {Arrays.asList(new RiskCharge(1.0, ">=", 10.0), new RiskCharge(3.0, ">=", 20.0), new RiskCharge(2.0, ">=", 15.0)), 19.999, 2.0}
    };
    return Arrays.asList(testData);
  }

  @Test
  public void testCalculationOfChargedRiskAmount() {
    ChangeRiskAssessmentFunction changeRiskAssessmentFunction = new ChangeRiskAssessmentFunction(dummyProfile, "", riskCharges);
    assertThat(changeRiskAssessmentFunction.getRiskChargeAmount(currentMetricValue))
        .as("The risk charge for metric value " + currentMetricValue
            + " should be " + expectedRiskChargeAmount + " with risk assessment function " + changeRiskAssessmentFunction)
        .isEqualTo(expectedRiskChargeAmount);
  }
}
