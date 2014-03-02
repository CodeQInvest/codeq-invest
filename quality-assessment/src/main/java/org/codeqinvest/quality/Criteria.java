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
package org.codeqinvest.quality;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@Embeddable
public class Criteria implements Serializable {

  private static final List<String> ALLOWED_OPERATORS = Arrays.asList("<", ">", "=", "!=", "<=", ">=");

  @Column(nullable = false, length = 2)
  private String operator;

  @Column(nullable = false)
  private double threshold;

  protected Criteria() {
  }

  public Criteria(String operator, double threshold) {
    if (!ALLOWED_OPERATORS.contains(operator)) {
      throw new IllegalArgumentException("only these operators are allowed: " + ALLOWED_OPERATORS);
    }

    this.operator = operator;
    this.threshold = threshold;
  }

  public boolean isViolated(double value) {
    if (operator.equals("<")) {
      return value >= threshold;
    } else if (operator.equals(">")) {
      return value <= threshold;
    } else if (operator.equals("!=")) {
      return value == threshold;
    } else if (operator.equals("=")) {
      return value != threshold;
    } else if (operator.equals("<=")) {
      return value > threshold;
    } else if (operator.equals(">=")) {
      return value < threshold;
    }
    return false;
  }
}
