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

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.repository.ProjectRepository;
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

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/module-context.xml", "classpath:inmemory-db-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProjectRepositoryIntegrationTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private ProjectRepository projectRepository;

  private Project project;

  @Before
  public void createAndPersistExampleProject() {
    QualityProfile profile = new QualityProfile("quality-profile");

    SonarConnectionSettings sonarConnectionSettings = new SonarConnectionSettings("http://localhost", "myProject::123");
    ScmConnectionSettings scmConnectionSettings = new ScmConnectionSettings("http://svn.localhost");
    project = new Project("myProject", "0 0 * * *", profile, sonarConnectionSettings, scmConnectionSettings, CodeChangeSettings.defaultSetting(1));

    entityManager.persist(profile);
    entityManager.persist(project);
  }

  @Test
  public void shouldOnlyFindOneProjectByGivenNameWhenProjectExistsInDatabase() {
    assertThat(projectRepository.findOneByLowercaseName("myproject")).isEqualTo(project);
  }

  @Test
  public void shouldNotFindAnyProjectByGivenNameWhenProjectDoesNotExistInDatabase() {
    assertThat(projectRepository.findOneByLowercaseName("abc123")).isNull();
  }
}
