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

import com.google.common.collect.Sets;
import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.CodeChurnCalculator;
import org.codeqinvest.codechanges.scm.DailyCodeChurn;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.factory.CodeChurnCalculatorFactory;
import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.Set;

/**
 * Helper base class for {@code CodeChangeProbabilityCalculator} implementations
 * that uses historical data from the scm system.
 *
 * @author fmueller
 */
abstract class AbstractCodeChangeProbabilityCalculator implements CodeChangeProbabilityCalculator {

  private final CodeChurnCalculatorFactory codeChurnCalculatorFactory;
  private final int days;

  protected AbstractCodeChangeProbabilityCalculator(CodeChurnCalculatorFactory codeChurnCalculatorFactory, int days) {
    this.codeChurnCalculatorFactory = codeChurnCalculatorFactory;
    this.days = days;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final double calculateCodeChangeProbability(ScmConnectionSettings connectionSettings, String file)
      throws CodeChurnCalculationException, ScmConnectionEncodingException {

    final CodeChurnCalculator codeChurnCalculator = codeChurnCalculatorFactory.create(connectionSettings);
    final LocalDate startDay = LocalDate.now();

    Set<DailyCodeChurn> codeChurns = Sets.newHashSet();
    for (int i = 0; i <= days; i++) {
      codeChurns.add(retrieveCodeChurnWithZeroAsDefault(codeChurnCalculator, connectionSettings, file, startDay.minusDays(i)));
    }

    return Math.min(1.0, computeChangeProbability(days, codeChurns));
  }

  private DailyCodeChurn retrieveCodeChurnWithZeroAsDefault(CodeChurnCalculator codeChurnCalculator, ScmConnectionSettings connectionSettings, String file, LocalDate day) {
    try {
      return codeChurnCalculator.calculateCodeChurn(connectionSettings, file, day);
    } catch (CodeChurnCalculationException e) {
      return new DailyCodeChurn(day, Collections.<Double>emptyList());
    } catch (ScmConnectionEncodingException e) {
      return new DailyCodeChurn(day, Collections.<Double>emptyList());
    }
  }

  protected abstract double computeChangeProbability(int days, Set<DailyCodeChurn> codeChurns);
}
