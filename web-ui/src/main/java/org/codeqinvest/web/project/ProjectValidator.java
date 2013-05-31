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

import com.google.common.base.Strings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.codeqinvest.quality.repository.QualityProfileRepository;
import org.codeqinvest.web.validation.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * This validator implementation can be used to validate
 * a binded {@link org.codeqinvest.quality.Project} instance.
 *
 * @author fmueller
 */
@Component
class ProjectValidator implements Validator {

  private final ProjectRepository projectRepository;
  private final QualityProfileRepository profileRepository;
  private final SonarConnectionSettingsValidator sonarConnectionSettingsValidator;
  private final ScmConnectionSettingsValidator scmConnectionSettingsValidator;
  private final CodeChangeSettingsValidator codeChangeSettingsValidator;

  @Autowired
  ProjectValidator(ProjectRepository projectRepository,
                   QualityProfileRepository profileRepository,
                   SonarConnectionSettingsValidator sonarConnectionSettingsValidator,
                   ScmConnectionSettingsValidator scmConnectionSettingsValidator,
                   CodeChangeSettingsValidator codeChangeSettingsValidator) {
    this.projectRepository = projectRepository;
    this.profileRepository = profileRepository;
    this.sonarConnectionSettingsValidator = sonarConnectionSettingsValidator;
    this.scmConnectionSettingsValidator = scmConnectionSettingsValidator;
    this.codeChangeSettingsValidator = codeChangeSettingsValidator;
  }

  /**
   * This validator only supports {@link org.codeqinvest.quality.Project} type.
   */
  @Override
  public boolean supports(Class<?> clazz) {
    return Project.class.equals(clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(Object target, Errors errors) {
    ValidationHelper.rejectIfEmptyOrWhitespace(errors, "name");
    ValidationHelper.rejectIfEmptyOrWhitespace(errors, "cronExpression");
    ValidationHelper.rejectIfEmpty(errors, "profile");
    ValidationHelper.rejectIfEmpty(errors, "sonarConnectionSettings");
    ValidationHelper.rejectIfEmpty(errors, "scmSettings");
    ValidationHelper.rejectIfEmpty(errors, "codeChangeSettings");

    Project project = (Project) target;
    validateUniqueName(project, errors);
    validateCronExpression(project, errors);
    validateThatQualityProfileExists(project, errors);
    validateSonarConnectionSettings(project, errors);
    validateScmSettings(project, errors);
    validateCodeChangeSettings(project, errors);
  }

  private void validateUniqueName(Project project, Errors errors) {
    if (!Strings.isNullOrEmpty(project.getName())
        && projectRepository.findOneByLowercaseName(project.getLowercaseName()) != null) {
      errors.rejectValue("name", "not.unique");
    }
  }

  private void validateCronExpression(Project project, Errors errors) {
    if (!Strings.isNullOrEmpty(project.getCronExpression())) {
      try {
        new CronTrigger(project.getCronExpression());
      } catch (IllegalArgumentException e) {
        errors.rejectValue("cronExpression", "non.valid.cron");
      }
    }
  }

  private void validateThatQualityProfileExists(Project project, Errors errors) {
    if (project.getProfile() != null && project.getProfile().getId() != null
        && !profileRepository.exists(project.getProfile().getId())) {
      errors.rejectValue("profile", "quality.profile.not.exists");
    }
  }

  private void validateSonarConnectionSettings(Project project, Errors errors) {
    if (project.getSonarConnectionSettings() != null) {
      try {
        errors.pushNestedPath("sonarConnectionSettings");
        ValidationUtils.invokeValidator(sonarConnectionSettingsValidator, project.getSonarConnectionSettings(), errors);
      } finally {
        errors.popNestedPath();
      }
    }
  }

  private void validateScmSettings(Project project, Errors errors) {
    if (project.getScmSettings() != null) {
      try {
        errors.pushNestedPath("scmSettings");
        ValidationUtils.invokeValidator(scmConnectionSettingsValidator, project.getScmSettings(), errors);
      } finally {
        errors.popNestedPath();
      }
    }
  }

  private void validateCodeChangeSettings(Project project, Errors errors) {
    if (project.getCodeChangeSettings() != null) {
      try {
        errors.pushNestedPath("codeChangeSettings");
        ValidationUtils.invokeValidator(codeChangeSettingsValidator, project.getCodeChangeSettings(), errors);
      } finally {
        errors.popNestedPath();
      }
    }
  }
}
