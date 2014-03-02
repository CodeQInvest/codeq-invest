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
package org.codeqinvest.web.investment.roi;

import lombok.Data;

/**
 * @author fmueller
 */
@Data
class RoiDistributionChartRepresentation implements Comparable<RoiDistributionChartRepresentation> {

  static final String[] DEFAULT_INVESTMENTS = new String[]{"1h", "2h", "4h", "8h", "16h"};

  private final String key;
  private final ValueTuple[] values = new ValueTuple[DEFAULT_INVESTMENTS.length];

  RoiDistributionChartRepresentation(String key) {
    this.key = key;
    for (int i = 0; i < DEFAULT_INVESTMENTS.length; i++) {
      values[i] = new ValueTuple(DEFAULT_INVESTMENTS[i], 0);
    }
  }

  void setValue(int index, ValueTuple value) {
    values[index] = value;
  }

  @Override
  public int compareTo(RoiDistributionChartRepresentation roiDistributionChartRepresentation) {
    return key.compareToIgnoreCase(roiDistributionChartRepresentation.key);
  }
}
