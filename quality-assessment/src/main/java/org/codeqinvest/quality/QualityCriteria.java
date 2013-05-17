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
package org.codeqinvest.quality;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Arrays;
import java.util.List;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@Embeddable
public class QualityCriteria {

  private static final List<String> ALLOWED_OPERATORS = Arrays.asList("<", ">", "=", "!=", "<=", ">=");

  @Column(nullable = false, length = 50)
  private String metricIdentifier;

  @Column(nullable = false, length = 2)
  private String operator;

  @Column(nullable = false)
  private double threshold;

  protected QualityCriteria() {
  }

  public QualityCriteria(String metricIdentifier, String operator, double threshold) {
    if (!ALLOWED_OPERATORS.contains(operator)) {
      throw new IllegalArgumentException("only these operators are allowed: " + ALLOWED_OPERATORS);
    }

    this.metricIdentifier = metricIdentifier;
    this.operator = operator;
    this.threshold = threshold;
  }

  public boolean isViolated(double metricValue) {
    if (operator.equals("<")) {
      return metricValue >= threshold;
    } else if (operator.equals(">")) {
      return metricValue <= threshold;
    } else if (operator.equals("!=")) {
      return metricValue == threshold;
    } else if (operator.equals("=")) {
      return metricValue != threshold;
    } else if (operator.equals("<=")) {
      return metricValue > threshold;
    } else if (operator.equals(">=")) {
      return metricValue < threshold;
    }
    return false;
  }

  @Override
  public String toString() {
    return "[" + metricIdentifier + " " + operator + " " + threshold + "]";
  }
}
