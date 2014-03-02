/*
 * Copyright 2013 - 2014 Felix Müller
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
package org.codeqinvest.web.sonar;

import com.google.common.collect.Sets;
import org.codeqinvest.sonar.ProjectInformation;
import org.codeqinvest.sonar.ProjectsCollectorService;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SonarControllerTest {

  private MockMvc mockMvc;

  private SonarConnectionCheckerService sonarConnectionCheckerService;
  private ProjectsCollectorService projectsCollectorService;

  @Before
  public void setUp() {
    sonarConnectionCheckerService = mock(SonarConnectionCheckerService.class);
    projectsCollectorService = mock(ProjectsCollectorService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new SonarController(sonarConnectionCheckerService, projectsCollectorService, new SonarServerValidator())).build();
  }

  @Test
  public void shouldReturnTrueWhenGivenSonarIsReachable() throws Exception {
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(true);
    mockMvc.perform(put("/sonar/reachable")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"url\": \"http://localhost\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(true));
  }

  @Test
  public void shouldReturnFalseWhenGivenSonarIsNotReachable() throws Exception {
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(false);
    mockMvc.perform(put("/sonar/reachable")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"url\": \"http://localhost\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(false));
  }

  @Test
  public void badRequestWhenUrlParameterIsMalformedForReachableRoute() throws Exception {
    mockMvc.perform(put("/sonar/reachable")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"url\": \"localhost\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void badRequestWhenUrlParameterIsMissedForReachableRoute() throws Exception {
    mockMvc.perform(put("/sonar/reachable").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnAllProjectOfGivenSonar() throws Exception {
    when(projectsCollectorService.collectAllProjects(any(SonarConnectionSettings.class)))
        .thenReturn(Sets.newHashSet(new ProjectInformation("A", "A-Key"), new ProjectInformation("B", "B-Key")));

    mockMvc.perform(put("/sonar/projects")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"url\": \"http://localhost\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void shouldReturnNameAndResourceKeyOfSonarProject() throws Exception {
    when(projectsCollectorService.collectAllProjects(any(SonarConnectionSettings.class)))
        .thenReturn(Sets.newHashSet(new ProjectInformation("A", "A-Key")));

    mockMvc.perform(put("/sonar/projects")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"url\": \"http://localhost\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("A"))
        .andExpect(jsonPath("$[0].resourceKey").value("A-Key"));
  }

  @Test
  public void badRequestWhenUrlParameterIsMalformedForProjectsRoute() throws Exception {
    mockMvc.perform(put("/sonar/projects")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"url\": \"localhost\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void badRequestWhenUrlParameterIsMissedForProjectsRoute() throws Exception {
    mockMvc.perform(put("/sonar/projects").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
  }
}
