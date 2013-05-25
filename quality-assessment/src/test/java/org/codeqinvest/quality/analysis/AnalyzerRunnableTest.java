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

import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalyzerRunnableTest {

  private AnalyzerRunnable analyzerRunnable;

  private Project project;
  private ProjectRepository projectRepository;
  private QualityAnalyzerService analyzerService;

  @Before
  public void setUpMockedSystem() {
    project = mock(Project.class);
    when(project.getId()).thenReturn(1L);
    projectRepository = mock(ProjectRepository.class);
    when(projectRepository.findOne(1L)).thenReturn(project);
    analyzerService = mock(QualityAnalyzerService.class);
    analyzerRunnable = new AnalyzerRunnable(project, projectRepository, analyzerService);
  }

  @Test
  public void retrieveProjectWithRepositoryFromDatabaseAndTriggerAnalysisAfterwards() {
    analyzerRunnable.run();
    InOrder inOrder = inOrder(projectRepository, analyzerService);
    inOrder.verify(projectRepository).findOne(1L);
    inOrder.verify(analyzerService).analyzeProject(project);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void notFailWhenProjectIsNotRetrievableViaRepository() {
    when(projectRepository.findOne(1L)).thenReturn(null);
    analyzerRunnable.run();
  }
}
