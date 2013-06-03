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
package org.codeqinvest.web.project;

import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller handle the request for detail view of
 * a given project.
 *
 * @author fmueller
 */
@Controller
@RequestMapping("/projects")
class ProjectController {

  private final ProjectRepository projectRepository;

  @Autowired
  ProjectController(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  /**
   * This method prepares the main site of a project to be displayed.
   */
  @RequestMapping("/{projectId}")
  String showProject(@PathVariable long projectId, Model model) {
    Project project = projectRepository.findOne(projectId);
    model.addAttribute("currentUrl", "/projects/" + projectId);
    model.addAttribute("project", project);
    model.addAttribute("investmentOpportunitiesJson", "{ \"name\": \"No Data\", \"value\": 0, \"children\": []}");
    return "project";
  }
}
