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

import org.codeqinvest.web.AbstractFluentTestWithHtmlUnitDriver;
import org.junit.Test;

import static org.codeqinvest.web.IntegrationTestHelper.PROJECT_SITE;
import static org.fest.assertions.Assertions.assertThat;

public class ProjectControllerIntegrationTest extends AbstractFluentTestWithHtmlUnitDriver {

  private static final String PROJECT_WITHOUT_ANALYSIS_SITE = PROJECT_SITE + "1";
  private static final String PROJECT_WITH_FIRST_ANALYSIS_IN_PROGRESS_SITE = PROJECT_SITE + "2";
  private static final String PROJECT_WITH_MANY_ANALYSIS_SITE = PROJECT_SITE + "3";

  @Test
  public void currentProjectShouldBeActiveItemInNavigationMenu() {
    goTo(PROJECT_WITHOUT_ANALYSIS_SITE);
    assertThat(find("#projectsMenuItem > .active > a").getAttribute("href")).endsWith("1");
  }

  @Test
  public void whenProjectHasNoAnalysisInfoBoxShouldBeVisible() {
    goTo(PROJECT_WITHOUT_ANALYSIS_SITE);
    assertThat(find("#projectQualityInvestment"))
        .as("When a project has no analysis the investment data boxes and forms should not be displayed.")
        .isEmpty();
    assertThat(find("#noAnalysisBox"))
        .as("When a project has no analysis the 'no analysis' info box should be displayed.")
        .isNotEmpty();
    assertThat(find("#noAnalysisBox button"))
        .as("When a project has no analysis a button to start the first analysis should be displayed.")
        .isNotEmpty();
  }

  @Test
  public void whenProjectHasNoFinishedAnalysisButFirstIsInProgressInfoTextShouldBeVisible() {
    goTo(PROJECT_WITH_FIRST_ANALYSIS_IN_PROGRESS_SITE);
    assertThat(find("#noAnalysisBox"))
        .as("When a project has no analysis but the first analysis is running the 'no analysis' info box should be displayed.")
        .isNotEmpty();
    assertThat(find("#noAnalysisBox button"))
        .as("The button to start the first analysis should not be visibile when first analysis is still running.")
        .isEmpty();
  }

  @Test
  public void whenProjectHasAnalysisDataQualityInvestmentDataBoxShouldBeVisible() {
    goTo(PROJECT_WITH_MANY_ANALYSIS_SITE);
    assertThat(find("#projectQualityInvestment")).isNotEmpty();
    assertThat(find("#noAnalysisBox")).isEmpty();
  }
}
