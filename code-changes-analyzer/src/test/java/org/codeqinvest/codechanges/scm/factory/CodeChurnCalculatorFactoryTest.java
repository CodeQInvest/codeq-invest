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
package org.codeqinvest.codechanges.scm.factory;

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.svn.SvnCodeChurnCalculatorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/module-context.xml")
public class CodeChurnCalculatorFactoryTest {

  @Autowired
  private CodeChurnCalculatorFactory codeChurnCalculatorFactory;

  @Test(expected = UnsupportedScmSystem.class)
  public void shouldFailForNotSupportedSvmTypesWithException() {
    ScmConnectionSettings connectionSettings = mock(ScmConnectionSettings.class);
    when(connectionSettings.getType()).thenReturn(Integer.MAX_VALUE);
    codeChurnCalculatorFactory.create(connectionSettings);
  }

  @Test
  public void createSvnCodeChurnCalculatorForSvnType() {
    ScmConnectionSettings connectionSettings = mock(ScmConnectionSettings.class);
    when(connectionSettings.getType()).thenReturn(SupportedScmSystem.SVN.getType());
    assertThat(codeChurnCalculatorFactory.create(connectionSettings)).isInstanceOf(SvnCodeChurnCalculatorService.class);
  }
}
