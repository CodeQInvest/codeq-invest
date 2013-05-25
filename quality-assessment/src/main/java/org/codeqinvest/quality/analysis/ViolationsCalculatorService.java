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
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityRequirement;
import org.codeqinvest.sonar.MetricCollectorService;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.ResourcesCollectorService;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.sonar.wsclient.services.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is a helper service that is only used internally
 * to calculate all quality violations for a given project. For that,
 * it collects all the necessary data from Sonar. Before it performs
 * these steps, it checks for the availability of the given
 * Sonar server instance.
 *
 * @author fmueller
 */
@Slf4j
@Service
class ViolationsCalculatorService {

  private final SonarConnectionCheckerService sonarConnectionCheckerService;
  private final ResourcesCollectorService resourcesCollectorService;
  private final MetricCollectorService metricCollectorService;

  @Autowired
  public ViolationsCalculatorService(SonarConnectionCheckerService sonarConnectionCheckerService,
                                     ResourcesCollectorService resourcesCollectorService,
                                     MetricCollectorService metricCollectorService) {
    this.sonarConnectionCheckerService = sonarConnectionCheckerService;
    this.resourcesCollectorService = resourcesCollectorService;
    this.metricCollectorService = metricCollectorService;
  }

  ViolationsAnalysisResult calculateAllViolation(Project project) {
    if (!sonarConnectionCheckerService.isReachable(project.getSonarConnectionSettings())) {
      return ViolationsAnalysisResult.createFailedAnalysis(Collections.<ViolationOccurence>emptyList(),
          "sonar project is not reachable with supplied connection settings: " + project.getSonarConnectionSettings().toString());
    }

    Map<String, Artefact> artefactsThatHaveAtLeastOneViolation = Maps.newHashMap();
    List<ViolationOccurence> violations = new ArrayList<ViolationOccurence>();
    for (Resource resource : resourcesCollectorService.collectAllResourcesForProject(project.getSonarConnectionSettings())) {
      for (QualityRequirement qualityRequirement : project.getProfile().getRequirements()) {

        final double metricValue;
        try {
          metricValue = metricCollectorService.collectMetricForResource(project.getSonarConnectionSettings(),
              resource.getKey(), qualityRequirement.getCriteria().getMetricIdentifier());
        } catch (ResourceNotFoundException e) {
          log.warn("Quality analysis run failed due one resource or metric could not be find in Sonar!", e);
          return ViolationsAnalysisResult.createFailedAnalysis(violations, "resource " + resource.getKey()
              + " or metric " + qualityRequirement.getCriteria().getMetricIdentifier() + " not available on Sonar");
        }

        if (qualityRequirement.isViolated(metricValue)) {
          final Artefact artefact;
          if (artefactsThatHaveAtLeastOneViolation.containsKey(resource.getKey())) {
            artefact = artefactsThatHaveAtLeastOneViolation.get(resource.getKey());
          } else {
            artefact = new Artefact(resource.getLongName(), resource.getKey());
            artefactsThatHaveAtLeastOneViolation.put(resource.getKey(), artefact);
          }

          log.debug("Create quality violation for artefact {} with violated requirement {}",
              artefact.getName(), qualityRequirement.getCriteria());
          violations.add(new ViolationOccurence(qualityRequirement, artefact));
        }
      }
    }

    log.info("Successfully analysed project {} and found {} quality violations in {} artefacts",
        project.getName(), violations.size(), artefactsThatHaveAtLeastOneViolation.size());
    return ViolationsAnalysisResult.createSuccessfulAnalysis(violations);
  }
}
