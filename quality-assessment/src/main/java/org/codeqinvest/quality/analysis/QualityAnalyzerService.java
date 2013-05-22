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

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is the main service of the quality assessment module. It
 * offers functionalities to analyze a given project. For that,
 * it collects all the necessary data from Sonar and the
 * corresponding source code management system. Before it performs
 * these steps, it checks for the availability of these third-party
 * systems.
 *
 * @author fmueller
 */
@Slf4j
@Service
public class QualityAnalyzerService {

  private final ViolationsCalculatorService violationsCalculatorService;
  private final CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory;

  @Autowired
  public QualityAnalyzerService(ViolationsCalculatorService violationsCalculatorService,
                                CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory) {
    this.violationsCalculatorService = violationsCalculatorService;
    this.codeChangeProbabilityCalculatorFactory = codeChangeProbabilityCalculatorFactory;
  }

  public QualityAnalysis analyzeProject(Project project) {
    // TODO implement
    return null;
  }
}
