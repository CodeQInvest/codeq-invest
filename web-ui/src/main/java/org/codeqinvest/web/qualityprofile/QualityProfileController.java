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
package org.codeqinvest.web.qualityprofile;

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.QualityProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author fmueller
 */
@Slf4j
@Controller
@RequestMapping("/qualityprofiles")
class QualityProfileController {

  private final QualityProfileRepository profileRepository;

  @Autowired
  QualityProfileController(QualityProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  String create(@ModelAttribute QualityProfile profile) {
    // TODO this is only a dummy implementation
    profileRepository.save(profile);
    log.info("Created new quality profile {} with {} requirements and {} change risk functions", profile.getName(),
        profile.getRequirements().size(), profile.getChangeRiskAssessmentFunctions().size());
    return "overview";
  }
}
