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
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;

/**
 * Describes a calculator for the probability that a given
 * code file will be changed in the near future.
 *
 * @author fmueller
 */
public interface CodeChangeProbabilityCalculator {

  /**
   * Calculates the probability that a code file will be changed
   * in the near future.
   *
   * @param connectionSettings the connection setting for the scm system where the {@code file} can be found
   * @param file               the file for which the change probability will be calculated
   * @return the calculated probability that the {@code file} will be changed
   * @throws CodeChurnCalculationException  if an error with scm server communication or calculation happens
   * @throws ScmConnectionEncodingException if an error with the supplied encoding of the {@code connectionSettings} happens
   */
  double calculateCodeChangeProbability(ScmConnectionSettings connectionSettings, String file)
      throws CodeChurnCalculationException, ScmConnectionEncodingException;
}
