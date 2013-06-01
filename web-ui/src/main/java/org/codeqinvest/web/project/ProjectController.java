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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.factory.SupportedScmSystem;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.SupportedCodeChangeProbabilityMethod;
import org.codeqinvest.quality.analysis.QualityAnalyzerScheduler;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.codeqinvest.quality.repository.QualityProfileRepository;
import org.codeqinvest.sonar.ProjectInformation;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
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

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
    return "createProject";
  }

  /**
   * This methods handles the submitted form for creating a new project.
   */
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  String create(@ModelAttribute Project project,
                BindingResult bindingResult,
                @ModelAttribute("retrievedSonarProjectsAsJson") String sonarProjects,
                Model model) {

    projectConnectionsValidator.validate(project, bindingResult);
    if (bindingResult.hasErrors()) {
      log.info("Rejected creation of project due {} validation errors", bindingResult.getErrorCount());
      if (log.isDebugEnabled()) {
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
          log.debug("Field {} has following error: {}", fieldError.getField(), fieldError.getCode());
        }
      }
      addDeserializedSonarProjectsToModel(sonarProjects, model);
      model.addAttribute("fieldErrors", bindingResult.getFieldErrors());
      return "createProject";
    }

    Project addedProject = projectRepository.save(project);
    analyzerScheduler.scheduleAnalyzer(addedProject);
    model.addAttribute("project", addedProject);
    log.info("Created project {} and scheduled its quality analysis", project.getName());
    return "project";
  }

  private void addDeserializedSonarProjectsToModel(String jsonString, Model model) {
    if (!Strings.isNullOrEmpty(jsonString)) {
      try {
        ProjectInformation[] sonarProjects = OBJECT_MAPPER.readValue(jsonString, ProjectInformation[].class);
        model.addAttribute("retrievedSonarProjects", sonarProjects);
      } catch (IOException e) {
        log.error("Could not parse sonar projects json tree!", e);
        throw new SonarProjectsJsonDeserializationException(e);
      }
    }
  }

  @ModelAttribute("currentUrl")
  String currentUrl() {
    return "/projects/create";
  }

  @ModelAttribute("profiles")
  List<QualityProfile> allQualityProfiles() {
    return profileRepository.findAll();
  }

  @ModelAttribute("codeChangeMethods")
  List<SupportedCodeChangeProbabilityMethod> allCodeChangeProbabilityMethods() {
    return Lists.newArrayList(SupportedCodeChangeProbabilityMethod.values());
  }

  @ModelAttribute("supportedScmSystems")
  List<SupportedScmSystem> allSupportedScmSystems() {
    return Lists.newArrayList(SupportedScmSystem.values());
  }

  private Project createEmptyProject() {
    return new Project("", "", null, new SonarConnectionSettings(), new ScmConnectionSettings(), new CodeChangeSettings());
  }
}
