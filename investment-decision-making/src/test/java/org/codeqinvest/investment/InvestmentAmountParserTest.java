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

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class InvestmentAmountParserTest {

  private InvestmentAmountParser parser;

  @Before
  public void setUp() {
    parser = new InvestmentAmountParser();
  }

  @Test
  public void emptyStringShouldBeParsedToZeroMinutes() throws InvestmentParsingException {
    assertThat(parser.parseMinutes("")).isEqualTo(0);
  }

  @Test
  public void shouldParseHoursProperly() throws InvestmentParsingException {
    assertThat(parser.parseMinutes("3h")).isEqualTo(180);
  }

  @Test
  public void shouldParseMinutesProperly() throws InvestmentParsingException {
    assertThat(parser.parseMinutes("21m")).isEqualTo(21);
  }

  @Test
  public void shouldParseHoursAndMinutesProperly() throws InvestmentParsingException {
    assertThat(parser.parseMinutes("3h 70m")).isEqualTo(250);
  }

  @Test(expected = InvestmentParsingException.class)
  public void shouldThrowExceptionWhenGivenStringIsNotParsable() throws InvestmentParsingException {
    parser.parseMinutes("abc0m");
  }
}
