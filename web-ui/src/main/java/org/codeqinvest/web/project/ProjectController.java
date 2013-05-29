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

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.ProjectRepository;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.QualityProfileRepository;
import org.codeqinvest.quality.analysis.QualityAnalyzerScheduler;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * This controller handles all requests for managing projects.
 *
 * @author fmueller
 */
@Slf4j
@Controller
@RequestMapping("/projects")
class ProjectController {

  private final ProjectRepository projectRepository;
  private final QualityProfileRepository profileRepository;
  private final QualityAnalyzerScheduler analyzerScheduler;
  private final ProjectConnectionsValidator projectConnectionsValidator;

  @Autowired
  ProjectController(ProjectRepository projectRepository,
                    QualityProfileRepository profileRepository,
                    QualityAnalyzerScheduler analyzerScheduler,
                    ProjectConnectionsValidator projectConnectionsValidator) {
    this.projectRepository = projectRepository;
    this.profileRepository = profileRepository;
    this.analyzerScheduler = analyzerScheduler;
    this.projectConnectionsValidator = projectConnectionsValidator;
  }

  /**
   * This controller method fills the form for creating a new project.
   */
  @RequestMapping(value = "/create", method = RequestMethod.GET)
  String initCreateForm(Model model) {
    model.addAttribute("project", createEmptyProject());
    return "addProject";
  }

  /**
   * This methods handles the submitted form for creating a new project.
   */
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  String create(@ModelAttribute Project project, Model model, BindingResult bindingResult) {
    projectConnectionsValidator.validate(project, bindingResult);
    if (bindingResult.hasErrors()) {
      log.info("Rejected creation of project due {} validation errors", bindingResult.getErrorCount());
      return "addProject";
    }

    Project addedProject = projectRepository.save(project);
    analyzerScheduler.scheduleAnalyzer(addedProject);
    model.addAttribute("project", addedProject);
    log.info("Created project {} and scheduled its quality analysis", project.getName());
    return "project";
  }

  @ModelAttribute("profiles")
  List<QualityProfile> allQualityProfiles() {
    return profileRepository.findAll();
  }

  private Project createEmptyProject() {
    return new Project("", "", null, new SonarConnectionSettings(), new ScmConnectionSettings(), new CodeChangeSettings());
  }
}
