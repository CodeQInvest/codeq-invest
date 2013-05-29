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
package org.codeqinvest.web.sonar;

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.sonar.SonarConnectionCheckerService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author fmueller
 */
@Slf4j
@Controller
@RequestMapping("/sonar")
class SonarController {

  private final SonarConnectionCheckerService sonarConnectionCheckerService;
  private final SonarServerValidator sonarServerValidator;

  @Autowired
  SonarController(SonarConnectionCheckerService sonarConnectionCheckerService, SonarServerValidator sonarServerValidator) {
    this.sonarConnectionCheckerService = sonarConnectionCheckerService;
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
      // could be improved with exception and corresponding exception handler
      response.setStatus(400);
      return null;
    }

    SonarConnectionSettings connectionSettings = new SonarConnectionSettings(sonarServer.getUrl());
    return new SonarReachableStatus(sonarConnectionCheckerService.isReachable(connectionSettings));
  }
}
