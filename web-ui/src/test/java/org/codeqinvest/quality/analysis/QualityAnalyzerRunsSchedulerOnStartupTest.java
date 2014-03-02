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
package org.codeqinvest.quality.analysis;

import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QualityAnalyzerRunsSchedulerOnStartupTest {

  private QualityAnalyzerRunsSchedulerOnStartup analyzerRunsSchedulerOnStartup;
  private QualityAnalyzerScheduler analyzerScheduler;
  private ProjectRepository projectRepository;

  private Project projectA;
  private Project projectB;
  private Project projectC;

  @Before
  public void setUpMockedSystem() {
    analyzerScheduler = mock(QualityAnalyzerScheduler.class);

    projectA = mock(Project.class);
    projectB = mock(Project.class);
    projectC = mock(Project.class);
    projectRepository = mock(ProjectRepository.class);
    when(projectRepository.findAll()).thenReturn(Arrays.asList(projectA, projectB, projectC));

    analyzerRunsSchedulerOnStartup = new QualityAnalyzerRunsSchedulerOnStartup(analyzerScheduler, projectRepository);
  }

  @Test
  public void scheduleAllProjects() {
    analyzerRunsSchedulerOnStartup.onApplicationEvent(null);
    verify(analyzerScheduler).scheduleAnalyzer(projectA);
    verify(analyzerScheduler).scheduleAnalyzer(projectB);
    verify(analyzerScheduler).scheduleAnalyzer(projectC);
  }

  @Test
  public void preventSchedulingProjectsTwice() {
    analyzerRunsSchedulerOnStartup.onApplicationEvent(null);
    analyzerRunsSchedulerOnStartup.onApplicationEvent(null);
    verify(analyzerScheduler, times(1)).scheduleAnalyzer(projectA);
    verify(analyzerScheduler, times(1)).scheduleAnalyzer(projectB);
    verify(analyzerScheduler, times(1)).scheduleAnalyzer(projectC);
  }
}
