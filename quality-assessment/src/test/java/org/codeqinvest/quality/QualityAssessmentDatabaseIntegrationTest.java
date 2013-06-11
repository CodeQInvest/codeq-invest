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
package org.codeqinvest.quality;

import com.google.common.collect.Sets;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.LinkedList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This test verifies that the database configuration is working as expected
 * in this module.
 *
 * @author fmueller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/module-context.xml", "classpath:inmemory-db-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class QualityAssessmentDatabaseIntegrationTest {

  @PersistenceContext
  private EntityManager entityManager;

  private QualityProfile profile;
  private QualityRequirement firstRequirement;
  private QualityRequirement secondRequirement;
  private ChangeRiskAssessmentFunction changeRiskAssessmentFunction;
  private RiskCharge riskCharge;
  private Project project;

  @Before
  public void createExampleEntities() {
    profile = new QualityProfile("quality-profile");
    firstRequirement = new QualityRequirement(profile, 100, 200, 10, "nloc", new QualityCriteria("cc", ">", 10));
    secondRequirement = new QualityRequirement(profile, 80, 300, 10, "nloc", new QualityCriteria("ec", "<", 15));
    riskCharge = new RiskCharge(0.3, "<", 10.0);
    changeRiskAssessmentFunction = new ChangeRiskAssessmentFunction(profile, "cc", Sets.newHashSet(riskCharge));

    profile.addRequirement(firstRequirement);
    profile.addRequirement(secondRequirement);
    profile.addChangeRiskAssessmentFunction(changeRiskAssessmentFunction);

    SonarConnectionSettings sonarConnectionSettings = new SonarConnectionSettings("http://localhost", "myProject::123");
    ScmConnectionSettings scmConnectionSettings = new ScmConnectionSettings("http://svn.localhost");
    project = new Project("myProject", "0 0 * * *", profile, sonarConnectionSettings, scmConnectionSettings, CodeChangeSettings.defaultSetting(1));
  }

  @Test
  public void persistAndLoadProjectEntity() {
    entityManager.persist(profile);
    entityManager.persist(project);
    Project projectFromDb = entityManager.find(Project.class, project.getId());
    assertThat(projectFromDb)
        .as("The loaded project object from the database should be equal to the one from the memory.")
        .isEqualTo(project);
  }

  @Test
  public void updateProjectEntity() {
    entityManager.persist(profile);
    entityManager.persist(project);

    entityManager.flush();

    Project projectFromDb = entityManager.find(Project.class, project.getId());
    projectFromDb.setHadAnalysis(true);
    entityManager.persist(projectFromDb);
    assertThat(projectFromDb).isEqualTo(project);
  }

  @Test
  public void persistAndLoadQualityProfileEntityWithRequirementsAndChangeRiskFunction() {
    entityManager.persist(profile);

    entityManager.flush();

    QualityProfile profileFromDb = entityManager.find(QualityProfile.class, profile.getId());
    assertThat(profileFromDb)
        .as("The loaded quality profile object from the database should be equal to the one from the memory.")
        .isEqualTo(profile);
    assertThat(profile.getRequirements()).contains(firstRequirement);
    assertThat(profile.getRequirements()).contains(secondRequirement);
    assertThat(profile.getChangeRiskAssessmentFunctions()).containsOnly(changeRiskAssessmentFunction);
    assertThat(profile.getChangeRiskAssessmentFunctions().iterator().next().getRiskCharges()).containsOnly(riskCharge);
  }

  @Test
  public void persistAndLoadQualityAnalysisEntity() {
    Artefact artefact = new Artefact("MyFile", "0123456");

    entityManager.persist(profile);
    entityManager.persist(project);
    entityManager.persist(artefact);

    QualityViolation firstViolation = new QualityViolation(artefact, secondRequirement, 0, 0, 0, "");
    QualityViolation secondViolation = new QualityViolation(artefact, firstRequirement, 0, 0, 0, "");

    List<QualityViolation> violations = new LinkedList<QualityViolation>();
    violations.add(firstViolation);
    violations.add(secondViolation);

    QualityAnalysis analysis = QualityAnalysis.success(project, violations);
    entityManager.persist(analysis);

    entityManager.flush();

    QualityAnalysis analysisFromDb = entityManager.find(QualityAnalysis.class, analysis.getId());

    assertThat(analysisFromDb)
        .as("The loaded quality analysis object from the database should be equal to the one from the memory.")
        .isEqualTo(analysis);
  }
}
