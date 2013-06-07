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
package org.codeqinvest.quality.analysis;

import com.google.common.collect.Sets;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.ChangeRiskAssessmentFunction;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.RiskCharge;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SecureChangeProbabilityCalculatorTest {

  private SecureChangeProbabilityCalculator secureChangeProbabilityCalculator;
  private FakeMetricCollectorService metricCollectorService;

  private QualityProfile profile;
  private Artefact artefact;

  @Before
  public void setUpMockedSystem() {
    metricCollectorService = new FakeMetricCollectorService();
    secureChangeProbabilityCalculator = new SecureChangeProbabilityCalculator(metricCollectorService);
    profile = new QualityProfile("quality-profile");
    artefact = new Artefact("A", "A");
  }

  @Test
  public void profileWithNoChangeRiskAssessmentFunctions() throws ResourceNotFoundException {
    assertThat(secureChangeProbabilityCalculator.calculateSecureChangeProbability(
        profile, mock(SonarConnectionSettings.class), mock(Artefact.class))).isEqualTo(1.0);
  }

  @Test
  public void profileWithOneChangeRiskAssessmentFunction() throws ResourceNotFoundException {
    metricCollectorService.addMetricValue("A", "metric", 9.0);

    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric", Sets.newHashSet(new RiskCharge(0.2, "<", 10.0))));

    assertThat(secureChangeProbabilityCalculator.calculateSecureChangeProbability(
        profile, mock(SonarConnectionSettings.class), artefact)).isEqualTo(1.2);
  }

  @Test
  public void profileWithManyChangeRiskAssessmentFunctions() throws ResourceNotFoundException {
    metricCollectorService.addMetricValue("A", "metric1", 9.0);
    metricCollectorService.addMetricValue("A", "metric2", -1.0);
    metricCollectorService.addMetricValue("A", "metric3", 0.0);

    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric1", Sets.newHashSet(new RiskCharge(0.2, "<", 10.0))));
    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric2", Sets.newHashSet(new RiskCharge(0.11, ">", -2.0))));
    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric3", Sets.newHashSet(new RiskCharge(0.005, ">=", 0.0))));

    assertThat(secureChangeProbabilityCalculator.calculateSecureChangeProbability(
        profile, mock(SonarConnectionSettings.class), artefact)).isEqualTo(1.315);
  }
}
