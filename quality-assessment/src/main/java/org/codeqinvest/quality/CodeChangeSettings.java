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
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fmueller
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
public class CodeChangeSettings implements Serializable {

  private static final int DEFAULT_DAYS = 30;

  @Column(name = "CODE_CHANGE_METHOD", nullable = false)
  private int method;

  @Column(name = "CODE_CHANGE_DAYS")
  private int days;

  public CodeChangeSettings() {
    this(SupportedCodeChangeProbabilityMethod.DEFAULT.getId(), DEFAULT_DAYS);
  }

  public CodeChangeSettings(int method, int days) {
    this.method = method;
    this.days = days;
  }

  public static CodeChangeSettings defaultSetting(int days) {
    return new CodeChangeSettings(SupportedCodeChangeProbabilityMethod.DEFAULT.getId(), days);
  }

  public static CodeChangeSettings weightedSetting(int days) {
    return new CodeChangeSettings(SupportedCodeChangeProbabilityMethod.WEIGHTED.getId(), days);
  }

  /**
   * Returns the IDs of all supported methods for calculating the code
   * change probability.
   *
   * @see org.codeqinvest.quality.SupportedCodeChangeProbabilityMethod
   */
  public static List<Integer> getSupportedMethods() {
    List<Integer> methods = new ArrayList<Integer>();
    for (SupportedCodeChangeProbabilityMethod supportedMethod : SupportedCodeChangeProbabilityMethod.values()) {
      methods.add(supportedMethod.getId());
    }
    return methods;
  }
}
