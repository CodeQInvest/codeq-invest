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

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityCriteria;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.QualityRequirement;
import org.codeqinvest.sonar.MetricCollectorService;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.ResourcesCollectorService;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.sonar.wsclient.services.Resource;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViolationsCalculatorServiceTest {

  private final String defaultSonarHost = "http://localhost";

  private QualityProfile profile;
  private QualityRequirement firstRequirement;
  private QualityRequirement secondRequirement;
  private Project project;

  private SonarConnectionCheckerService connectionCheckerService;

  @Before
  public void setUpQualityProfileAndProjectForAnalysisRuns() {
    profile = new QualityProfile();
    firstRequirement = new QualityRequirement(profile, 100, 200, 10, "nloc", new QualityCriteria("cc", ">", 10));
    secondRequirement = new QualityRequirement(profile, 80, 300, 10, "nloc", new QualityCriteria("ec", "<", 15));
    profile.addRequirement(firstRequirement);
    profile.addRequirement(secondRequirement);

    SonarConnectionSettings sonarConnectionSettings = new SonarConnectionSettings(defaultSonarHost, "myProject::123");
    ScmConnectionSettings scmConnectionSettings = new ScmConnectionSettings("http://svn.localhost");
    project = new Project("myProject", "0 0 * * *", profile, sonarConnectionSettings, scmConnectionSettings, CodeChangeSettings.defaultSetting(1));

    connectionCheckerService = mock(SonarConnectionCheckerService.class);
    when(connectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(true);
  }

  @Test
  public void failedViolationAnalysisResultWhenSonarProjectIsNotReachable() {
    SonarConnectionSettings connectionSettings = new SonarConnectionSettings(defaultSonarHost, "abc");
    Project project = mock(Project.class);
    when(project.getSonarConnectionSettings()).thenReturn(connectionSettings);
    SonarConnectionCheckerService connectionCheckerService = mock(SonarConnectionCheckerService.class);
    when(connectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(false);

    ViolationsCalculatorService violationsCalculatorService = new ViolationsCalculatorService(connectionCheckerService,
        new ResourcesCollectorService(), new MetricCollectorService());
    assertThat(violationsCalculatorService.calculateAllViolation(project).isSuccessful()).isFalse();
  }

  @Test
  public void failedViolationAnalysisResultWhenOneMetricValueIsNotReachable() throws ResourceNotFoundException {
    Resource dummyResource = new Resource();
    ResourcesCollectorService resourcesCollectorService = mock(ResourcesCollectorService.class);
    when(resourcesCollectorService.collectAllResourcesForProject(any(SonarConnectionSettings.class)))
        .thenReturn(Arrays.asList(dummyResource));

    MetricCollectorService metricCollectorService = mock(MetricCollectorService.class);
    when(metricCollectorService.collectMetricForResource(any(SonarConnectionSettings.class), anyString(), anyString()))
        .thenThrow(ResourceNotFoundException.class);

    ViolationsCalculatorService violationsCalculatorService = new ViolationsCalculatorService(connectionCheckerService,
        resourcesCollectorService, metricCollectorService);
    assertThat(violationsCalculatorService.calculateAllViolation(project).isSuccessful()).isFalse();
  }

  @Test
  public void noViolationsWhenNoRequirementsAreViolated() {
    FakeResourcesCollectorService resourcesCollectorService = new FakeResourcesCollectorService();
    resourcesCollectorService.addResource("A");
    resourcesCollectorService.addResource("B");

    FakeMetricCollectorService metricCollectorService = new FakeMetricCollectorService();
    metricCollectorService.addMetricValue("A", "cc", 11.0);
    metricCollectorService.addMetricValue("A", "ec", 14.0);
    metricCollectorService.addMetricValue("B", "cc", 20.0);
    metricCollectorService.addMetricValue("B", "ec", 2.0);

    ViolationsCalculatorService violationsCalculatorService = new ViolationsCalculatorService(connectionCheckerService,
        resourcesCollectorService, metricCollectorService);

    ViolationsAnalysisResult analysisResult = violationsCalculatorService.calculateAllViolation(project);
    assertThat(analysisResult.isSuccessful()).isTrue();
    assertThat(analysisResult.getViolations()).isEmpty();
  }

  @Test
  public void violationsWhenRequirementsAreViolatedInOneArtefact() {
    FakeResourcesCollectorService resourcesCollectorService = new FakeResourcesCollectorService();
    resourcesCollectorService.addResource("A");
    resourcesCollectorService.addResource("B");

    FakeMetricCollectorService metricCollectorService = new FakeMetricCollectorService();
    metricCollectorService.addMetricValue("A", "cc", 11.0);
    metricCollectorService.addMetricValue("A", "ec", 14.0);
    metricCollectorService.addMetricValue("B", "cc", 10.0);
    metricCollectorService.addMetricValue("B", "ec", 15.0);

    ViolationsCalculatorService violationsCalculatorService = new ViolationsCalculatorService(connectionCheckerService,
        resourcesCollectorService, metricCollectorService);

    ViolationsAnalysisResult analysisResult = violationsCalculatorService.calculateAllViolation(project);
    assertThat(analysisResult.isSuccessful()).isTrue();
    assertThat(analysisResult.getViolations()).containsOnly(
        new ViolationOccurence(firstRequirement, new Artefact("B", "B")),
        new ViolationOccurence(secondRequirement, new Artefact("B", "B"))
    );
  }

  @Test
  public void violationsWhenRequirementsAreViolatedInManyArtefact() {
    FakeResourcesCollectorService resourcesCollectorService = new FakeResourcesCollectorService();
    resourcesCollectorService.addResource("A");
    resourcesCollectorService.addResource("B");
    resourcesCollectorService.addResource("C");

    FakeMetricCollectorService metricCollectorService = new FakeMetricCollectorService();
    metricCollectorService.addMetricValue("A", "cc", 9.0);
    metricCollectorService.addMetricValue("A", "ec", 20.0);
    metricCollectorService.addMetricValue("B", "cc", 10.0);
    metricCollectorService.addMetricValue("B", "ec", 15.0);
    metricCollectorService.addMetricValue("C", "cc", 11.0);
    metricCollectorService.addMetricValue("C", "ec", 14.0);

    ViolationsCalculatorService violationsCalculatorService = new ViolationsCalculatorService(connectionCheckerService,
        resourcesCollectorService, metricCollectorService);

    ViolationsAnalysisResult analysisResult = violationsCalculatorService.calculateAllViolation(project);
    assertThat(analysisResult.isSuccessful()).isTrue();
    assertThat(analysisResult.getViolations()).containsOnly(
        new ViolationOccurence(firstRequirement, new Artefact("A", "A")),
        new ViolationOccurence(secondRequirement, new Artefact("A", "A")),
        new ViolationOccurence(firstRequirement, new Artefact("B", "B")),
        new ViolationOccurence(secondRequirement, new Artefact("B", "B"))
    );
  }
}
