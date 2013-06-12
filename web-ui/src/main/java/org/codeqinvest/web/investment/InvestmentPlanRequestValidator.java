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
package org.codeqinvest.web.investment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
class InvestmentPlanRequestValidator implements Validator {

  private final InvestmentAmountParser investmentAmountParser;

  @Autowired
  InvestmentPlanRequestValidator(InvestmentAmountParser investmentAmountParser) {
    this.investmentAmountParser = investmentAmountParser;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return InvestmentPlanRequest.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    InvestmentPlanRequest request = (InvestmentPlanRequest) target;
    if (request.getBasePackage() == null) {
      errors.rejectValue("basePackage", "null");
    }

    try {
      investmentAmountParser.parseMinutes(request.getInvestment());
    } catch (InvestmentParsingException e) {
      errors.rejectValue("investment", "not.valid");
    }
  }
}
