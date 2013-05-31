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
package org.codeqinvest.sonar;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SonarConnectionCheckerServiceTest {

  private SonarConnectionCheckerService sonarConnectionCheckerService;

  @Before
  public void setUp() {
    sonarConnectionCheckerService = new SonarConnectionCheckerService();
  }

  @Test
  public void notReachableWhenConnectionSettingsAreNull() {
    assertThat(sonarConnectionCheckerService.isReachable(null)).isFalse();
  }

  @Test
  public void notReachableWhenUrlOfConnectionSettingsIsNull() {
    assertThat(sonarConnectionCheckerService.isReachable(new SonarConnectionSettings(null))).isFalse();
  }

  @Test
  public void notReachableWhenUrlOfConnectionSettingsIsEmpty() {
    assertThat(sonarConnectionCheckerService.isReachable(new SonarConnectionSettings(""))).isFalse();
  }
}
