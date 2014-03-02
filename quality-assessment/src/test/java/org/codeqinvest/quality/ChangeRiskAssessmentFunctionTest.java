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

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChangeRiskAssessmentFunctionTest {

  private final QualityProfile qualityProfile = new QualityProfile("quality-profile");

  private Set<RiskCharge> riskCharges;

  @Before
  public void setUp() {
    riskCharges = new HashSet<RiskCharge>();
  }

  @Test(expected = IllegalArgumentException.class)
  public void allRiskChargesShouldHaveTheSameOperator() {
    riskCharges.add(new RiskCharge(0.0, "<", 0.0));
    riskCharges.add(new RiskCharge(0.0, "<", 0.0));
    riskCharges.add(new RiskCharge(0.0, ">", 0.0));
    new ChangeRiskAssessmentFunction(qualityProfile, "", riskCharges);
  }

  @Test(expected = IllegalArgumentException.class)
  public void allRiskChargesShouldHaveDifferentThresholds() {
    riskCharges.add(new RiskCharge(0.0, "<", 0.0));
    riskCharges.add(new RiskCharge(0.0, "<", 1.0));
    riskCharges.add(new RiskCharge(1.0, "<", 0.0));
    new ChangeRiskAssessmentFunction(qualityProfile, "", riskCharges);
  }

  @Test(expected = IllegalArgumentException.class)
  public void riskChargesMustNotBeEmpty() {
    new ChangeRiskAssessmentFunction(qualityProfile, "", Collections.<RiskCharge>emptySet());
  }

  @Test(expected = IllegalArgumentException.class)
  public void equalOperatorShouldNotBeAllowedInRiskCharges() {
    riskCharges.add(new RiskCharge(0.0, "=", 0.0));
    new ChangeRiskAssessmentFunction(qualityProfile, "", riskCharges);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEqualOperatorShouldNotBeAllowedInRiskCharges() {
    riskCharges.add(new RiskCharge(0.0, "!=", 0.0));
    new ChangeRiskAssessmentFunction(qualityProfile, "", riskCharges);
  }
}
