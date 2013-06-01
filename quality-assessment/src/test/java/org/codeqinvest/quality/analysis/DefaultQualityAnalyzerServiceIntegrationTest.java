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
import org.codeqinvest.sonar.ResourceNotFoundException;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/module-context.xml", "classpath:inmemory-db-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class DefaultQualityAnalyzerServiceIntegrationTest {

  private QualityProfile profile;
  private QualityRequirement firstRequirement;
  private QualityRequirement secondRequirement;

  private Project project;

  private ScmAvailabilityCheckerServiceFactory scmAvailabilityCheckerServiceFactory;
  private QualityViolationCostsCalculator costsCalculator;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private QualityAnalysisRepository qualityAnalysisRepository;

  @Before
  public void setUp() {
    profile = new QualityProfile("quality-profile");
    firstRequirement = new QualityRequirement(profile, 100, 200, 10, "nloc", new QualityCriteria("cc", ">", 10));
    secondRequirement = new QualityRequirement(profile, 80, 300, 10, "nloc", new QualityCriteria("ec", "<", 15));
    profile.addRequirement(firstRequirement);
    profile.addRequirement(secondRequirement);

    SonarConnectionSettings sonarConnectionSettings = new SonarConnectionSettings("http://localhost", "myProject::123");
    ScmConnectionSettings scmConnectionSettings = new ScmConnectionSettings("http://svn.localhost");
    project = new Project("myProject", "0 0 * * *", profile, sonarConnectionSettings, scmConnectionSettings, CodeChangeSettings.defaultSetting(1));

    // set up scm system that is available
    ScmAvailabilityCheckerService availableScmSystem = mock(ScmAvailabilityCheckerService.class);
    when(availableScmSystem.isAvailable(any(ScmConnectionSettings.class))).thenReturn(true);
    scmAvailabilityCheckerServiceFactory = mock(ScmAvailabilityCheckerServiceFactory.class);
    when(scmAvailabilityCheckerServiceFactory.create(any(ScmConnectionSettings.class))).thenReturn(availableScmSystem);

    costsCalculator = mock(QualityViolationCostsCalculator.class);

    entityManager.persist(profile);
    entityManager.persist(project);
  }

  @Test
  public void qualityAnalysisShouldBePersistedToDatabase() throws CodeChurnCalculationException, ScmConnectionEncodingException, ResourceNotFoundException {
    Artefact artefactA = new Artefact("A", "A");
    Artefact artefactB = new Artefact("B", "B");
    Artefact artefactC = new Artefact("C", "C");

    ViolationsCalculatorService violationsCalculatorService = mock(ViolationsCalculatorService.class);
    when(violationsCalculatorService.calculateAllViolation(any(Project.class)))
        .thenReturn(ViolationsAnalysisResult.createSuccessfulAnalysis(Arrays.asList(
            new ViolationOccurence(firstRequirement, artefactA),
            new ViolationOccurence(secondRequirement, artefactA),
            new ViolationOccurence(firstRequirement, artefactB),
            new ViolationOccurence(firstRequirement, artefactC),
            new ViolationOccurence(secondRequirement, artefactC))));

    CodeChangeProbabilityCalculator codeChangeProbabilityCalculator = mock(CodeChangeProbabilityCalculator.class);
    when(codeChangeProbabilityCalculator.calculateCodeChangeProbability(any(ScmConnectionSettings.class), anyString())).thenReturn(1.0);
    CodeChangeProbabilityCalculatorFactory codeChangeProbabilityCalculatorFactory = mock(CodeChangeProbabilityCalculatorFactory.class);
    when(codeChangeProbabilityCalculatorFactory.create(any(CodeChangeSettings.class))).thenReturn(codeChangeProbabilityCalculator);

    SecureChangeProbabilityCalculator secureChangeProbabilityCalculator = mock(SecureChangeProbabilityCalculator.class);
    when(secureChangeProbabilityCalculator.calculateSecureChangeProbability(any(QualityProfile.class),
        any(SonarConnectionSettings.class), any(Artefact.class))).thenReturn(1.0);

    QualityAnalyzerService qualityAnalyzerService = new DefaultQualityAnalyzerService(violationsCalculatorService, scmAvailabilityCheckerServiceFactory,
        codeChangeProbabilityCalculatorFactory, secureChangeProbabilityCalculator, costsCalculator, qualityAnalysisRepository);

    QualityAnalysis analysis = qualityAnalyzerService.analyzeProject(project);
    QualityAnalysis analysisFromDb = qualityAnalysisRepository.findOne(analysis.getId());
    assertThat(analysisFromDb).isEqualTo(analysis);
    assertThat(analysisFromDb.getViolations().toString()).isEqualTo(analysis.getViolations().toString());
  }
}
