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
package org.codeqinvest.investment.profit;

import org.codeqinvest.quality.QualityViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fmueller
 */
@Component
public class WeightedProfitCalculator {

  private final ProfitCalculator profitCalculator;

  @Autowired
  public WeightedProfitCalculator(ProfitCalculator profitCalculator) {
    this.profitCalculator = profitCalculator;
  }

  public double calculateWeightedProfit(QualityViolation violation) {
    return profitCalculator.calculateProfit(violation) / violation.getWeightingMetricValue();
  }
}
