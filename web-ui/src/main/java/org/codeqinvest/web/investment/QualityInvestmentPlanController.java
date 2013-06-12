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

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.investment.QualityInvestmentPlan;
import org.codeqinvest.investment.QualityInvestmentPlanService;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.analysis.LastQualityAnalysisService;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fmueller
 */
@Slf4j
@Controller
class QualityInvestmentPlanController {

  private final ProjectRepository projectRepository;
  private final LastQualityAnalysisService lastQualityAnalysisService;
  private final QualityInvestmentPlanService investmentPlanService;
  private final InvestmentAmountParser investmentAmountParser;

  @Autowired
  QualityInvestmentPlanController(ProjectRepository projectRepository,
                                  LastQualityAnalysisService lastQualityAnalysisService,
                                  QualityInvestmentPlanService investmentPlanService,
                                  InvestmentAmountParser investmentAmountParser) {
    this.projectRepository = projectRepository;
    this.lastQualityAnalysisService = lastQualityAnalysisService;
    this.investmentPlanService = investmentPlanService;
    this.investmentAmountParser = investmentAmountParser;
  }

  @RequestMapping(value = "/projects/{projectId}/investment", method = RequestMethod.PUT)
  @ResponseBody
  QualityInvestmentPlan generateInvestmentPlan(@PathVariable long projectId, @RequestBody InvestmentPlanRequest investmentPlanRequest) throws InvestmentParsingException {
    Project project = projectRepository.findOne(projectId);
    QualityAnalysis lastAnalysis = lastQualityAnalysisService.retrieveLastAnalysis(project);
    int investmentInMinutes = investmentAmountParser.parseMinutes(investmentPlanRequest.getInvestment());
    return investmentPlanService.computeInvestmentPlan(lastAnalysis, investmentPlanRequest.getBasePackage(), investmentInMinutes);
  }
}
