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

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.factory.ScmAvailabilityCheckerServiceFactory;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author fmueller
 */
@Slf4j
@Component
public class QualityAnalyzerScheduler {

  private static final int DEFAULT_POOL_SIZE = 10;

  private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

  private final ProjectRepository projectRepository;
  private final ViolationsCalculatorService violationsCalculatorService;
  private final ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory;
  private final CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory;
  private final QualityViolationCostsCalculator costsCalculator;
  private final QualityAnalysisRepository qualityAnalysisRepository;

  private final Set<Project> alreadyScheduledProjects = Sets.newCopyOnWriteArraySet();

  @Autowired
  public QualityAnalyzerScheduler(ProjectRepository projectRepository,
                                  ViolationsCalculatorService violationsCalculatorService,
                                  ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory,
                                  CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory,
                                  QualityViolationCostsCalculator costsCalculator,
                                  QualityAnalysisRepository qualityAnalysisRepository) {
    this.projectRepository = projectRepository;
    this.violationsCalculatorService = violationsCalculatorService;
    this.scmAvailabilityCheckerServiceFactory = scmAvailabilityCheckerServiceFactory;
    this.codeChangeProbabilityCalculatorFactory = codeChangeProbabilityCalculatorFactory;
    this.costsCalculator = costsCalculator;
    this.qualityAnalysisRepository = qualityAnalysisRepository;

    scheduler.setPoolSize(DEFAULT_POOL_SIZE);
    scheduler.initialize();
  }

  public boolean scheduleAnalyzer(Project project) {
    if (alreadyScheduledProjects.contains(project)) {
      log.info("Project {} is already scheduled!", project.getName());
      return false;
    }
    alreadyScheduledProjects.add(project);

    QualityAnalyzerService qualityAnalyzerService = new QualityAnalyzerService(violationsCalculatorService,
        scmAvailabilityCheckerServiceFactory,
        codeChangeProbabilityCalculatorFactory,
        costsCalculator,
        qualityAnalysisRepository);

    scheduler.schedule(new AnalyzerRunnable(project, projectRepository, qualityAnalyzerService), new CronTrigger(project.getCronExpression()));
    log.info("Scheduled analyzer job for project {} with cron expression {}", project.getName(), project.getCronExpression());
    return true;
  }
}
