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
package org.codeqinvest.web.quality.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.LastQualityAnalysisService;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.codeqinvest.quality.analysis.QualityAnalysisRepository;
import org.codeqinvest.quality.repository.ProjectRepository;
import org.codeqinvest.web.project.InvestmentOpportunitiesJsonGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author fmueller
 */
@Slf4j
@Controller
class ManualEstimatesController {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final ProjectRepository projectRepository;
  private final QualityAnalysisRepository qualityAnalysisRepository;
  private final LastQualityAnalysisService lastQualityAnalysisService;
  private final InvestmentOpportunitiesJsonGenerator investmentOpportunitiesJsonGenerator;

  @Autowired
  ManualEstimatesController(ProjectRepository projectRepository,
                            QualityAnalysisRepository qualityAnalysisRepository,
                            LastQualityAnalysisService lastQualityAnalysisService,
                            InvestmentOpportunitiesJsonGenerator investmentOpportunitiesJsonGenerator) {
    this.projectRepository = projectRepository;
    this.qualityAnalysisRepository = qualityAnalysisRepository;
    this.lastQualityAnalysisService = lastQualityAnalysisService;
    this.investmentOpportunitiesJsonGenerator = investmentOpportunitiesJsonGenerator;
  }

  @RequestMapping(value = "/projects/{projectId}/estimates", method = RequestMethod.PUT)
  @ResponseBody
  JsonNode updateManualEstimates(@PathVariable long projectId, @RequestBody Set<ManualEstimate> manualEstimates) throws IOException {
    Project project = projectRepository.findOne(projectId);
    QualityAnalysis lastAnalysis = lastQualityAnalysisService.retrieveLastAnalysis(project);

    for (ManualEstimate manualEstimate : manualEstimates) {
      for (Artefact artefact : findArtefacts(lastAnalysis, manualEstimate.getArtefact())) {
        if (manualEstimate.getEstimate() != null) {
          artefact.setManualEstimate(Integer.parseInt(manualEstimate.getEstimate()));
        } else {
          artefact.setManualEstimate(null);
        }
      }
    }

    qualityAnalysisRepository.save(lastAnalysis);
    log.info("Updated {} artefacts with manual estimates for project {}", manualEstimates.size(), project.getName());
    return MAPPER.readTree(investmentOpportunitiesJsonGenerator.generate(lastAnalysis));
  }

  private Collection<Artefact> findArtefacts(QualityAnalysis analysis, String artefactName) {
    List<Artefact> artefacts = new ArrayList<Artefact>();
    for (QualityViolation violation : analysis.getViolations()) {
      String currentArtefactName = violation.getArtefact().getName();
      if (currentArtefactName.equals(artefactName) || currentArtefactName.startsWith(artefactName)) {
        artefacts.add(violation.getArtefact());
      }
    }
    return artefacts;
  }
}
