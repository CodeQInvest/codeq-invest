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

import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.QualityCriteria;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.QualityRequirement;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class QualityViolationCostsCalculatorTest {

  private final QualityProfile qualityProfile = new QualityProfile("quality-profile");

  private QualityViolationCostsCalculator costsCalculator;
  private FakeMetricCollectorService metricCollectorService;
  private SonarConnectionSettings connectionSettings;
  private Artefact artefact;

  @Before
  public void setUp() {
    artefact = new Artefact("A", "A");
    metricCollectorService = new FakeMetricCollectorService();
    costsCalculator = new QualityViolationCostsCalculator(metricCollectorService);
    connectionSettings = mock(SonarConnectionSettings.class);

    metricCollectorService.addMetricValue("A", "metric", 2.0);
    metricCollectorService.addMetricValue("A", "nloc", 120.0);
  }

  @Test
  public void calculateRemediationCostsProperlyForGreaterOperator() throws ResourceNotFoundException {
    QualityRequirement requirement = new QualityRequirement(qualityProfile, 20, 30, 100, "nloc", new QualityCriteria("metric", ">", 10.0));
    ViolationOccurence violation = new ViolationOccurence(requirement, artefact);

    // 20 * (abs(10.0 - 2.0) + 1) * (120.0 / 100.0) = 216 min
    assertThat(costsCalculator.calculateRemediationCosts(connectionSettings, violation)).isEqualTo(216);
  }

  @Test
  public void calculateRemediationCostsProperlyForLessOperator() throws ResourceNotFoundException {
    QualityRequirement requirement = new QualityRequirement(qualityProfile, 20, 30, 100, "nloc", new QualityCriteria("metric", "<", 1.0));
    ViolationOccurence violation = new ViolationOccurence(requirement, artefact);

    // 20 * (abs(1.0 - 2.0) + 1) * (120.0 / 100.0) = 48 min
    assertThat(costsCalculator.calculateRemediationCosts(connectionSettings, violation)).isEqualTo(48);
  }

  @Test
  public void calculateNonRemediationCostsProperlyForGreaterEqualsOperator() throws ResourceNotFoundException {
    QualityRequirement requirement = new QualityRequirement(qualityProfile, 20, 30, 100, "nloc", new QualityCriteria("metric", ">=", 5.0));
    ViolationOccurence violation = new ViolationOccurence(requirement, artefact);

    // 30 * abs(5.0 - 2.0) * (120.0 / 100.0) = 108 min
    assertThat(costsCalculator.calculateNonRemediationCosts(connectionSettings, violation)).isEqualTo(108);
  }
}
