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
import org.codeqinvest.codechanges.scm.DailyCodeChurn;
import org.codeqinvest.codechanges.scm.factory.CodeChurnCalculatorFactory;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

/**
 * This {@code CodeChangeProbabilityCalculator} implementation uses
 * historical scm data to calculate the probability. Exactly as
 * {@code DefaultCodeChangeProbabilityCalculator}, it loads the past commit
 * data for the last X days. But each code change from the past
 * is weighted by an exponential formula.
 *
 * @author fmueller
 */
public class WeightedCodeChangeProbabilityCalculator extends AbstractCodeChangeProbabilityCalculator {

  public WeightedCodeChangeProbabilityCalculator(CodeChurnCalculatorFactory codeChurnCalculatorFactory, int days) {
    super(codeChurnCalculatorFactory, days);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeChangeProbability(int days, Set<DailyCodeChurn> codeChurns) {
    if (codeChurns.isEmpty()) {
      return 0.0;
    }

    final double numberOfDays = days + 1;
    final SortedSet<DailyCodeChurn> sortedCodeChurns = sortDescendingByDay(codeChurns);
    final LocalDate startDay = sortedCodeChurns.first().getDay();

    double changeProbability = 0.0;
    for (DailyCodeChurn codeChurn : sortedCodeChurns) {
      final int numberOfCurrentDay = Days.daysBetween(codeChurn.getDay(), startDay).getDays();
      for (double churnProportion : codeChurn.getCodeChurnProportions()) {
        final double weight = ((numberOfDays - numberOfCurrentDay) * Math.exp((days - numberOfCurrentDay) / numberOfDays)) / (numberOfDays * numberOfDays);
        changeProbability += churnProportion * weight;
      }
    }
    return changeProbability;
  }

  private SortedSet<DailyCodeChurn> sortDescendingByDay(Set<DailyCodeChurn> codeChurns) {
    SortedSet<DailyCodeChurn> sortedCodeChurn = Sets.newTreeSet(new Comparator<DailyCodeChurn>() {

      @Override
      public int compare(DailyCodeChurn codeChurn, DailyCodeChurn otherCodeChurn) {
        return -1 * codeChurn.getDay().compareTo(otherCodeChurn.getDay());
      }
    });
    sortedCodeChurn.addAll(codeChurns);
    return sortedCodeChurn;
  }
}
