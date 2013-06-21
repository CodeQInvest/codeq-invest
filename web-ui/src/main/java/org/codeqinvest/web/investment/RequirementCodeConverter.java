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

import org.codeqinvest.investment.QualityInvestmentPlan;
import org.codeqinvest.investment.QualityInvestmentPlanEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * This helper class is used to convert the requirement code
 * in {@link QualityInvestmentPlanEntry} instances to a localized
 * full text representation.
 *
 * @author fmueller
 */
@Component
class RequirementCodeConverter {

  private final MessageSource messageSource;
  private final LocaleResolver localeResolver;

  @Autowired
  RequirementCodeConverter(MessageSource messageSource, LocaleResolver localeResolver) {
    this.messageSource = messageSource;
    this.localeResolver = localeResolver;
  }

  void convertRequirementCodeToLocalizedMessage(HttpServletRequest request, QualityInvestmentPlan investmentPlan) {
    for (QualityInvestmentPlanEntry entry : investmentPlan.getEntries()) {
      String code = entry.getRequirementCode();
      entry.setRequirementCode(messageSource.getMessage(code, null, localeResolver.resolveLocale(request)));
    }
  }
}
