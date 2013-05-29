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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This is a tiny test that checks the application is deployable and
 * running properly in a web container.
 *
 * @author fmueller
 */
public class ApplicationStatusCheckIntegrationTest extends AbstractFluentTestWithHtmlUnitDriver {

  private final String overviewUrl = System.getProperty("system.host");

  @Test
  public void isProperlyDeployed() throws IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpResponse response = httpClient.execute(new HttpGet(overviewUrl));
    assertThat(response.getStatusLine().getStatusCode())
        .as("The application should be deployed and return http status code 200 for " + overviewUrl)
        .isEqualTo(200);
  }

  @Test
  public void displaysOverviewPageProperly() {
    goTo(overviewUrl);
    assertThat(find(".brand").getText())
        .as("The overview page should be properly displayed by showing at least the correct brand in the navigation bar.")
        .isEqualTo("CodeQ Invest");
  }
}
