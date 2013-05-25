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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode(exclude = "profile")
@ToString(exclude = "profile")
@Entity
@Table(name = "QUALITY_REQUIREMENT")
public class QualityRequirement implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "PROFILE_ID", nullable = false, updatable = false)
  private QualityProfile profile;

  @Column(nullable = false)
  private int remediationCosts;

  @Column(nullable = false)
  private int nonRemediationCosts;

  @Column(nullable = false)
  private double weightingMetricValue;

  @Column(nullable = false, length = 50)
  private String weightingMetricIdentifier;

  @Embedded
  private QualityCriteria criteria;

  protected QualityRequirement() {
  }

  public QualityRequirement(QualityProfile profile, int remediationCosts, int nonRemediationCosts,
                            double weightingMetricValue, String weightingMetricIdentifier, QualityCriteria criteria) {
    this.profile = profile;
    this.remediationCosts = remediationCosts;
    this.nonRemediationCosts = nonRemediationCosts;
    this.weightingMetricValue = weightingMetricValue;
    this.weightingMetricIdentifier = weightingMetricIdentifier;
    this.criteria = criteria;
  }

  public boolean isViolated(double metricValue) {
    return criteria.isViolated(metricValue);
  }

  public String getMetricIdentifier() {
    return criteria.getMetricIdentifier();
  }

  public double getThreshold() {
    return criteria.getThreshold();
  }

  public String getOperator() {
    return criteria.getOperator();
  }
}
