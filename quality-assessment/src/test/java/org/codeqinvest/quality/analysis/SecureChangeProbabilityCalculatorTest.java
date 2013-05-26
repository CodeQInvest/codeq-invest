package org.codeqinvest.quality.analysis;

import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.ChangeRiskAssessmentFunction;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.RiskCharge;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SecureChangeProbabilityCalculatorTest {

  private SecureChangeProbabilityCalculator secureChangeProbabilityCalculator;
  private FakeMetricCollectorService metricCollectorService;

  private Artefact artefact;

  @Before
  public void setUpMockedSystem() {
    metricCollectorService = new FakeMetricCollectorService();
    secureChangeProbabilityCalculator = new SecureChangeProbabilityCalculator(metricCollectorService);
    artefact = new Artefact("A", "A");
  }

  @Test
  public void profileWithNoChangeRiskAssessmentFunctions() throws ResourceNotFoundException {
    assertThat(secureChangeProbabilityCalculator.calculateSecureChangeProbability(
        new QualityProfile(), mock(SonarConnectionSettings.class), mock(Artefact.class))).isEqualTo(1.0);
  }

  @Test
  public void profileWithOneChangeRiskAssessmentFunction() throws ResourceNotFoundException {
    metricCollectorService.addMetricValue("A", "metric", 9.0);

    QualityProfile profile = new QualityProfile();
    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric", Arrays.asList(new RiskCharge(0.2, "<", 10.0))));

    assertThat(secureChangeProbabilityCalculator.calculateSecureChangeProbability(
        profile, mock(SonarConnectionSettings.class), artefact)).isEqualTo(1.2);
  }

  @Test
  public void profileWithManyChangeRiskAssessmentFunctions() throws ResourceNotFoundException {
    metricCollectorService.addMetricValue("A", "metric1", 9.0);
    metricCollectorService.addMetricValue("A", "metric2", -1.0);
    metricCollectorService.addMetricValue("A", "metric3", 0.0);

    QualityProfile profile = new QualityProfile();
    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric1", Arrays.asList(new RiskCharge(0.2, "<", 10.0))));
    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric2", Arrays.asList(new RiskCharge(0.11, ">", -2.0))));
    profile.addChangeRiskAssessmentFunction(new ChangeRiskAssessmentFunction(profile, "metric3", Arrays.asList(new RiskCharge(0.005, ">=", 0.0))));

    assertThat(secureChangeProbabilityCalculator.calculateSecureChangeProbability(
        profile, mock(SonarConnectionSettings.class), artefact)).isEqualTo(1.315);
  }
}
