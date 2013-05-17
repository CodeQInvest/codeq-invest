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

import org.codeqinvest.project.Project;
import org.codeqinvest.sonar.ResourcesCollectorService;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QualityAnalyzerServiceTest {

  @Test(expected = ConnectionException.class)
  public void shouldFailWithConnectionExceptionWhenSonarProjectIsNotReachable() throws ConnectionException {
    SonarConnectionCheckerService sonarConnectionCheckerService = mock(SonarConnectionCheckerService.class);
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(false);

    QualityAnalyzerService qualityAnalyzerService = new QualityAnalyzerService(sonarConnectionCheckerService, new ResourcesCollectorService());
    qualityAnalyzerService.analyzeProject(mock(Project.class));
  }
}
