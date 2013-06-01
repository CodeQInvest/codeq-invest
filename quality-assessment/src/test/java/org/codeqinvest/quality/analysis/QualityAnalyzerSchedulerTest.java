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
import org.codeqinvest.codechanges.scm.factory.ScmAvailabilityCheckerServiceFactory;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class QualityAnalyzerSchedulerTest {

  private QualityAnalyzerScheduler analyzerScheduler;
  private Project project;

  @Before
  public void setUpMockedSystem() {
    analyzerScheduler = new QualityAnalyzerScheduler(mock(ProjectRepository.class),
        mock(ViolationsCalculatorService.class),
        mock(ScmAvailabilityCheckerServiceFactory.class),
        mock(CodeChangeProbabilityCalculatorFactory.class),
        mock(SecureChangeProbabilityCalculator.class),
        mock(QualityViolationCostsCalculator.class),
        mock(QualityAnalysisRepository.class));
    project = new Project("MyProject", "* * 4 * * *", new QualityProfile("quality-profile"),
        mock(SonarConnectionSettings.class), mock(ScmConnectionSettings.class), CodeChangeSettings.defaultSetting(1));
    project.setId(1L);
  }

  @Test
  public void scheduleProjectForAnalysis() {
    assertThat(analyzerScheduler.scheduleAnalyzer(project)).isTrue();
  }

  @Test
  public void failToScheduleProjectTwice() {
    analyzerScheduler.scheduleAnalyzer(project);
    assertThat(analyzerScheduler.scheduleAnalyzer(project)).isFalse();
  }
}
