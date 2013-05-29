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
package org.codeqinvest.web.project;

import org.codeqinvest.codechanges.scm.ScmAvailabilityCheckerService;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.factory.ScmAvailabilityCheckerServiceFactory;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.ProjectRepository;
import org.codeqinvest.quality.QualityProfileRepository;
import org.codeqinvest.quality.analysis.QualityAnalyzerScheduler;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectControllerTest {

  private MockMvc mockMvc;

  private ProjectRepository projectRepository;
  private QualityAnalyzerScheduler analyzerScheduler;
  private ProjectConnectionsValidator projectConnectionsValidator;
  private SonarConnectionCheckerService sonarConnectionCheckerService;
  private ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory;

  @Before
  public void setUp() {
    projectRepository = mock(ProjectRepository.class);
    analyzerScheduler = mock(QualityAnalyzerScheduler.class);

    sonarConnectionCheckerService = mock(SonarConnectionCheckerService.class);
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(true);

    scmAvailabilityCheckerServiceFactory = mock(ScmAvailabilityCheckerServiceFactory.class);
    ScmAvailabilityCheckerService checkerService = mock(ScmAvailabilityCheckerService.class);
    when(checkerService.isAvailable(any(ScmConnectionSettings.class))).thenReturn(true);
    when(scmAvailabilityCheckerServiceFactory.create(any(ScmConnectionSettings.class))).thenReturn(checkerService);

    QualityProfileRepository profileRepository = mock(QualityProfileRepository.class);
    when(profileRepository.exists(anyLong())).thenReturn(true);
    ProjectValidator projectValidator = new ProjectValidator(projectRepository, profileRepository,
        new SonarConnectionSettingsValidator(), new ScmConnectionSettingsValidator(), new CodeChangeSettingsValidator());
    projectConnectionsValidator = new ProjectConnectionsValidator(projectValidator, sonarConnectionCheckerService, scmAvailabilityCheckerServiceFactory);

    mockMvc = MockMvcBuilders.standaloneSetup(new ProjectController(projectRepository, profileRepository, analyzerScheduler, projectConnectionsValidator)).build();
  }

  @Test
  public void showFormForCreatingNewProjectShouldReturnWithOkStatusCode() throws Exception {
    mockMvc.perform(get("/projects/create")).andExpect(status().isOk());
  }

  @Test
  public void addNewProjectWithValidParametersToDatabase() throws Exception {
    performValidCreateProjectRequest();
    verify(projectRepository).save(any(Project.class));
  }

  @Test
  public void notAddNewProjectToDatabaseWhenParametersAreNotValid() throws Exception {
    performInValidCreateProjectRequest();
    verify(projectRepository, never()).save(any(Project.class));
  }

  @Test
  public void scheduleCreatedProjectForAnalysis() throws Exception {
    performValidCreateProjectRequest();
    verify(analyzerScheduler).scheduleAnalyzer(any(Project.class));
  }

  private void performValidCreateProjectRequest() throws Exception {
    mockMvc.perform(post("/projects/create")
        .param("name", "MyProject")
        .param("profile.id", "1")
        .param("cronExpression", "* * * * * *")
        .param("sonarConnectionSettings.url", "http://localhost")
        .param("sonarConnectionSettings.project", "project")
        .param("scmSettings.url", "scm:svnhttp://svn.localhost")
        .param("codeChangeSettings.method", "1")
        .param("codeChangeSettings.days", "30"))
        .andExpect(status().isOk());
  }

  private void performInValidCreateProjectRequest() throws Exception {
    mockMvc.perform(post("/projects/create")
        .param("name", "MyProject")
        .param("profile.id", "1")
        .param("cronExpression", "* *"))
        .andExpect(status().isOk());
  }
}
