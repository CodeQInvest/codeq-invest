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

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.QualityViolation;
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
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/module-context.xml", "classpath:inmemory-db-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class DefaultLastQualityAnalysisServiceIntegrationTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private LastQualityAnalysisService lastQualityAnalysisService;

  private Project project;

  @Before
  public void setUpExampleProject() {
    QualityProfile profile = new QualityProfile("quality-profile");
    SonarConnectionSettings sonarConnectionSettings = new SonarConnectionSettings("http://localhost", "myProject::123");
    ScmConnectionSettings scmConnectionSettings = new ScmConnectionSettings("http://svn.localhost");
    project = new Project("myProject", "0 0 * * *", profile, sonarConnectionSettings, scmConnectionSettings, CodeChangeSettings.defaultSetting(1));

    entityManager.persist(profile);
    entityManager.persist(project);
  }

  @Test
  public void shouldLoadSuccessfulAnalysis() {
    entityManager.persist(QualityAnalysis.success(project, Collections.<QualityViolation>emptyList()));
    assertThat(lastQualityAnalysisService.retrieveLastSuccessfulAnalysis(project)).isNotNull();
  }

  @Test
  public void shouldNotLoadFailedAnalysis() {
    entityManager.persist(QualityAnalysis.failed(project, Collections.<QualityViolation>emptyList(), "error"));
    assertThat(lastQualityAnalysisService.retrieveLastSuccessfulAnalysis(project)).isNull();
  }

  @Test
  public void shouldLoadSuccessfulAnalysisForRetrieveLastAnalysis() {
    entityManager.persist(QualityAnalysis.success(project, Collections.<QualityViolation>emptyList()));
    assertThat(lastQualityAnalysisService.retrieveLastSuccessfulAnalysis(project)).isNotNull();
  }

  @Test
  public void shouldLoadFailedAnalysisForRetrieveLastAnalysis() {
    entityManager.persist(QualityAnalysis.failed(project, Collections.<QualityViolation>emptyList(), "error"));
    assertThat(lastQualityAnalysisService.retrieveLastAnalysis(project)).isNotNull();
  }
}
