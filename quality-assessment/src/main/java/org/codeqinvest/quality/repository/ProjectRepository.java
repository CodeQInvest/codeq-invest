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
package org.codeqinvest.quality.repository;

import org.codeqinvest.quality.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author fmueller
 */
@Transactional(readOnly = true)
public interface ProjectRepository extends JpaRepository<Project, Long> {

  Project findOneByLowercaseName(String lowercaseName);

  /**
   * Retrieves all projects from the database but only loads the id and the
   * name of each project.
   */
  @Query("select new org.codeqinvest.quality.repository.BasicProjectInformation(p.id, p.name) from Project p")
  List<BasicProjectInformation> findAllBasicInformation();
}
