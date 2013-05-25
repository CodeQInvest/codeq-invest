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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This component is executed on startup of the web application
 * and scans for projects in the database. It schedules for
 * each project a corresponding job that will be executed according
 * to the cron setting of the project.
 *
 * @author fmueller
 */
@Slf4j
@Component
class QualityAnalyzerRunsSchedulerOnStartup implements ApplicationListener<ContextRefreshedEvent> {

  private final QualityAnalyzerScheduler analyzerScheduler;
  private final ProjectRepository projectRepository;

  private final AtomicBoolean alreadyExecuted = new AtomicBoolean(false);

  @Autowired
  QualityAnalyzerRunsSchedulerOnStartup(QualityAnalyzerScheduler analyzerScheduler, ProjectRepository projectRepository) {
    this.analyzerScheduler = analyzerScheduler;
    this.projectRepository = projectRepository;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (alreadyExecuted.compareAndSet(false, true)) {
      List<Project> projects = projectRepository.findAll();
      log.info("Schedule analyzer jobs for {} projects", projects.size());
      for (Project project : projects) {
        analyzerScheduler.scheduleAnalyzer(project);
      }
    } else {
      log.info("Projects already scheduled.");
    }
  }
}
