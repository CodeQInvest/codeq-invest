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
package org.codeqinvest.web.investment;

import com.google.common.collect.Sets;
import org.codeqinvest.investment.QualityInvestmentPlan;
import org.codeqinvest.investment.QualityInvestmentPlanEntry;
import org.codeqinvest.investment.QualityInvestmentPlanService;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.analysis.LastQualityAnalysisService;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QualityInvestmentPlanControllerTest {

  private MockMvc mockMvc;

  private ProjectRepository projectRepository;
  private LastQualityAnalysisService lastQualityAnalysisService;
  private QualityInvestmentPlanService investmentPlanService;

  @Before
  public void setUpMockedSystem() {
    projectRepository = mock(ProjectRepository.class);
    lastQualityAnalysisService = mock(LastQualityAnalysisService.class);
    investmentPlanService = mock(QualityInvestmentPlanService.class);

    QualityInvestmentPlan dummyInvestmentPlan = new QualityInvestmentPlan("", 90, 110, 122, Sets.<QualityInvestmentPlanEntry>newTreeSet());

    Project mockedProject = mock(Project.class);
    QualityAnalysis mockedAnalysis = mock(QualityAnalysis.class);

    when(projectRepository.findOne(1L)).thenReturn(mockedProject);
    when(lastQualityAnalysisService.retrieveLastAnalysis(mockedProject)).thenReturn(mockedAnalysis);
    when(investmentPlanService.computeInvestmentPlan(eq(mockedAnalysis), anyString(), anyInt())).thenReturn(dummyInvestmentPlan);

    mockMvc = MockMvcBuilders.standaloneSetup(new QualityInvestmentPlanController(projectRepository,
        lastQualityAnalysisService,
        investmentPlanService,
        new InvestmentAmountParser(),
        new InvestmentPlanRequestValidator(new InvestmentAmountParser()),
        mock(RequirementCodeConverter.class))).build();
  }

  @Test
  public void shouldReturnTheCalculatedQualityInvestmentPlan() throws Exception {
    mockMvc.perform(put("/projects/1/investment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"basePackage\": \"\", \"investment\": \"1h 30m\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.basePackage").value(""))
        .andExpect(jsonPath("$.investmentInMinutes").value(90))
        .andExpect(jsonPath("$.profitInMinutes").value(110))
        .andExpect(jsonPath("$.roi").value(122));
  }

  @Test
  public void jsonShouldBeValid() throws Exception {
    mockMvc.perform(put("/projects/1/investment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("nojson"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void basePackageShouldBeValidString() throws Exception {
    mockMvc.perform(put("/projects/1/investment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"basePackage\": null, \"investment\": \"1h 30m\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void investmentAmountShouldBeValid() throws Exception {
    mockMvc.perform(put("/projects/1/investment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"basePackage\": \"\", \"investment\": \"abc\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void projectShouldExists() throws Exception {
    mockMvc.perform(put("/projects/123/investment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"basePackage\": \"\", \"investment\": \"1h\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void projectShouldHaveLastSuccessfulAnalysis() throws Exception {
    when(lastQualityAnalysisService.retrieveLastAnalysis(any(Project.class))).thenReturn(null);
    mockMvc.perform(put("/projects/1/investment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"basePackage\": \"\", \"investment\": \"1h\"}"))
        .andExpect(status().isBadRequest());
  }
}
