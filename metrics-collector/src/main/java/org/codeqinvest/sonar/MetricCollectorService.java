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
package org.codeqinvest.sonar;

import lombok.extern.slf4j.Slf4j;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import org.springframework.stereotype.Service;

/**
 * This service collects the value of a metric for a resource
 * that is managed by a given sonar server instance.
 *
 * @author fmueller
 */
@Slf4j
@Service
public class MetricCollectorService {

  static final double DEFAULT_VALUE = 0.0;

  public double collectMetricForResource(SonarConnectionSettings connectionSettings, String resourceKey, String metricIdentifier) throws ResourceNotFoundException {
    if (!connectionSettings.hasProject()) {
      throw new IllegalArgumentException("you can only collect metric value for a resource with connection settings that has a project");
    }
    Sonar sonar = new Sonar(new HttpClient4Connector(connectionSettings.asHostObject()));
    Resource resource = sonar.find(ResourceQuery.create(resourceKey).setMetrics(metricIdentifier));
    if (resource == null) {
      log.info("Could not find measurement for metric {} at resource {}", metricIdentifier, resourceKey);
      return DEFAULT_VALUE;
    }
    Double metricValue = resource.getMeasureValue(metricIdentifier);
    if (metricValue == null) {
      throw new ResourceNotFoundException("could not find metric with identifier: " + metricIdentifier);
    }
    return metricValue;
  }
}
