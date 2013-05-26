package org.codeqinvest.quality.analysis;

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.ChangeRiskAssessmentFunction;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.sonar.MetricCollectorService;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fmueller
 */
@Slf4j
@Service
class SecureChangeProbabilityCalculator {

  private final MetricCollectorService metricCollectorService;

  @Autowired
  SecureChangeProbabilityCalculator(MetricCollectorService metricCollectorService) {
    this.metricCollectorService = metricCollectorService;
  }

  public double calculateSecureChangeProbability(QualityProfile qualityProfile, SonarConnectionSettings sonarConnectionSettings, Artefact artefact) throws ResourceNotFoundException {
    double secureChangeProbability = 1.0;
    for (ChangeRiskAssessmentFunction riskAssessmentFunction : qualityProfile.getChangeRiskAssessmentFunctions()) {
      final double metricValueForArtefact = metricCollectorService.collectMetricForResource(sonarConnectionSettings,
          artefact.getSonarIdentifier(),
          riskAssessmentFunction.getMetricIdentifier());
      secureChangeProbability += riskAssessmentFunction.getRiskChargeAmount(metricValueForArtefact);
    }
    return secureChangeProbability;
  }
}
