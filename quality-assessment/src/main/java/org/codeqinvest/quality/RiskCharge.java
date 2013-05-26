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
import lombok.ToString;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "RISK_CHARGE")
public class RiskCharge implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  private double amount;

  @Embedded
  private Criteria criteria;

  protected RiskCharge() {
  }

  public RiskCharge(double amount, String operator, double threshold) {
    this.amount = amount;
    this.criteria = new Criteria(operator, threshold);
  }

  public boolean isPayable(double metricValue) {
    return !criteria.isViolated(metricValue);
  }

  public String getOperator() {
    return criteria.getOperator();
  }

  public double getThreshold() {
    return criteria.getThreshold();
  }

  @Override
  public String toString() {
    return "[" + getOperator() + " " + getThreshold() + ", amount: " + amount + "]";
  }
}
