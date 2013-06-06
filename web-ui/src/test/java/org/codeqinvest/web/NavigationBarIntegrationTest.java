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

import org.junit.Test;

import java.io.IOException;

import static org.codeqinvest.web.IntegrationTestHelper.addRandomProject;
import static org.fest.assertions.Assertions.assertThat;

public class NavigationBarIntegrationTest extends AbstractFluentTestWithHtmlUnitDriver {

  @Test
  public void shouldListAllProjectsInNavigationBarMenuItem() throws IOException {
    addRandomProject();
    addRandomProject();

    goTo(IntegrationTestHelper.ADD_PROJECT_SITE);
    assertThat(find("#projectsMenuItem li"))
        .as("All projects should be listed in corresponding menu item.")
        .hasSize(2 + 2 + 3);
    // + 2 because of addProject menu item and delimiter item
    // + 3 because there are 3 test projects in test data
  }
}
