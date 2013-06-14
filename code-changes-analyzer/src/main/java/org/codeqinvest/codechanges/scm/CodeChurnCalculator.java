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

import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Implementations of this interface calculate the daily
 * code churn proportion of one file for one day. The code
 * churn is the sum of all inserted, modified and deleted lines.
 *
 * @author fmueller
 */
public interface CodeChurnCalculator {

  /**
   * Calculates the code churn proportions of one file for a number of days in the past starting from
   * a specified start date. The code churn proportion is the portion of code that has changed by commits in comparison to the whole file.
   *
   * @return all code churn proportions for the given days
   * @throws CodeChurnCalculationException  if an error with scm server communication or calculation happens
   * @throws ScmConnectionEncodingException if an error with the supplied encoding of the {@code connectionSettings} happens
   */
  Collection<DailyCodeChurn> calculateCodeChurn(ScmConnectionSettings connectionSettings, String file, LocalDate startDay, int numberOfDays)
      throws CodeChurnCalculationException, ScmConnectionEncodingException;

  /**
   * Once a code churn calculation for one project has finished,
   * this method should be called to clean up eventually allocated resources.
   */
  void reset();
}
