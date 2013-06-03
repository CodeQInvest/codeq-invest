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
package org.codeqinvest.web.quality.analysis;

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.analysis.QualityAnalyzerScheduler;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller handles all requests for executing quality analyzer runs.
 *
 * @author fmueller
 */
@Slf4j
@Controller
class QualityAnalysisController {

  private final QualityAnalyzerScheduler analyzerScheduler;
  private final ProjectRepository projectRepository;

  @Autowired
  QualityAnalysisController(QualityAnalyzerScheduler analyzerScheduler, ProjectRepository projectRepository) {
    this.analyzerScheduler = analyzerScheduler;
    this.projectRepository = projectRepository;
  }

  /**
   * This method handles the manual execution of a quality analysis for
   * the specified project.
   */
  @RequestMapping("/projects/{projectId}/analyze")
  String analyzeProject(@PathVariable long projectId) {
    Project project = projectRepository.findOne(projectId);
    if (project != null) {
      analyzerScheduler.executeAnalyzer(project);
    }
    return "redirect:/projects/" + projectId;
  }
}
