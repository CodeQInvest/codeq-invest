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
package org.codeqinvest.codechanges;

import org.codeqinvest.codechanges.scm.CodeChurn;
import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class CommitBasedCodeChangeProbabilityCalculatorTest extends AbstractCodeChangeProbabilityCalculatorTest {

  @Test
  public void codeChurnForOneLastCommit() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    fakeCodeChurnCalculator.addCodeChurnWithoutDay("A", new CodeChurn(Arrays.asList(0.8)));
    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = new CommitBasedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, 1);
    assertThat(codeChangeProbabilityCalculator.calculateCodeChangeProbability(dummyConnectionSettings, "A")).isEqualTo(0.8);
  }

  @Test
  public void codeChurnForManyLastCommits() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    fakeCodeChurnCalculator.addCodeChurnWithoutDay("A", new CodeChurn(Arrays.asList(0.8)));
    fakeCodeChurnCalculator.addCodeChurnWithoutDay("A", new CodeChurn(Arrays.asList(1.0)));
    fakeCodeChurnCalculator.addCodeChurnWithoutDay("A", new CodeChurn(Arrays.asList(0.2)));
    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = new CommitBasedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, 4);
    assertThat(codeChangeProbabilityCalculator.calculateCodeChangeProbability(dummyConnectionSettings, "A")).isEqualTo(0.5);
  }

  @Test
  public void maxPossibleProbabilityShouldBeOne() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    fakeCodeChurnCalculator.addCodeChurnWithoutDay("A", new CodeChurn(Arrays.asList(10.0)));
    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = new CommitBasedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, 1);
    assertThat(codeChangeProbabilityCalculator.calculateCodeChangeProbability(dummyConnectionSettings, "A")).isEqualTo(1.0);
  }
}
