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
package org.codeqinvest.web;

import org.apache.commons.lang.math.RandomUtils;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class NavigationBarIntegrationTest extends AbstractFluentTestWithHtmlUnitDriver {

  @Test
  public void shouldListAllProjectsInNavigationBarMenuItem() throws IOException {
    addRandomProject();
    addRandomProject();

    goTo(IntegrationTestHelper.ADD_PROJECT_SITE);
    assertThat(find("#projectsMenuItem li"))
        .as("All projects should be listed in corresponding menu item.")
        .hasSize(2 + 2); // + 2 because of addProject menu item and delimiter item
  }

  private void addRandomProject() throws IOException {
    QualityProfile profile = new QualityProfile();
    profile.setId(1L);

    Project project = new Project("Project-" + RandomUtils.nextInt(),
        "* * 3 * * *",
        profile,
        new SonarConnectionSettings("http://nemo.sonarsource.org", "org.apache.cloudstack:cloudstack"),
        new ScmConnectionSettings("http://rapla.googlecode.com/svn/trunk/src/"),
        CodeChangeSettings.defaultSetting(20));

    IntegrationTestHelper.addNewProject(project);
  }
}
