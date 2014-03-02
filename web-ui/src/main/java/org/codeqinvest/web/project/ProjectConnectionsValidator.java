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

import org.codeqinvest.codechanges.scm.factory.ScmAvailabilityCheckerServiceFactory;
import org.codeqinvest.codechanges.scm.factory.UnsupportedScmSystem;
import org.codeqinvest.quality.Project;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * This validator implementation validates a binded
 * {@link org.codeqinvest.quality.Project} instance and
 * that the remote systems are available.
 *
 * @author fmueller
 */
@Component
class ProjectConnectionsValidator implements Validator {

  private final ProjectValidator projectValidator;
  private final SonarConnectionCheckerService sonarConnectionCheckerService;
  private final ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory;

  @Autowired
  ProjectConnectionsValidator(ProjectValidator projectValidator,
                              SonarConnectionCheckerService sonarConnectionCheckerService,
                              ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory) {
    this.projectValidator = projectValidator;
    this.sonarConnectionCheckerService = sonarConnectionCheckerService;
    this.scmAvailabilityCheckerServiceFactory = scmAvailabilityCheckerServiceFactory;
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
    ValidationUtils.invokeValidator(projectValidator, target, errors);
    Project project = (Project) target;
    if (!sonarConnectionCheckerService.isReachable(project.getSonarConnectionSettings())) {
      errors.rejectValue("sonarConnectionSettings", "sonar.not.reachable");
    }

    try {
      if (!scmAvailabilityCheckerServiceFactory.create(project.getScmSettings()).isAvailable(project.getScmSettings())) {
        scmSystemNotAvailable(errors);
      }
    } catch (UnsupportedScmSystem e) {
      scmSystemNotAvailable(errors);
    }
  }

  private void scmSystemNotAvailable(Errors errors) {
    errors.rejectValue("scmSettings", "scm.not.available");
  }
}
