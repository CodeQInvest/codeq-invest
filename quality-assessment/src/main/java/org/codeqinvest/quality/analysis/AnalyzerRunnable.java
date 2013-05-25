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
import org.codeqinvest.quality.ProjectRepository;

/**
 * This {@code Runnable} implementation is used in the
 * quality analyzer scheduler to encapsulate the job logic.
 *
 * @author fmueller
 */
@Slf4j
class AnalyzerRunnable implements Runnable {

  private final long projectId;
  private final ProjectRepository projectRepository;
  private final QualityAnalyzerService qualityAnalyzerService;

  public AnalyzerRunnable(Project project, ProjectRepository projectRepository, QualityAnalyzerService qualityAnalyzerService) {
    this.projectId = project.getId();
    this.projectRepository = projectRepository;
    this.qualityAnalyzerService = qualityAnalyzerService;
  }

  @Override
  public void run() {
    Project project = projectRepository.findOne(projectId);
    if (project != null) {
      log.info("Start analyzer run for project {}", project.getName());
      qualityAnalyzerService.analyzeProject(project);
      log.info("Finished analyzer run for project {}", project.getName());
    } else {
      log.error("Could not find project with id " + projectId + " for starting an analyzer run!");
    }
  }
}