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

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fmueller
 */
@Component
public class InvestmentAmountParser {

  public int parseMinutes(String formattedInvestmentAmount) throws InvestmentParsingException {
    if (formattedInvestmentAmount.isEmpty()) {
      return 0;
    }

    Pattern pattern = Pattern.compile("(\\d*h)?\\s?(\\d*m)?");
    Matcher matcher = pattern.matcher(formattedInvestmentAmount);

    int minutes = 0;
    if (!matcher.find()) {
      throw new InvestmentParsingException();
    }

    String[] timeValues = matcher.group().split("\\s");
    minutes += getMinutesFor(timeValues[0]);
    if (timeValues.length > 1) {
      minutes += getMinutesFor(timeValues[1]);
    }
    return minutes;
  }

  private int getMinutesFor(String timeValue) throws InvestmentParsingException {
    try {
      int parsedValue = Integer.parseInt(timeValue.substring(0, timeValue.length() - 1));
      if (timeValue.charAt(timeValue.length() - 1) == 'h') {
        return 60 * parsedValue;
      } else {
        return parsedValue;
      }
    } catch (StringIndexOutOfBoundsException e) {
      throw new InvestmentParsingException(e);
    }
  }
}
