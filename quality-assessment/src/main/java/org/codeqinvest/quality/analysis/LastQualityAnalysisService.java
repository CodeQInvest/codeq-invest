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
 * This interface describes a service that retrieves the
 * last analysis of a project from the database.
 *
 * @author fmueller
 */
public interface LastQualityAnalysisService {

  /**
   * Retrieves the last successful analysis of a project
   * from the database. Returns {@code null} if it can not
   * find one.
   */
  QualityAnalysis retrieveLastSuccessfulAnalysis(Project project);

  /**
   * Retrieves the last analysis of a project from the database - succesful or failed.
   * Returns {@code null} if it can not find one.
   */
  QualityAnalysis retrieveLastAnalysis(Project project);
}
