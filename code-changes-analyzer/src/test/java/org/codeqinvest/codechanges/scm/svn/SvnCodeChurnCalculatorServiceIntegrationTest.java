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
package org.codeqinvest.codechanges.scm.svn;

import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.DailyCodeChurn;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.fest.assertions.Delta;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/module-context.xml")
public class SvnCodeChurnCalculatorServiceIntegrationTest {

  @Autowired
  private SvnCodeChurnCalculatorService codeChurnCalculator;

  @Test
  public void shouldCalculateAllCodeChurnProportionsForOneDayAndOneFile() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    // TODO improve this test with vagrant and puppet and own svn repository server via vm
    ScmConnectionSettings connectionSettings = new ScmConnectionSettings("http://rapla.googlecode.com/svn/trunk/src/");
    Collection<DailyCodeChurn> codeChurns = codeChurnCalculator.calculateCodeChurn(connectionSettings,
        "org/rapla/server/internal/SecurityManager.java",
        new LocalDate(2013, 5, 17), 0);
    assertThat(codeChurns).hasSize(1);

    DailyCodeChurn codeChurn = codeChurns.iterator().next();
    assertThat(codeChurn.getCodeChurnProportions().get(0)).isEqualTo(0.0719, Delta.delta(0.0001));
    assertThat(codeChurn.getCodeChurnProportions().get(1)).isEqualTo(0.0111, Delta.delta(0.0001));
  }

  @Test
  public void shouldHandleRenamedFilesProperlyForOneCommit() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    ScmConnectionSettings connectionSettings = new ScmConnectionSettings("http://svn.apache.org/repos/asf/commons/proper/configuration/trunk/src/main/java");
    Collection<DailyCodeChurn> codeChurns = codeChurnCalculator.calculateCodeChurn(connectionSettings,
        "org/apache/commons/configuration/reloading/ManagedReloadingDetector.java", new LocalDate(2013, 4, 4), 0);
    assertThat(codeChurns).hasSize(1);

    DailyCodeChurn codeChurn = codeChurns.iterator().next();
    assertThat(codeChurn.getCodeChurnProportions()).hasSize(1);
    assertThat(codeChurn.getCodeChurnProportions().get(0)).isEqualTo(0.1126, Delta.delta(0.0001));
  }

  @Test
  public void shouldHandleRenamedFilesProperlyOverSeveralCommits() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    ScmConnectionSettings connectionSettings = new ScmConnectionSettings("http://svn.apache.org/repos/asf/commons/proper/configuration/trunk/src/main/java");
    Collection<DailyCodeChurn> codeChurns = codeChurnCalculator.calculateCodeChurn(connectionSettings,
        "org/apache/commons/configuration/reloading/ManagedReloadingDetector.java", new LocalDate(2013, 5, 6), 36); // until 2013-04-01
    assertThat(codeChurns).hasSize(37);

    DailyCodeChurn fifthMay = getCodeChurnByDay(codeChurns, new LocalDate(2013, 5, 5));
    assertThat(fifthMay.getCodeChurnProportions()).hasSize(1);
    assertThat(fifthMay.getCodeChurnProportions().get(0)).isEqualTo(0.0563, Delta.delta(0.0001));

    DailyCodeChurn forthApril = getCodeChurnByDay(codeChurns, new LocalDate(2013, 4, 4));
    assertThat(forthApril.getCodeChurnProportions()).hasSize(1);
    assertThat(forthApril.getCodeChurnProportions().get(0)).isEqualTo(0.1126, Delta.delta(0.0001));
  }

  private DailyCodeChurn getCodeChurnByDay(Collection<DailyCodeChurn> codeChurns, LocalDate day) {
    for (DailyCodeChurn codeChurn : codeChurns) {
      if (codeChurn.getDay().equals(day)) {
        return codeChurn;
      }
    }
    return null;
  }
}
