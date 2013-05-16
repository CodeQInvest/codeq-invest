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

import org.codeqinvest.project.Project;
import org.codeqinvest.project.ScmSettings;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.codeqinvest.test.utils.AbstractDatabaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
public class QualityAssessmentDatabaseIntegrationTest extends AbstractDatabaseIntegrationTest {

  @PersistenceContext
  private EntityManager entityManager;

  private QualityProfile profile;
  private QualityRequirement firstRequirement;
  private QualityRequirement secondRequirement;
  private Project project;

  @Before
  public void createExampleEntities() {
    profile = new QualityProfile();
    firstRequirement = new QualityRequirement(profile, 100, 200, 10, "nloc", "cc", ">", 10);
    secondRequirement = new QualityRequirement(profile, 80, 300, 10, "nloc", "ec", "<", 15);
    profile.addRequirement(firstRequirement);
    profile.addRequirement(secondRequirement);

    SonarConnectionSettings sonarConnectionSettings = new SonarConnectionSettings("http://localhost", "myProject::123");
    ScmSettings scmSettings = new ScmSettings(0, "http://svn.localhost");
    project = new Project("myProject", "0 0 * * *", sonarConnectionSettings, scmSettings);
  }

  @Test
  public void persistAndLoadProjectEntity() {
    entityManager.persist(project);
    Project projectFromDb = entityManager.find(Project.class, project.getId());
    assertThat(projectFromDb)
        .as("The loaded project object from the database should be equal to the one from the memory.")
        .isEqualTo(project);
  }

  @Test
  public void persistAndLoadQualityProfileEntityWithRequirements() {
    entityManager.persist(profile);
    QualityProfile profileFromDb = entityManager.find(QualityProfile.class, profile.getId());
    assertThat(profileFromDb)
        .as("The loaded quality profile object from the database should be equal to the one from the memory.")
        .isEqualTo(profile);
  }

  @Test
  public void persistAndLoadQualityAnalsisEntity() {
    Artefact artefact = new Artefact("MyFile", "0123456");

    entityManager.persist(profile);
    entityManager.persist(project);
    entityManager.persist(artefact);

    QualityViolation firstViolation = new QualityViolation(artefact, secondRequirement);
    QualityViolation secondViolation = new QualityViolation(artefact, firstRequirement);

    List<QualityViolation> violations = new LinkedList<QualityViolation>();
    violations.add(firstViolation);
    violations.add(secondViolation);

    QualityAnalysis analysis = new QualityAnalysis(project, violations);
    entityManager.persist(analysis);

    QualityAnalysis analysisFromDb = entityManager.find(QualityAnalysis.class, analysis.getId());

    assertThat(analysisFromDb)
        .as("The loaded quality analysis object from the database should be equal to the one from the memory.")
        .isEqualTo(analysis);
  }
}
