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

import com.google.common.collect.Maps;
import org.codeqinvest.sonar.MetricCollectorService;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;

import java.util.HashMap;
import java.util.Map;

class FakeMetricCollectorService extends MetricCollectorService {

  private final Map<String, Map<String, Double>> metricValues = Maps.newHashMap();

  public void addMetricValue(String resourceKey, String metricIdentifier, double metricValue) {
    if (!metricValues.containsKey(resourceKey)) {
      metricValues.put(resourceKey, new HashMap<String, Double>());
    }
    metricValues.get(resourceKey).put(metricIdentifier, metricValue);
  }

  @Override
  public double collectMetricForResource(SonarConnectionSettings connectionSettings, String resourceKey, String metricIdentifier) throws ResourceNotFoundException {
    if (!metricValues.containsKey(resourceKey) || !metricValues.get(resourceKey).containsKey(metricIdentifier)) {
      throw new ResourceNotFoundException();
    }
    return metricValues.get(resourceKey).get(metricIdentifier);
  }
}
