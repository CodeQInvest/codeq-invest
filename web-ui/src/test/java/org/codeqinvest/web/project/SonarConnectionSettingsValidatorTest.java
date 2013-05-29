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

import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.fest.assertions.Assertions.assertThat;

public class SonarConnectionSettingsValidatorTest {

  private SonarConnectionSettingsValidator validator;

  @Before
  public void setUp() {
    validator = new SonarConnectionSettingsValidator();
  }

  @Test
  public void shouldSupportSonarConnectionSettingsType() {
    assertThat(validator.supports(SonarConnectionSettings.class)).isTrue();
  }

  @Test
  public void shouldNotSupportOtherTypeThanSonarConnectionSettings() {
    assertThat(validator.supports(Object.class)).isFalse();
  }

  @Test
  public void validSettingsShouldResultInEmptyErrorsObject() {
    SonarConnectionSettings settings = new SonarConnectionSettings("http://localhost", "project");
    Errors errors = validateSettings(settings);
    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void urlShouldBeMandatory() {
    SonarConnectionSettings settings = new SonarConnectionSettings("");
    Errors errors = validateSettings(settings);
    assertThat(errors.hasFieldErrors("url")).isTrue();
  }

  @Test
  public void urlShouldBeValidUrl() {
    SonarConnectionSettings settings = new SonarConnectionSettings("locahost");
    Errors errors = validateSettings(settings);
    assertThat(errors.hasFieldErrors("url")).isTrue();
  }

  @Test
  public void projectShouldBeMandatory() {
    SonarConnectionSettings settings = new SonarConnectionSettings("http://localhost", "   ");
    Errors errors = validateSettings(settings);
    assertThat(errors.hasFieldErrors("project")).isTrue();
  }

  private Errors validateSettings(SonarConnectionSettings settings) {
    Errors errors = new BeanPropertyBindingResult(settings, "settings");
    validator.validate(settings, errors);
    return errors;
  }
}
