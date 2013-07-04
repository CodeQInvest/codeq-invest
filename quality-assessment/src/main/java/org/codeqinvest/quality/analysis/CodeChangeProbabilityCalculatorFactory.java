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
package org.codeqinvest.quality.analysis;

import lombok.Getter;
import org.codeqinvest.codechanges.CodeChangeProbabilityCalculator;
import org.codeqinvest.codechanges.CommitBasedCodeChangeProbabilityCalculator;
import org.codeqinvest.codechanges.DefaultCodeChangeProbabilityCalculator;
import org.codeqinvest.codechanges.WeightedCodeChangeProbabilityCalculator;
import org.codeqinvest.codechanges.scm.factory.CodeChurnCalculatorFactory;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.SupportedCodeChangeProbabilityMethod;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fmueller
 */
@Component
class CodeChangeProbabilityCalculatorFactory {

  @Getter
  private final CodeChurnCalculatorFactory codeChurnCalculatorFactory;

  @Autowired
  CodeChangeProbabilityCalculatorFactory(CodeChurnCalculatorFactory codeChurnCalculatorFactory) {
    this.codeChurnCalculatorFactory = codeChurnCalculatorFactory;
  }

  CodeChangeProbabilityCalculator create(CodeChangeSettings codeChangeSettings) {
    if (codeChangeSettings.getMethod() == SupportedCodeChangeProbabilityMethod.WEIGHTED.getId()) {
      return new WeightedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, LocalDate.now(), codeChangeSettings.getDays());
    } else if (codeChangeSettings.getMethod() == SupportedCodeChangeProbabilityMethod.COMMIT_BASED.getId()) {
      return new CommitBasedCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, codeChangeSettings.getNumberOfCommits());
    } else {
      return new DefaultCodeChangeProbabilityCalculator(codeChurnCalculatorFactory, LocalDate.now(), codeChangeSettings.getDays());
    }
  }
}
