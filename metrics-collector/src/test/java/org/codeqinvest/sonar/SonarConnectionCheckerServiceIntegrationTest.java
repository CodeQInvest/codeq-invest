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
package org.codeqinvest.sonar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/module-context.xml")
public class SonarConnectionCheckerServiceIntegrationTest {

  @Autowired
  private SonarConnectionCheckerService connectionCheckerService;

  @Test
  public void nemoSonarDemoServerShouldBeReachable() {
    assertThat(connectionCheckerService.isReachable(new SonarConnectionSettings("http://nemo.sonarsource.org"))).isTrue();
  }

  @Test
  public void whenServerIsNotSonarItShouldBeUnreachable() {
    assertThat(connectionCheckerService.isReachable(new SonarConnectionSettings("http://google.com"))).isFalse();
  }
}
