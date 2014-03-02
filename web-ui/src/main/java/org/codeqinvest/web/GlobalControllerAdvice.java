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
package org.codeqinvest.web;

import org.codeqinvest.quality.repository.BasicProjectInformation;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller advice adds global data to each controller
 * and handles exceptions.
 *
 * @author fmueller
 */
@ControllerAdvice
class GlobalControllerAdvice {

  private final ProjectRepository projectRepository;

  @Autowired
  GlobalControllerAdvice(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  /**
   * Adds all projects to each controller as model attribute.
   */
  @ModelAttribute("projects")
  List<ProjectNavBarInformation> allProjects() {
    List<ProjectNavBarInformation> projectsInformation = new ArrayList<ProjectNavBarInformation>();
    for (BasicProjectInformation project : projectRepository.findAllBasicInformation()) {
      projectsInformation.add(new ProjectNavBarInformation(project.getId(), project.getName()));
    }
    return projectsInformation;
  }
}
