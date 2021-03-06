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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CriteriaViolationBehaviorTest {

  private static final boolean VIOLATED = true;
  private static final boolean NOT_VIOLATED = false;

  private final String operator;
  private final double threshold;
  private final double currentValue;
  private final boolean shouldBeViolated;

  public CriteriaViolationBehaviorTest(String operator, double threshold, double currentValue, boolean shouldBeViolated) {
    this.operator = operator;
    this.threshold = threshold;
    this.currentValue = currentValue;
    this.shouldBeViolated = shouldBeViolated;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    Object[][] testData = new Object[][]{
        {"<", 20.0, 19, NOT_VIOLATED},
        {"<", 20.0, 20, VIOLATED},
        {"<", 20.0, 21, VIOLATED},
        {">", 20.0, 21, NOT_VIOLATED},
        {">", 20.0, 20, VIOLATED},
        {">", 20.0, 19, VIOLATED},
        {"!=", 20.0, 21, NOT_VIOLATED},
        {"!=", 20.0, 19, NOT_VIOLATED},
        {"!=", 20.0, 20, VIOLATED},
        {"=", 20.0, 21, VIOLATED},
        {"=", 20.0, 19, VIOLATED},
        {"=", 20.0, 20, NOT_VIOLATED},
        {"<=", 20.0, 21, VIOLATED},
        {"<=", 20.0, 19, NOT_VIOLATED},
        {"<=", 20.0, 20, NOT_VIOLATED},
        {">=", 20.0, 21, NOT_VIOLATED},
        {">=", 20.0, 19, VIOLATED},
        {">=", 20.0, 20, NOT_VIOLATED}
    };
    return Arrays.asList(testData);
  }

  @Test
  public void testIfCriteriaIsViolated() {
    Criteria criteria = new Criteria(operator, threshold);
    if (shouldBeViolated) {
      assertThat(criteria.isViolated(currentValue))
          .as("Criteria " + criteria + " should be violated for value " + currentValue)
          .isTrue();
    } else {
      assertThat(criteria.isViolated(currentValue))
          .as("Criteria " + criteria + " should not be violated for value " + currentValue)
          .isFalse();
    }
  }
}
