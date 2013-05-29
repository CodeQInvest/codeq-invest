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
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectConnectionsValidatorTest {

  private ProjectConnectionsValidator validator;

  private ProjectValidator projectValidator;
  private SonarConnectionCheckerService sonarConnectionCheckerService;
  private ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory;

  private Project project;

  @Before
  public void setUp() {
    projectValidator = mock(ProjectValidator.class);
    when(projectValidator.supports(any(Class.class))).thenReturn(true);
    sonarConnectionCheckerService = mock(SonarConnectionCheckerService.class);
    scmAvailabilityCheckerServiceFactory = mock(ScmAvailabilityCheckerServiceFactory.class);
    validator = new ProjectConnectionsValidator(projectValidator, sonarConnectionCheckerService, scmAvailabilityCheckerServiceFactory);

    project = mock(Project.class);
    when(project.getSonarConnectionSettings()).thenReturn(mock(SonarConnectionSettings.class));
    when(project.getScmSettings()).thenReturn(mock(ScmConnectionSettings.class));
  }

  @Test
  public void shouldSupportProjectType() {
    assertThat(validator.supports(Project.class)).isTrue();
  }

  @Test
  public void shouldNotSupportOtherTypeThanProject() {
    assertThat(validator.supports(Object.class)).isFalse();
  }

  @Test
  public void validProjectAndAvailableSystemsShouldResultInEmptyErrorsObject() {
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(true);
    mockAvailableScmSystem();
    Errors errors = validateProject(project);
    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void shouldCallSuppliedProjectValidator() {
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(true);
    mockAvailableScmSystem();
    validateProject(project);
    verify(projectValidator).validate(any(), any(Errors.class));
  }

  @Test
  public void shouldFailWhenSonarProjectIsNotReachable() {
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(false);
    mockAvailableScmSystem();
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("sonarConnectionSettings")).isTrue();
  }

  @Test
  public void shouldFailWhenScmSystemIsNotAvailable() {
    when(sonarConnectionCheckerService.isReachable(any(SonarConnectionSettings.class))).thenReturn(true);
    mockNotAvailableScmSystem();
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("scmSettings")).isTrue();
  }

  private void mockAvailableScmSystem() {
    mockScmSystemWithAvailability(true);
  }

  private void mockNotAvailableScmSystem() {
    mockScmSystemWithAvailability(false);
  }

  private void mockScmSystemWithAvailability(boolean isAvailable) {
    ScmAvailabilityCheckerService checkerService = mock(ScmAvailabilityCheckerService.class);
    when(checkerService.isAvailable(any(ScmConnectionSettings.class))).thenReturn(isAvailable);
    when(scmAvailabilityCheckerServiceFactory.create(any(ScmConnectionSettings.class))).thenReturn(checkerService);
  }

  private Errors validateProject(Project project) {
    Errors errors = new BeanPropertyBindingResult(project, "project");
    validator.validate(project, errors);
    return errors;
  }
}
