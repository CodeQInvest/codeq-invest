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
package org.codeqinvest.web.quality.analysis;

import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.analysis.QualityAnalyzerScheduler;
import org.codeqinvest.quality.repository.ProjectRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class QualityAnalysisControllerTest {

  private MockMvc mockMvc;

  private QualityAnalyzerScheduler qualityAnalyzerScheduler;
  private ProjectRepository projectRepository;

  private Project dummyProject;

  @Before
  public void setUp() {
    dummyProject = mock(Project.class);
    qualityAnalyzerScheduler = mock(QualityAnalyzerScheduler.class);
    projectRepository = mock(ProjectRepository.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new QualityAnalysisController(qualityAnalyzerScheduler, projectRepository)).build();
  }

  @Test
  public void shouldCallSchedulerWhenSubmittingNewAnalysisRun() throws Exception {
    when(projectRepository.findOne(anyLong())).thenReturn(dummyProject);
    mockMvc.perform(post("/projects/1/analyze"));
    verify(qualityAnalyzerScheduler).executeAnalyzer(dummyProject);
  }

  @Test
  public void shouldNotExecuteAnalyzerWhenProjectDoesNotExist() throws Exception {
    when(projectRepository.findOne(anyLong())).thenReturn(null);
    mockMvc.perform(post("/projects/1/analyze"));
    verify(qualityAnalyzerScheduler, never()).executeAnalyzer(any(Project.class));
  }
}
