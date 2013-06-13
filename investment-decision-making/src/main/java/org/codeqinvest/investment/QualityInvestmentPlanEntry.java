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
package org.codeqinvest.investment;

import lombok.Data;

/**
 * @author fmueller
 */
@Data
public class QualityInvestmentPlanEntry implements Comparable<QualityInvestmentPlanEntry> {

  private String requirementCode;

  private final String violatedConstraint;
  private final String artefactLongName;
  private final String artefactShortName;
  private final int profitInMinutes;
  private final int remediationCostsInMinutes;

  public QualityInvestmentPlanEntry(String requirementCode, String violatedConstraint, String artefactLongName,
                                    String artefactShortName, int profitInMinutes, int remediationCostsInMinutes) {
    this.requirementCode = requirementCode;
    this.violatedConstraint = violatedConstraint;
    this.artefactLongName = artefactLongName;
    this.artefactShortName = artefactShortName;
    this.profitInMinutes = profitInMinutes;
    this.remediationCostsInMinutes = remediationCostsInMinutes;
  }

  @Override
  public int compareTo(QualityInvestmentPlanEntry other) {
    if (profitInMinutes < other.getProfitInMinutes()) {
      return 1;
    } else if (profitInMinutes > other.getProfitInMinutes()) {
      return -1;
    } else if (remediationCostsInMinutes < other.getRemediationCostsInMinutes()) {
      return 1;
    } else if (remediationCostsInMinutes > other.getRemediationCostsInMinutes()) {
      return -1;
    } else {
      return 0;
    }
  }
}
