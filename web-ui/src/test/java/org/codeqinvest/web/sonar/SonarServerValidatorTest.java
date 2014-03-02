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
package org.codeqinvest.web.sonar;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.fest.assertions.Assertions.assertThat;

public class SonarServerValidatorTest {

  private SonarServerValidator validator;

  @Before
  public void setUp() {
    validator = new SonarServerValidator();
  }

  @Test
  public void shouldSupportSonarServerType() {
    assertThat(validator.supports(SonarServer.class)).isTrue();
  }

  @Test
  public void shouldNotSupportOtherTypeThanSonarServer() {
    assertThat(validator.supports(Object.class)).isFalse();
  }

  @Test
  public void validSettingsShouldResultInEmptyErrorsObject() {
    SonarServer server = new SonarServer("http://localhost");
    Errors errors = validateSonarServer(server);
    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void urlShouldBeMandatory() {
    SonarServer server = new SonarServer("");
    Errors errors = validateSonarServer(server);
    assertThat(errors.hasFieldErrors("url")).isTrue();
  }

  @Test
  public void urlShouldBeValidUrl() {
    SonarServer server = new SonarServer("localhost");
    Errors errors = validateSonarServer(server);
    assertThat(errors.hasFieldErrors("url")).isTrue();
  }

  private Errors validateSonarServer(SonarServer server) {
    Errors errors = new BeanPropertyBindingResult(server, "server");
    validator.validate(server, errors);
    return errors;
  }
}
