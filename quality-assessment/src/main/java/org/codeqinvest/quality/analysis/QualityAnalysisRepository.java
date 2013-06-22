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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author fmueller
 */
public interface QualityAnalysisRepository extends JpaRepository<QualityAnalysis, Long> {

  List<QualityAnalysis> findByProjectOrderByCreatedDesc(Project project);

  List<QualityAnalysis> findByProjectAndSuccessfulOrderByCreatedDesc(Project project, boolean successful);

  @Query("select q from QualityAnalysis q left join fetch q.violations where q.id = ?1")
  QualityAnalysis findOneByIdWithViolations(Long id);
}
