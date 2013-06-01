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

import org.codeqinvest.codechanges.CodeChangeProbabilityCalculator;
import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.ScmAvailabilityCheckerService;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.factory.ScmAvailabilityCheckerServiceFactory;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityCriteria;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.QualityRequirement;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultQualityAnalyzerServiceTest {

  private QualityProfile profile;
  private QualityRequirement firstRequirement;
  private QualityRequirement secondRequirement;

  private Project project;

  private ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory;
  private SecureChangeProbabilityCalculator secureChangeProbabilityCalculator;
  private QualityViolationCostsCalculator costsCalculator;
  private QualityAnalysisRepository qualityAnalysisRepository;

  @Before
  public void setUp() throws ResourceNotFoundException {
    profile = new QualityProfile("quality-profile");
    firstRequirement = new QualityRequirement(profile, 100, 200, 10, "nloc", new QualityCriteria("cc", ">", 10));
    secondRequirement = new QualityRequirement(profile, 80, 300, 10, "nloc", new QualityCriteria("ec", "<", 15));
    profile.addRequirement(firstRequirement);
    profile.addRequirement(secondRequirement);

    project = mock(Project.class);
    when(project.getProfile()).thenReturn(profile);

    // set up scm system that is available
    ScmAvailabilityCheckerService availableScmSystem = mock(ScmAvailabilityCheckerService.class);
    when(availableScmSystem.isAvailable(any(ScmConnectionSettings.class))).thenReturn(true);
    scmAvailabilityCheckerServiceFactory = mock(ScmAvailabilityCheckerServiceFactory.class);
    when(scmAvailabilityCheckerServiceFactory.create(any(ScmConnectionSettings.class))).thenReturn(availableScmSystem);

    secureChangeProbabilityCalculator = mock(SecureChangeProbabilityCalculator.class);
    when(secureChangeProbabilityCalculator.calculateSecureChangeProbability(any(QualityProfile.class),
        any(SonarConnectionSettings.class), any(Artefact.class))).thenReturn(1.0);

    costsCalculator = mock(QualityViolationCostsCalculator.class);
    qualityAnalysisRepository = new DummyQualityAnalysisRepository();
  }

  @Test
  public void succeededAnalysisWithOneViolation() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    Artefact artefact = new Artefact("A", "A");
    QualityAnalyzerService qualityAnalyzerService = createMockedSystemWithArtefactsAndViolations(
        Arrays.asList(new ViolationOccurence(firstRequirement, artefact)));

    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThatIsSuccessfulAndContainsOnlyGivenViolationsWithoutCostsComparison(analysis, new QualityViolation(artefact, firstRequirement, 0, 0));
  }

  @Test
  public void succeededAnalysisWithManyViolations() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    Artefact artefactA = new Artefact("A", "A");
    Artefact artefactB = new Artefact("B", "B");
    Artefact artefactC = new Artefact("C", "C");
    QualityAnalyzerService qualityAnalyzerService = createMockedSystemWithArtefactsAndViolations(
        Arrays.asList(new ViolationOccurence(firstRequirement, artefactA),
            new ViolationOccurence(secondRequirement, artefactA),
            new ViolationOccurence(firstRequirement, artefactB),
            new ViolationOccurence(firstRequirement, artefactC),
            new ViolationOccurence(secondRequirement, artefactC)));

    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThatIsSuccessfulAndContainsOnlyGivenViolationsWithoutCostsComparison(analysis,
        new QualityViolation(artefactA, firstRequirement, 0, 0),
        new QualityViolation(artefactA, secondRequirement, 0, 0),
        new QualityViolation(artefactB, firstRequirement, 0, 0),
        new QualityViolation(artefactC, firstRequirement, 0, 0),
        new QualityViolation(artefactC, secondRequirement, 0, 0));
  }

  @Test
  public void addCodeChangeProbabilityToArtefacts() throws CodeChurnCalculationException, ScmConnectionEncodingException, ResourceNotFoundException {
    Artefact artefactA = new Artefact("A", "A");
    Artefact artefactB = new Artefact("B", "B");

    ViolationOccurence violationA = new ViolationOccurence(firstRequirement, artefactA);
    ViolationOccurence violationB = new ViolationOccurence(firstRequirement, artefactB);

    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(violationA, violationB)));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.2);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService,
        scmAvailabilityCheckerServiceFactory, codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);
    qualityAnalyzerService.analyzeProject(project);

    assertThat(artefactA.getChangeProbability()).isEqualTo(1.2);
    assertThat(artefactB.getChangeProbability()).isEqualTo(1.2);
  }

  @Test
  public void addSecureChangeProbabilityToArtefacts() throws CodeChurnCalculationException, ScmConnectionEncodingException, ResourceNotFoundException {
    Artefact artefactA = new Artefact("A", "A");
    Artefact artefactB = new Artefact("B", "B");

    ViolationOccurence violationA = new ViolationOccurence(firstRequirement, artefactA);
    ViolationOccurence violationB = new ViolationOccurence(firstRequirement, artefactB);

    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(violationA, violationB)));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.0);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    when(secureChangeProbabilityCalculator.calculateSecureChangeProbability(any(QualityProfile.class),
        any(SonarConnectionSettings.class), eq(artefactA))).thenReturn(1.115);
    when(secureChangeProbabilityCalculator.calculateSecureChangeProbability(any(QualityProfile.class),
        any(SonarConnectionSettings.class), eq(artefactB))).thenReturn(1.341);

    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService,
        scmAvailabilityCheckerServiceFactory, codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);
    qualityAnalyzerService.analyzeProject(project);

    assertThat(artefactA.getSecureChangeProbability()).isEqualTo(1.115);
    assertThat(artefactB.getSecureChangeProbability()).isEqualTo(1.341);
  }

  @Test
  public void failedAnalysisWhenSecureChangeProbabilityCalculatorThrowsResourceNotFoundException()
      throws CodeChurnCalculationException, ScmConnectionEncodingException, ResourceNotFoundException {

    ViolationOccurence violation = new ViolationOccurence(firstRequirement, new Artefact("A", "A"));

    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(violation)));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.0);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    when(secureChangeProbabilityCalculator.calculateSecureChangeProbability(any(QualityProfile.class),
        any(SonarConnectionSettings.class), any(Artefact.class))).thenThrow(ResourceNotFoundException.class);

    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService,
        scmAvailabilityCheckerServiceFactory, codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);
    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);

    assertThat(analysis.isSuccessful()).isFalse();
  }

  @Test
  public void takeCostsFromSuppliedCostCalculator() throws CodeChurnCalculationException, ScmConnectionEncodingException, ResourceNotFoundException {
    ViolationOccurence violation = new ViolationOccurence(firstRequirement, new Artefact("A", "A"));

    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(violation)));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.0);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService,
        scmAvailabilityCheckerServiceFactory, codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);
    qualityAnalyzerService.analyzeProject(project);
    verify(costsCalculator).calculateRemediationCosts(project.getSonarConnectionSettings(), violation);
    verify(costsCalculator).calculateNonRemediationCosts(project.getSonarConnectionSettings(), violation);
  }

  @Test
  public void failedAnalysisWhenViolationAnalysisWasNotSuccessful() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createFailedAnalysis(Collections.<ViolationOccurence>emptyList(), "error"));

    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService,
        scmAvailabilityCheckerServiceFactory, mock(CodeChangeProbabilityCalculatorFactory.class),
        secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);

    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThat(analysis.isSuccessful()).isFalse();
    assertThat(analysis.getFailureReason()).isEqualTo("error");
  }

  @Test
  public void failedAnalysisWhenScmSystemIsNotAvailable() {
    // set up scm system that is not available
    ScmAvailabilityCheckerService notAvailableScmSystem = mock(ScmAvailabilityCheckerService.class);
    when(notAvailableScmSystem.isAvailable(any(ScmConnectionSettings.class))).thenReturn(false);
    ScmAvailabilityCheckerServiceFactory notAvailableCheckerServiceFactory = mock(ScmAvailabilityCheckerServiceFactory.class);
    when(notAvailableCheckerServiceFactory.create(any(ScmConnectionSettings.class))).thenReturn(notAvailableScmSystem);

    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Collections.<ViolationOccurence>emptyList()));

    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService,
        notAvailableCheckerServiceFactory, mock(CodeChangeProbabilityCalculatorFactory.class),
        secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);

    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThat(analysis.isSuccessful()).isFalse();
  }

  @Test
  public void failedAnalysisWhenProblemWithCodeChurnCalculationOccurred() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    QualityAnalyzerService qualityAnalyzerService = createMockedSystemThatThrowsExceptionInCodeChangeCalculation(CodeChurnCalculationException.class);
    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThat(analysis.isSuccessful()).isFalse();
  }

  @Test
  public void failedAnalysisWhenProblemWithScmConnectionEncodingOccurred() throws CodeChurnCalculationException, ScmConnectionEncodingException {
    QualityAnalyzerService qualityAnalyzerService = createMockedSystemThatThrowsExceptionInCodeChangeCalculation(ScmConnectionEncodingException.class);
    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThat(analysis.isSuccessful()).isFalse();
  }

  @Test
  public void failedAnalysisWhenCostCalculatorThrowsResourceNotFoundExceptionOnRemediationCosts() throws CodeChurnCalculationException, ScmConnectionEncodingException, ResourceNotFoundException {
    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(new ViolationOccurence(firstRequirement, new Artefact("A", "A")))));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.0);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    when(costsCalculator.calculateRemediationCosts(any(SonarConnectionSettings.class), any(ViolationOccurence.class))).thenThrow(ResourceNotFoundException.class);
    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService, scmAvailabilityCheckerServiceFactory,
        codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);

    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThat(analysis.isSuccessful()).isFalse();
  }

  @Test
  public void failedAnalysisWhenCostCalculatorThrowsResourceNotFoundExceptionOnNonRemediationCosts() throws CodeChurnCalculationException, ScmConnectionEncodingException, ResourceNotFoundException {
    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(new ViolationOccurence(firstRequirement, new Artefact("A", "A")))));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.0);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    when(costsCalculator.calculateNonRemediationCosts(any(SonarConnectionSettings.class), any(ViolationOccurence.class))).thenThrow(ResourceNotFoundException.class);
    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService, scmAvailabilityCheckerServiceFactory,
        codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);

    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    assertThat(analysis.isSuccessful()).isFalse();
  }

  private QualityAnalyzerService createMockedSystemWithArtefactsAndViolations(List<ViolationOccurence> violations) throws CodeChurnCalculationException, ScmConnectionEncodingException {
    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class))).thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(violations));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.0);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    return new DefaultQualityAnalyzerService(violationsCalculatorService, scmAvailabilityCheckerServiceFactory,
        codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);
  }

  private <T extends Exception> QualityAnalyzerService createMockedSystemThatThrowsExceptionInCodeChangeCalculation(Class<T> exception) throws CodeChurnCalculationException, ScmConnectionEncodingException {
    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(new ViolationOccurence(firstRequirement, new Artefact("A", "A")))));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenThrow(exception);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);
    return new DefaultQualityAnalyzerService(violationsCalculatorService, scmAvailabilityCheckerServiceFactory,
        codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);
  }

  private void assertThatIsSuccessfulAndContainsOnlyGivenViolationsWithoutCostsComparison(QualityAnalysis analysis, QualityViolation... qualityViolations) {
    assertThat(analysis.isSuccessful()).isTrue();
    assertThat(analysis.getViolations()).hasSize(qualityViolations.length);
    int i = 0;
    for (QualityViolation violation : analysis.getViolations()) {
      assertThat(violation.getArtefact()).isEqualTo(qualityViolations[i].getArtefact());
      assertThat(violation.getRequirement()).isEqualTo(qualityViolations[i].getRequirement());
      i++;
    }
  }
}
