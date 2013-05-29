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

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.SupportedCodeChangeProbabilityMethod;
import org.codeqinvest.web.AbstractFluentTestWithHtmlUnitDriver;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class ProjectControllerIntegrationTest extends AbstractFluentTestWithHtmlUnitDriver {

  private final String addProjectSite = getUriWithHost("/projects/create");

  @Test
  public void shouldListAllQualityProfiles() throws IOException {
    QualityProfile firstProfile = new QualityProfile("first");
    QualityProfile secondProfile = new QualityProfile("second");
    addNewProfile(firstProfile);
    addNewProfile(secondProfile);

    goTo(addProjectSite);
    FluentList<FluentWebElement> profileOptions = find("#profile option");
    assertThat(profileOptions).as("Site should contain all available quality profiles.").hasSize(2);
    assertThat(profileOptions.get(0).getText()).isEqualTo("first");
    assertThat(profileOptions.get(1).getText()).isEqualTo("second");
  }

  @Test
  public void shouldListAllSupportedCodeChangeProbabilityCalculationMethods() {
    goTo(addProjectSite);
    FluentList<FluentWebElement> codeChangeMethodOptions = find("#codeMethod option");
    assertThat(codeChangeMethodOptions)
        .as("Site should contain all supported methods for calculating code change probability.")
        .hasSize(SupportedCodeChangeProbabilityMethod.values().length);
    assertThat(codeChangeMethodOptions.get(0).getText()).isEqualTo("Default method");
    assertThat(codeChangeMethodOptions.get(1).getText()).isEqualTo("Weighted method");
  }

  private void addNewProfile(QualityProfile profile) throws IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost post = new HttpPost(getUriWithHost("/qualityprofiles/create"));
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("name", profile.getName()));
    post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
    httpClient.execute(post);
  }
}
