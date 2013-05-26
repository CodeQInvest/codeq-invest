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
import javax.persistence.Embedded;
import java.io.Serializable;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@Embeddable
public class QualityCriteria implements Serializable {

  @Column(nullable = false, length = 50)
  private String metricIdentifier;

  @Embedded
  private Criteria criteria;

  protected QualityCriteria() {
  }

  public QualityCriteria(String metricIdentifier, String operator, double threshold) {
    this.metricIdentifier = metricIdentifier;
    this.criteria = new Criteria(operator, threshold);
  }

  public boolean isViolated(double metricValue) {
    return criteria.isViolated(metricValue);
  }

  public double getThreshold() {
    return criteria.getThreshold();
  }

  public String getOperator() {
    return criteria.getOperator();
  }

  @Override
  public String toString() {
    return "[" + metricIdentifier + " " + getOperator() + " " + getThreshold() + "]";
  }
}
