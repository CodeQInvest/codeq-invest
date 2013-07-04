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
package org.codeqinvest.quality.analysis;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.CodeChangeProbabilityCalculator;
import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.factory.ScmAvailabilityCheckerServiceFactory;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This is the main service of the quality assessment module. It
 * offers functionalities to analyze a given project. For that,
 * it collects all the necessary data from Sonar and the
 * corresponding source code management system. Before it performs
 * these steps, it checks for the availability of these third-party
 * systems. The result of an analysis is persisted in the database.
 *
 * @author fmueller
 */
// TODO does this really have to be annotated with Service? and think about the class name
@Slf4j
@Service
class DefaultQualityAnalyzerService implements QualityAnalyzerService {

  private final ViolationsCalculatorService violationsCalculatorService;
  private final ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory;
  private final CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory;
  private final SecureChangeProbabilityCalculator secureChangeProbabilityCalculator;
  private final QualityViolationCostsCalculator costsCalculator;
  private final QualityAnalysisRepository qualityAnalysisRepository;

  @Autowired
  public DefaultQualityAnalyzerService(ViolationsCalculatorService violationsCalculatorService,
                                       ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory,
                                       CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory,
                                       SecureChangeProbabilityCalculator secureChangeProbabilityCalculator,
                                       QualityViolationCostsCalculator costsCalculator,
                                       QualityAnalysisRepository qualityAnalysisRepository) {
    this.violationsCalculatorService = violationsCalculatorService;
    this.scmAvailabilityCheckerServiceFactory = scmAvailabilityCheckerServiceFactory;
    this.codeChangeProbabilityCalculatorFactory = codeChangeProbabilityCalculatorFactory;
    this.secureChangeProbabilityCalculator = secureChangeProbabilityCalculator;
    this.costsCalculator = costsCalculator;
    this.qualityAnalysisRepository = qualityAnalysisRepository;
  }

  // TODO IMPORTANT: refactor this into commmand pattern to get better structure
  @Override
  public QualityAnalysis analyzeProject(Project project) {
    try {
      ViolationsAnalysisResult violationsAnalysisResult = violationsCalculatorService.calculateAllViolation(project);
      if (!violationsAnalysisResult.isSuccessful()) {
        log.error("Quality analysis for project {} failed due '{}'", project.getName(), violationsAnalysisResult.getFailureReason().get());
        return QualityAnalysis.failed(project,
            zeroCostsForEachViolation(violationsAnalysisResult),
            violationsAnalysisResult.getFailureReason().get());
      }

      log.info("Checking the availability of the SCM system {} for project {}", project.getScmSettings(), project.getName());
      if (!scmAvailabilityCheckerServiceFactory.create(project.getScmSettings()).isAvailable(project.getScmSettings())) {
        return QualityAnalysis.failed(project, zeroCostsForEachViolation(violationsAnalysisResult), "The scm system is not available.");
      }

      QualityAnalysis qualityAnalysis = addChangeProbabilityToEachArtifact(project, violationsAnalysisResult);
      if (!qualityAnalysis.isSuccessful()) {
        return qualityAnalysisRepository.save(qualityAnalysis);
      }

      qualityAnalysis = addSecureChangeProbabilityToEachArtifact(project, qualityAnalysis);
      log.info("Quality analysis succeeded for project {} with {} violations.", project.getName(), violationsAnalysisResult.getViolations().size());
      return qualityAnalysisRepository.save(qualityAnalysis);
    } catch (Exception e) {
      String errorMessage = "Unexpected error occured during quality analysis!";
      log.error(errorMessage, e);
      return QualityAnalysis.failed(project, new ArrayList<QualityViolation>(), errorMessage);
    }
  }

