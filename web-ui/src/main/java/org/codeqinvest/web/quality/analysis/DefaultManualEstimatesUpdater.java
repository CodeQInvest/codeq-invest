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

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.codeqinvest.quality.analysis.QualityAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

/**
 * @author fmueller
 */
@Slf4j
@Service
class DefaultManualEstimatesUpdater implements ManualEstimatesUpdater {

  private final QualityAnalysisRepository qualityAnalysisRepository;

  @Autowired
  DefaultManualEstimatesUpdater(QualityAnalysisRepository qualityAnalysisRepository) {
    this.qualityAnalysisRepository = qualityAnalysisRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public QualityAnalysis updateManualEstimates(QualityAnalysis analysis, Set<ManualEstimate> manualEstimates) {
    long updatedArtefacts = 0L;
    for (ManualEstimate manualEstimate : manualEstimates) {
      for (Artefact artefact : findArtefacts(analysis, manualEstimate.getArtefact())) {
        updatedArtefacts++;
        log.debug("Update {} with manual estimate of {}", artefact.getName(), manualEstimate.getEstimate());
        if (manualEstimate.getEstimate() != null) {
          artefact.setManualEstimate(Integer.parseInt(manualEstimate.getEstimate()));
        } else {
          artefact.setManualEstimate(null);
        }
      }
    }

    qualityAnalysisRepository.save(analysis);
    log.info("Updated {} artefacts with manual estimates for project {}", updatedArtefacts, analysis.getProject().getName());
    return analysis;
  }

  private Collection<Artefact> findArtefacts(QualityAnalysis analysis, String artefactName) {
    Set<Artefact> artefacts = Sets.newHashSet();
    for (QualityViolation violation : analysis.getViolations()) {
      String currentArtefactName = violation.getArtefact().getName();
      if (currentArtefactName.equals(artefactName) || isPartOfPackageName(artefactName, currentArtefactName)) {
        artefacts.add(violation.getArtefact());
      }
    }
    log.debug("Found {} artefacts for {}: {}", artefacts.size(), artefactName, artefacts);
    return artefacts;
  }

  private boolean isPartOfPackageName(String artefactName, String currentArtefactName) {
    if (currentArtefactName.startsWith(artefactName)) {
      String packagePart = currentArtefactName.substring(artefactName.length());
      return packagePart.contains(".");
    }
    return false;
  }
}
