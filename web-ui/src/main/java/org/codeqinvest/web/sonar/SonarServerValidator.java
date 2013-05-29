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
package org.codeqinvest.web.sonar;

import org.codeqinvest.web.validation.ValidationHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This validator implementation can be used to validate
 * a binded {@link org.codeqinvest.web.sonar.SonarServer} instance.
 *
 * @author fmueller
 */
@Component
class SonarServerValidator implements Validator {

  /**
   * This validator only supports {@link org.codeqinvest.web.sonar.SonarServer} type.
   */
  @Override
  public boolean supports(Class<?> clazz) {
    return SonarServer.class.equals(clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(Object target, Errors errors) {
    ValidationHelper.rejectIfEmptyOrWhitespace(errors, "url");
    SonarServer server = (SonarServer) target;
    if (server.getUrl() != null) {
      try {
        new URL(server.getUrl());
      } catch (MalformedURLException e) {
        errors.rejectValue("url", "malformed.url");
      }
    }
  }
}
