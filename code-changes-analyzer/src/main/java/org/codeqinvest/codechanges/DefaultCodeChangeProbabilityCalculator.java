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

import org.codeqinvest.codechanges.scm.DailyCodeChurn;
import org.codeqinvest.codechanges.scm.factory.CodeChurnCalculatorFactory;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * This {@code CodeChangeProbabilityCalculator} implementation uses
 * historical scm data to calculate the probability. For that, it loads
 * the past commit data for the last X days. Every code change from the past
 * is weighted equally.
 *
 * @author fmueller
 */
public class DefaultCodeChangeProbabilityCalculator extends AbstractCodeChangeProbabilityCalculator {

  public DefaultCodeChangeProbabilityCalculator(CodeChurnCalculatorFactory codeChurnCalculatorFactory, LocalDate startDay, int days) {
    super(codeChurnCalculatorFactory, startDay, days);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeChangeProbability(int days, Collection<DailyCodeChurn> codeChurns) {
    final double numberOfDays = days + 1.0;
    double changeProbability = 0.0;
    for (DailyCodeChurn codeChurn : codeChurns) {
      for (double churnProportion : codeChurn.getCodeChurnProportions()) {
        changeProbability += churnProportion * (1 / numberOfDays);
      }
    }
    return changeProbability;
  }
}
