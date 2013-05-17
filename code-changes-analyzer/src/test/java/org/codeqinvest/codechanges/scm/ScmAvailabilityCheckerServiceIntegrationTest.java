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
package org.codeqinvest.codechanges.scm;

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/module-context.xml")
public class ScmAvailabilityCheckerServiceIntegrationTest {

  @Autowired
  private ScmAvailabilityCheckerService connectionCheckerService;

  @Test
  public void apacheSvnServerShouldBeReachable() {
    // TODO improve this with vagrant and puppet
    assertThat(connectionCheckerService.isAvailable(new ScmConnectionSettings("http://svn.apache.org/repos/asf/commons/proper/logging/trunk/src/main/java/"))).isTrue();
  }
}
