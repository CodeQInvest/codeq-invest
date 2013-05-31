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
package org.codeqinvest.web.project;

import org.codeqinvest.web.IntegrationTestHelper;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;

public class ProjectControllerFirefoxIntegrationTest extends FluentTest {

  @Test
  public void loadedProjectsShouldStillBePresentAfterSiteWasSubmittedTwiceWithValidationErrors() {
    goTo(IntegrationTestHelper.ADD_PROJECT_SITE);
    fill("#sonarUrl").with("http://nemo.sonarsource.org");
    click("#sonarUsername");

    // wait for project information to be loaded
    await().atMost(1, TimeUnit.MINUTES).until("#sonarProject option").areDisplayed();

    int numberOfLoadedProjects = find("#sonarProject option").size();

    submit("#createProjectForm");
    submit("#createProjectForm");

    assertThat(find("#sonarProject option")).hasSize(numberOfLoadedProjects);
  }

  @Test
  public void whenFormWasSubmittedBeforeSonarProjectsCouldBeLoadedTheyShouldBeLoadedWhenTheFormIsDisplayedWithValidationErrors() {
    goTo(IntegrationTestHelper.ADD_PROJECT_SITE);
    fill("#sonarUrl").with("http://nemo.sonarsource.org");
    click("#sonarUsername");

    submit("#createProjectForm");

    await().atMost(1, TimeUnit.MINUTES).until("#sonarProject option").areDisplayed();
    assertThat(find("#sonarProject option")).isNotEmpty();
  }

  @Override
  public WebDriver getDefaultDriver() {
    return new FirefoxDriver();
  }
}
