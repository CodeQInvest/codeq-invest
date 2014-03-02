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
package org.codeqinvest.web.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.analysis.LastQualityAnalysisService;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller handle the request for detail view of
 * a given project.
 *
 * @author fmueller
 */
@Slf4j
@Controller
@RequestMapping("/projects")
class ProjectController {

  private final ProjectRepository projectRepository;
  private final LastQualityAnalysisService lastQualityAnalysisService;
  private final InvestmentOpportunitiesJsonGenerator investmentOpportunitiesJsonGenerator;

  @Autowired
  ProjectController(ProjectRepository projectRepository,
                    LastQualityAnalysisService lastQualityAnalysisService,
                    InvestmentOpportunitiesJsonGenerator investmentOpportunitiesJsonGenerator) {
    this.projectRepository = projectRepository;
    this.lastQualityAnalysisService = lastQualityAnalysisService;
    this.investmentOpportunitiesJsonGenerator = investmentOpportunitiesJsonGenerator;
  }

  /**
   * This method prepares the main site of a project to be displayed.
   */
  @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
  String showProject(@PathVariable long projectId, Model model) throws JsonProcessingException {
    Project project = projectRepository.findOne(projectId);
    QualityAnalysis lastAnalysis = lastQualityAnalysisService.retrieveLastAnalysis(project);

    model.addAttribute("currentUrl", "/projects/" + projectId);
    model.addAttribute("project", project);

    if (lastAnalysis != null) {
      model.addAttribute("lastAnalysis", lastAnalysis);
      model.addAttribute("investmentOpportunitiesJson", investmentOpportunitiesJsonGenerator.generate(lastAnalysis));
    }

    log.debug("Show project {}", project.getName());
    return "project";
  }
}
