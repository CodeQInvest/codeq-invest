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
import javax.persistence.Embeddable;

/**
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@ToString
@Embeddable
public class CodeChangeSettings {

  public static final int DEFAULT_METHOD = 0;
  public static final int WEIGHTED_METHOD = 1;

  @Column(name = "CODE_CHANGE_METHOD", nullable = false)
  private int method;

  @Column(name = "CODE_CHANGE_DAYS")
  private int days;

  protected CodeChangeSettings() {
  }

  public CodeChangeSettings(int method, int days) {
    this.method = method;
    this.days = days;
  }

  public static CodeChangeSettings defaultSetting(int days) {
    return new CodeChangeSettings(DEFAULT_METHOD, days);
  }

  public static CodeChangeSettings weightedSetting(int days) {
    return new CodeChangeSettings(WEIGHTED_METHOD, days);
  }
}
