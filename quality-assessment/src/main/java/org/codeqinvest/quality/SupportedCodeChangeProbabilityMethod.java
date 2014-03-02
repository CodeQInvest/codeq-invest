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

import lombok.Getter;

/**
 * This enum lists all supported method for calculating
 * the code change probability.
 *
 * @author fmueller
 */
@Getter
public enum SupportedCodeChangeProbabilityMethod {

  DEFAULT(0, "code.change.method.default"),
  WEIGHTED(1, "code.change.method.weighted"),
  COMMIT_BASED(2, "code.change.method.commitBased");

  private final int id;
  private final String messageSourceIdentifier;

  private SupportedCodeChangeProbabilityMethod(int id, String messageSourceIdentifier) {
    this.id = id;
    this.messageSourceIdentifier = messageSourceIdentifier;
  }
}
