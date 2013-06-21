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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author fmueller
 */
@Service
class DefaultLastQualityAnalysisService implements LastQualityAnalysisService {

  private final QualityAnalysisRepository qualityAnalysisRepository;

  @Autowired
  DefaultLastQualityAnalysisService(QualityAnalysisRepository qualityAnalysisRepository) {
    this.qualityAnalysisRepository = qualityAnalysisRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public QualityAnalysis retrieveLastSuccessfulAnalysis(Project project) {
    List<QualityAnalysis> allAnalysis = qualityAnalysisRepository.findByProjectAndSuccessfulOrderByCreatedDesc(project, true);
    return (allAnalysis != null && !allAnalysis.isEmpty())
        ? qualityAnalysisRepository.findOneByIdWithViolations(allAnalysis.get(0).getId())
        : null;
  }
}
