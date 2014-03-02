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
package org.codeqinvest.web.sonar;

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.sonar.ProjectInformation;
import org.codeqinvest.sonar.ProjectsCollectorService;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * This controller handles various request made by frontend
 * JavaScript code to get some information about a given Sonar
 * server.
 *
 * @author fmueller
 */
@Slf4j
@Controller
@RequestMapping("/sonar")
class SonarController {

  private static final int BAD_REQUEST = 400;

  private final SonarConnectionCheckerService sonarConnectionCheckerService;
  private final ProjectsCollectorService projectsCollectorService;
  private final SonarServerValidator sonarServerValidator;

  @Autowired
  SonarController(SonarConnectionCheckerService sonarConnectionCheckerService,
                  ProjectsCollectorService projectsCollectorService,
                  SonarServerValidator sonarServerValidator) {
    this.sonarConnectionCheckerService = sonarConnectionCheckerService;
    this.projectsCollectorService = projectsCollectorService;
    this.sonarServerValidator = sonarServerValidator;
  }

  /**
   * This route can be used by JavaScript frontend code to verify if a
   * given Sonar server is reachable.
   */
  @RequestMapping(value = "/reachable", method = RequestMethod.PUT)
  @ResponseBody
  SonarReachableStatus isSonarServerReachable(@RequestBody SonarServer sonarServer, BindingResult errors, HttpServletResponse response) {
    sonarServerValidator.validate(sonarServer, errors);
    if (errors.hasErrors()) {
      // TODO could be improved with exception and corresponding exception handler
      log.info("Rejected checking Sonar server {} due {} validation errors", sonarServer, errors.getErrorCount());
      response.setStatus(BAD_REQUEST);
      return null;
    }

    boolean isReachable = sonarConnectionCheckerService.isReachable(sonarServer.getConnectionSettings());
    log.info("Sonar server at {} is reachable: {}", sonarServer, isReachable);
    return new SonarReachableStatus(isReachable);
  }

  /**
   * This route can be used by JavaScript frontend code to retrieve all available
   * projects of a given Sonar server.
   */
  @RequestMapping(value = "/projects", method = RequestMethod.PUT)
  @ResponseBody
  Set<ProjectInformation> allProjects(@RequestBody SonarServer sonarServer, BindingResult errors, HttpServletResponse response) {
    sonarServerValidator.validate(sonarServer, errors);
    if (errors.hasErrors()) {
      // TODO could be improved with exception and corresponding exception handler
      log.info("Rejected retrieving all projects from Sonar server {} due {} validation errors", sonarServer, errors.getErrorCount());
      response.setStatus(BAD_REQUEST);
      return null;
    }

    Set<ProjectInformation> projects = projectsCollectorService.collectAllProjects(sonarServer.getConnectionSettings());
    log.info("Found {} projects for sonar server at {}", projects.size(), sonarServer);
    return projects;
  }
}
