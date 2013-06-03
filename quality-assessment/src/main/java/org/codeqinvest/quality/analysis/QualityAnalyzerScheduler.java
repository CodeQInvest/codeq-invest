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

import org.codeqinvest.quality.Project;

/**
 * This interface describes a component that can be used
 * for executing and scheduling execution of analyzer runs.
 *
 * @author fmueller
 */
public interface QualityAnalyzerScheduler {

  /**
   * Executes a quality analyzer run for the given project.
   */
  void executeAnalyzer(Project project);

  /**
   * This method tries to schedule a {@link QualityAnalyzerService} for the given
   * project. The cron expression from the project is used as configuration for
   * a cron trigger. A project can not be scheduled twice.
   *
   * @return {@code true} if the project was scheduled for analyzer runs,
   *         {@code false} if the project could not be scheduled due it is already scheduled
   */
  boolean scheduleAnalyzer(Project project);
}