  private QualityAnalysis addChangeProbabilityToEachArtifact(Project project, ViolationsAnalysisResult violationsAnalysisResult) {
    log.info("Starting calculation of change probability for each artefact of project {}", project.getName());
    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = codeChangeProbabilityCalculatorFactory.create(project.getCodeChangeSettings());
    Set<String> computedArtefacts = Sets.newHashSet();
    for (ViolationOccurence violation : violationsAnalysisResult.getViolations()) {
      Artefact artefact = violation.getArtefact();
      if (computedArtefacts.contains(violation.getArtefact().getSonarIdentifier())) {
        continue;
      }

      final double changeProbability;
      try {
        changeProbability = codeChangeProbabilityCalculator.calculateCodeChangeProbability(project.getScmSettings(), artefact.getFilename());
        artefact.setChangeProbability(changeProbability);
        computedArtefacts.add(artefact.getSonarIdentifier());
      } catch (CodeChurnCalculationException e) {
        logFailedAnalysis(project, e);
        return QualityAnalysis.failed(project,
            zeroCostsForEachViolation(violationsAnalysisResult),
            "Error during calculating the code churn for " + violation.getArtefact().getName());
      } catch (ScmConnectionEncodingException e) {
        logFailedAnalysis(project, e);
        return QualityAnalysis.failed(project,
            zeroCostsForEachViolation(violationsAnalysisResult),
            "Error with supplied scm connection encoding.");
      }
    }

    try {
      return QualityAnalysis.success(project, calculateCostsForEachViolation(project.getSonarConnectionSettings(), violationsAnalysisResult));
    } catch (ResourceNotFoundException e) {
      logFailedAnalysis(project, e);
      return QualityAnalysis.failed(project, zeroCostsForEachViolation(violationsAnalysisResult), "Resource not found during costs calculation.");
    }
  }

  private QualityAnalysis addSecureChangeProbabilityToEachArtifact(Project project, QualityAnalysis qualityAnalysis) {
    log.info("Starting calculation of secure change probability for each artefact of project {}", project.getName());
    Set<String> computedArtefacts = Sets.newHashSet();
    for (QualityViolation violation : qualityAnalysis.getViolations()) {
      if (computedArtefacts.contains(violation.getArtefact().getSonarIdentifier())) {
        continue;
      }

      try {
        Artefact artefact = violation.getArtefact();
        double secureChangeProbability = secureChangeProbabilityCalculator.calculateSecureChangeProbability(project.getProfile(),
            project.getSonarConnectionSettings(), artefact);
        artefact.setSecureChangeProbability(secureChangeProbability);
        computedArtefacts.add(artefact.getSonarIdentifier());
      } catch (ResourceNotFoundException e) {
        logFailedAnalysis(project, e);
        return QualityAnalysis.failed(project, qualityAnalysis.getViolations(), "Resource not found during secure change calculation");
      }
    }
    log.info("Finished calculation of secure change probability for each artefact of project {}", project.getName());
    return qualityAnalysis;
  }

  private void logFailedAnalysis(Project project, Exception e) {
    log.error("Quality analysis for project " + project.getName() + " failed!", e);
  }

  private List<QualityViolation> calculateCostsForEachViolation(SonarConnectionSettings sonarConnectionSettings, ViolationsAnalysisResult violationsAnalysisResult) throws ResourceNotFoundException {
    List<QualityViolation> qualityViolations = new ArrayList<QualityViolation>(violationsAnalysisResult.getViolations().size());
    for (ViolationOccurence violation : violationsAnalysisResult.getViolations()) {
      int remediationCosts = costsCalculator.calculateRemediationCosts(sonarConnectionSettings, violation);
      int nonRemediationCosts = costsCalculator.calculateNonRemediationCosts(sonarConnectionSettings, violation);
      qualityViolations.add(new QualityViolation(violation.getArtefact(), violation.getRequirement(),
          remediationCosts, nonRemediationCosts, violation.getWeightingMetricValue(), violation.getRequirement().getWeightingMetricIdentifier()));
    }
    return qualityViolations;
  }

  private List<QualityViolation> zeroCostsForEachViolation(ViolationsAnalysisResult violationsAnalysisResult) {
    List<QualityViolation> qualityViolations = new ArrayList<QualityViolation>(violationsAnalysisResult.getViolations().size());
    for (ViolationOccurence violation : violationsAnalysisResult.getViolations()) {
      qualityViolations.add(new QualityViolation(violation.getArtefact(), violation.getRequirement(), 0, 0, 0, violation.getRequirement().getWeightingMetricIdentifier()));
    }
    return qualityViolations;
  }
}
