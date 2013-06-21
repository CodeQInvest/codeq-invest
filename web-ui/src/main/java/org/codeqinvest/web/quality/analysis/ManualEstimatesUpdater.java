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

import org.codeqinvest.quality.analysis.QualityAnalysis;

import java.util.Set;

/**
 * This interface describes a service for updating manual estimates
 * of a given {@link org.codeqinvest.quality.analysis.QualityAnalysis}.
 *
 * @author fmueller
 */
interface ManualEstimatesUpdater {

  /**
   * Updates all artefacts of the analysis's violations with the given set of
   * manual estimates.
   *
   * @return the updated analysis instance
   */
  QualityAnalysis updateManualEstimates(QualityAnalysis analysis, Set<ManualEstimate> manualEstimates);
}
