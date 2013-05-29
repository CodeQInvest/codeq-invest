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
package org.codeqinvest.web.sonar;

import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SonarControllerTest {

  private MockMvc mockMvc;

  private SonarConnectionCheckerService sonarConnectionCheckerService;

  @Before
  public void setUp() {
    sonarConnectionCheckerService = mock(SonarConnectionCheckerService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new SonarController(sonarConnectionCheckerService, new SonarServerValidator())).build();
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
  public void badRequestWhenUrlParameterIsMalformed() throws Exception {
    mockMvc.perform(put("/sonar/reachable")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"url\": \"localhost\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void badRequestWhenUrlParameterIsMissed() throws Exception {
    mockMvc.perform(put("/sonar/reachable").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
  }
}
