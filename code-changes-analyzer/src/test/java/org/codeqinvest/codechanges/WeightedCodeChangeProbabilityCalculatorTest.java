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
package org.codeqinvest.codechanges;

import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.DailyCodeChurn;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.fest.assertions.Delta;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class WeightedCodeChangeProbabilityCalculatorTest extends AbstractCodeChangeProbabilityCalculatorTest {

  private LocalDate startDay;

  @Before
  public void setUp() {
    startDay = LocalDate.now();
  }

  @Test
  public void codeChurnForOneDayPeriod() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(startDay, Arrays.asList(0.8)));
    // no data for yesterday, so only 'today' values count
    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = new WeightedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, startDay, 1);
    assertThat(codeChangeProbabilityCalculator.calculateCodeChangeProbability(dummyConnectionSettings, "A"))
        .isEqualTo(0.6595, Delta.delta(0.0001));
  }

  @Test
  public void codeChurnForFiveDayPeriod() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(startDay, Arrays.asList(0.8, 0.1)));
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(startDay.minusDays(1), Arrays.asList(0.1)));
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(startDay.minusDays(2), Arrays.asList(0.23)));
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(startDay.minusDays(3), Arrays.asList(0.33, 0.004)));
    // on the fourth day nothing happened
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(LocalDate.now().minusDays(5), Arrays.asList(1.0)));

    final double expectedSumOfCodeChurnProportions = 0.306796785456 + 0.038349598181547084
        + 0.027051861681
        + 0.042133988029
        + 0.03837934168987 + 0.000465
        + 0.027777;

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = new WeightedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, startDay, 5);
    assertThat(codeChangeProbabilityCalculator.calculateCodeChangeProbability(dummyConnectionSettings, "A"))
        .isEqualTo(expectedSumOfCodeChurnProportions, Delta.delta(0.001));
  }

  @Test
  public void maxPossibleProbabilityShouldBeOne() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(startDay, Arrays.asList(20.0, 30.0, 40.0)));
    fakeCodeChurnCalculator.addCodeChurn("A", new DailyCodeChurn(startDay.minusDays(1), Arrays.asList(20.0, 10.0)));
    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = new WeightedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, startDay, 1);
    assertThat(codeChangeProbabilityCalculator.calculateCodeChangeProbability(dummyConnectionSettings, "A")).isEqualTo(1.0);
  }
}
