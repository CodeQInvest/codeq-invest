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
    log.info("Calculate secure change probability for artefact {}", artefact.getName());
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
