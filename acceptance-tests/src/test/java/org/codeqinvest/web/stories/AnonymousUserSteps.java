/*
 * Copyright 2013 - 2014 Felix Müller
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
package org.codeqinvest.web.stories;

import org.codeqinvest.web.pages.OverviewPage;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author fmueller
 */
public class AnonymousUserSteps {

  OverviewPage overviewPage;

  @Given("the user has not loaded the web app yet")
  public void givenWebAppNotLoaded() {
    // do nothing, this step is only for convenience
  }

  @When("the user loads the web app")
  public void whenTheUserLoadsTheWebApp() {
    overviewPage.open();
  }

  @Then("they should see the overview page")
  public void thenTheyShouldSeeOverviewPage() {
    assertThat(overviewPage.getNavigationBar().isDisplayed()).isTrue();
  }
}
