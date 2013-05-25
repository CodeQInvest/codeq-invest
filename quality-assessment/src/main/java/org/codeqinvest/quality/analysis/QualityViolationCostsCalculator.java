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

import org.codeqinvest.sonar.MetricCollectorService;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fmueller
 */
@Service
class QualityViolationCostsCalculator {

  private final MetricCollectorService metricCollectorService;

  @Autowired
  QualityViolationCostsCalculator(MetricCollectorService metricCollectorService) {
    this.metricCollectorService = metricCollectorService;
  }

  public int calculateRemediationCosts(SonarConnectionSettings sonarConnectionSettings, ViolationOccurence violation) throws ResourceNotFoundException {
    return calculateCosts(sonarConnectionSettings, violation, violation.getRequirement().getRemediationCosts());
  }

  public int calculateNonRemediationCosts(SonarConnectionSettings sonarConnectionSettings, ViolationOccurence violation) throws ResourceNotFoundException {
    return calculateCosts(sonarConnectionSettings, violation, violation.getRequirement().getNonRemediationCosts());
  }

  private int calculateCosts(SonarConnectionSettings sonarConnectionSettings, ViolationOccurence violation, int costs) throws ResourceNotFoundException {
    double metricDistance = calculateMetricDistance(sonarConnectionSettings, violation);
    double weightingMetricValue = calculatedWeightingMetricValue(sonarConnectionSettings, violation);
    return (int) Math.round(costs * metricDistance * weightingMetricValue);
  }

  private double calculateMetricDistance(SonarConnectionSettings sonarConnectionSettings, ViolationOccurence violation) throws ResourceNotFoundException {
    double currentMetricValue = metricCollectorService.collectMetricForResource(sonarConnectionSettings,
        violation.getSonarIdentifierOfArtefact(), violation.getRequirement().getMetricIdentifier());
    double metricDistance = Math.abs(violation.getRequirement().getThreshold() - currentMetricValue);
    String operator = violation.getRequirement().getOperator();
    return operator.equals("<") || operator.equals(">") ? metricDistance + 1 : metricDistance;
  }

  private double calculatedWeightingMetricValue(SonarConnectionSettings sonarConnectionSettings, ViolationOccurence violation) throws ResourceNotFoundException {
    return metricCollectorService.collectMetricForResource(sonarConnectionSettings,
        violation.getSonarIdentifierOfArtefact(), violation.getRequirement().getWeightingMetricIdentifier()) / violation.getRequirement().getWeightingMetricValue();
  }
}
