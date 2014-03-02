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
package org.codeqinvest.web.project;

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.repository.QualityProfileRepository;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectValidatorTest {

  private final String validName = "MyProject";
  private final String validCronExpression = "* * * * * *";

  private ProjectValidator validator;
  private ProjectRepository projectRepository;
  private QualityProfileRepository profileRepository;
  private SonarConnectionSettingsValidator sonarConnectionSettingsValidator;
  private ScmConnectionSettingsValidator scmConnectionSettingsValidator;
  private CodeChangeSettingsValidator codeChangeSettingsValidator;

  private QualityProfile profile;
  private SonarConnectionSettings sonarConnectionSettings;
  private ScmConnectionSettings scmSettings;
  private CodeChangeSettings codeChangeSettings;

  @Before
  public void setUp() {
    projectRepository = mock(ProjectRepository.class);
    profileRepository = mock(QualityProfileRepository.class);
    when(profileRepository.exists(anyLong())).thenReturn(true);
    sonarConnectionSettingsValidator = new SonarConnectionSettingsValidator();
    scmConnectionSettingsValidator = new ScmConnectionSettingsValidator();
    codeChangeSettingsValidator = new CodeChangeSettingsValidator();
    validator = new ProjectValidator(projectRepository, profileRepository,
        sonarConnectionSettingsValidator, scmConnectionSettingsValidator, codeChangeSettingsValidator);

    profile = mock(QualityProfile.class);
    sonarConnectionSettings = new SonarConnectionSettings("http://localhost", "project");
    scmSettings = new ScmConnectionSettings("scm:svn:http://localhost");
    codeChangeSettings = CodeChangeSettings.defaultSetting(1);
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
  public void validProjectShouldResultInEmptyErrorsObject() {
    Project project = new Project(validName, validCronExpression, profile, sonarConnectionSettings, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void nameAttributeShouldBeMandatory() {
    Project project = new Project("", validCronExpression, profile, sonarConnectionSettings, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("name")).isTrue();
  }

  @Test
  public void nameAttributeShouldBeUnique() {
    when(projectRepository.findOneByLowercaseName(eq(validName.toLowerCase()))).thenReturn(mock(Project.class));
    Project project = new Project(validName, validCronExpression, profile, sonarConnectionSettings, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("name")).isTrue();
  }

  @Test
  public void cronExpressionAttributeShouldBeMandatory() {
    Project project = new Project(validName, "", profile, sonarConnectionSettings, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("cronExpression")).isTrue();
  }

  @Test
  public void cronExpressionAttributeShouldContainValidCronExpression() {
    Project project = new Project(validName, "* * * *", profile, sonarConnectionSettings, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("cronExpression")).isTrue();
  }

  @Test
  public void qualityProfileMustNotBeNull() {
    Project project = new Project(validName, validCronExpression, null, sonarConnectionSettings, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("profile")).isTrue();
  }

  @Test
  public void qualityProfileShouldHaveIdOfExistingQualityProfile() {
    when(profileRepository.exists(anyLong())).thenReturn(false);
    Project project = new Project(validName, validCronExpression, profile, sonarConnectionSettings, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("profile")).isTrue();
  }

  @Test
  public void sonarConnectionSettingsMustNotBeNull() {
    Project project = new Project(validName, validCronExpression, profile, null, scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("sonarConnectionSettings")).isTrue();
  }

  @Test
  public void sonarConnectionSettingsShouldBeValid() {
    Project project = new Project(validName, validCronExpression, profile, new SonarConnectionSettings(""), scmSettings, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("sonarConnectionSettings.url")).isTrue();
  }

  @Test
  public void scmConnectionSettingsMustNotBeNull() {
    Project project = new Project(validName, validCronExpression, profile, sonarConnectionSettings, null, codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("scmSettings")).isTrue();
  }

  @Test
  public void scmConnectionSettingsShouldBeValid() {
    Project project = new Project(validName, validCronExpression, profile, sonarConnectionSettings, new ScmConnectionSettings(""), codeChangeSettings);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("scmSettings.url")).isTrue();
  }

  @Test
  public void codeChangeSettingsMustNotBeNull() {
    Project project = new Project(validName, validCronExpression, profile, sonarConnectionSettings, scmSettings, null);
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("codeChangeSettings")).isTrue();
  }

  @Test
  public void codeChangeSettingsShouldBeValid() {
    Project project = new Project(validName, validCronExpression, profile, sonarConnectionSettings, null, CodeChangeSettings.defaultSetting(-1));
    Errors errors = validateProject(project);
    assertThat(errors.hasFieldErrors("codeChangeSettings.days")).isTrue();
  }

  private Errors validateProject(Project project) {
    Errors errors = new BeanPropertyBindingResult(project, "project");
    validator.validate(project, errors);
    return errors;
  }
}
