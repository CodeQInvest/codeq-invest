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
package org.codeqinvest.investment.profit;

import org.codeqinvest.quality.QualityViolation;
import org.fest.assertions.Delta;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeightedProfitCalculatorTest {

  @Test
  public void shouldDivideProfitOfViolationByWeightingMetricValue() {
    ProfitCalculator profitCalculator = mock(ProfitCalculator.class);
    when(profitCalculator.calculateProfit(any(QualityViolation.class))).thenReturn(20.0);

    QualityViolation violation = mock(QualityViolation.class);
    when(violation.getWeightingMetricValue()).thenReturn(13.0);

    WeightedProfitCalculator weightedProfitCalculator = new WeightedProfitCalculator(profitCalculator);
    assertThat(weightedProfitCalculator.calculateWeightedProfit(violation)).isEqualTo(1.54, Delta.delta(0.01));
  }
}
