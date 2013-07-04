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

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.CodeChurn;
import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.CodeChurnCalculator;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.factory.CodeChurnCalculatorFactory;

/**
 * This code change probability calculator uses a fixed number of
 * the last commits to estimate the change probability of a file.
 *
 * @author fmueller
 */
@Slf4j
public class CommitBasedCodeChangeProbabilityCalculator implements CodeChangeProbabilityCalculator {

  private final CodeChurnCalculatorFactory codeChurnCalculatorFactory;
  private final int numberOfCommits;

  public CommitBasedCodeChangeProbabilityCalculator(CodeChurnCalculatorFactory codeChurnCalculatorFactory, int numberOfCommits) {
    this.codeChurnCalculatorFactory = codeChurnCalculatorFactory;
    this.numberOfCommits = numberOfCommits;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double calculateCodeChangeProbability(ScmConnectionSettings connectionSettings, String file)
      throws CodeChurnCalculationException, ScmConnectionEncodingException {

    final CodeChurnCalculator codeChurnCalculator = codeChurnCalculatorFactory.create(connectionSettings);
    CodeChurn codeChurn = codeChurnCalculator.calculateCodeChurnForLastCommits(connectionSettings, file, numberOfCommits);
    double changeProbability = 0.0;
    for (Double codeChurnProportion : codeChurn.getCodeChurnProportions()) {
      changeProbability += codeChurnProportion * (1 / (double) numberOfCommits);
    }
    return Math.min(1.0, changeProbability);
  }
}
