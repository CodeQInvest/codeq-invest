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

import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode(exclude = "profile")
@ToString(exclude = "profile")
@Entity
@Table(name = "CHANGE_RISK_FUNCTION")
public class ChangeRiskAssessmentFunction implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "PROFILE_ID", nullable = false, updatable = false)
  private QualityProfile profile;

  @Column(nullable = false, length = 50)
  private String metricIdentifier;

  @OneToMany(cascade = CascadeType.ALL)
  private List<RiskCharge> riskCharges = new ArrayList<RiskCharge>();

  private static class SortRiskChargeByThresholdAscending implements Comparator<RiskCharge>, Serializable {

    @Override
    public int compare(RiskCharge riskCharge, RiskCharge otherRiskCharge) {
      if (riskCharge.getThreshold() < otherRiskCharge.getThreshold()) {
        return 1;
      } else if (riskCharge.getThreshold() > otherRiskCharge.getThreshold()) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  private static class SortRiskChargeByThresholdDescending implements Comparator<RiskCharge>, Serializable {

    private final SortRiskChargeByThresholdAscending sortAscending = new SortRiskChargeByThresholdAscending();

    @Override
    public int compare(RiskCharge riskCharge, RiskCharge otherRiskCharge) {
      return -1 * sortAscending.compare(riskCharge, otherRiskCharge);
    }
  }

  protected ChangeRiskAssessmentFunction() {
  }

  public ChangeRiskAssessmentFunction(QualityProfile profile, String metricIdentifier, List<RiskCharge> riskCharges) {
    if (riskCharges.isEmpty()) {
      throw new IllegalArgumentException();
    }
    validateRiskCharges(riskCharges);
    this.profile = profile;
    this.metricIdentifier = metricIdentifier;
    this.riskCharges = riskCharges;
  }

  public double getRiskChargeAmount(double metricValue) {
    RiskCharge currentRiskCharge = null;
    for (RiskCharge riskCharge : sortByThreshold(riskCharges)) {
      if ((currentRiskCharge == null && riskCharge.isPayable(metricValue)) || riskCharge.isPayable(metricValue)) {
        currentRiskCharge = riskCharge;
      } else {
        break;
      }
    }
    return currentRiskCharge != null ? currentRiskCharge.getAmount() : 0.0;
  }

  private List<RiskCharge> sortByThreshold(List<RiskCharge> riskCharges) {
    List<RiskCharge> sortedRiskCharges = new ArrayList<RiskCharge>(riskCharges);
    String operator = getUsedOperatorOfRiskCharges(riskCharges);
    if (operator.equals("<") || operator.equals("<=")) {
      Collections.sort(sortedRiskCharges, new SortRiskChargeByThresholdAscending());
    } else if (operator.equals(">") || operator.equals(">=")) {
      Collections.sort(sortedRiskCharges, new SortRiskChargeByThresholdDescending());
    }
    return sortedRiskCharges;
  }

  private String getUsedOperatorOfRiskCharges(List<RiskCharge> riskCharges) {
    return riskCharges.iterator().next().getOperator();
  }

  private void validateRiskCharges(List<RiskCharge> riskCharges) {
    validateSameOperator(riskCharges);
    validateDifferentThresholds(riskCharges);
    validateAllowedOperator(riskCharges);
  }

  private void validateSameOperator(List<RiskCharge> riskCharges) {
    String operator = null;
    for (RiskCharge riskCharge : riskCharges) {
      if (operator == null) {
        operator = riskCharge.getOperator();
      } else if (!operator.equals(riskCharge.getOperator())) {
        throw new IllegalArgumentException();
      }
    }
  }

  private void validateDifferentThresholds(List<RiskCharge> riskCharges) {
    Set<Double> thresholds = Sets.newHashSet();
    for (RiskCharge riskCharge : riskCharges) {
      if (thresholds.contains(riskCharge.getThreshold())) {
        throw new IllegalArgumentException();
      }
      thresholds.add(riskCharge.getThreshold());
    }
  }

  private void validateAllowedOperator(List<RiskCharge> riskCharges) {
    for (RiskCharge riskCharge : riskCharges) {
      if (riskCharge.getOperator().equals("=") || riskCharge.getOperator().equals("!=")) {
        throw new IllegalArgumentException();
      }
    }
  }
}
