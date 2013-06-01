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
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.quality.CodeChangeSettings;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.sonar.SonarConnectionSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class IntegrationTestHelper {

  public static final String PROJECT_SITE = getUriWithHost("/projects/");
  public static final String ADD_PROJECT_SITE = getUriWithHost("/projects/create");
  public static final String ADD_QUALITY_PROFILE_SITE = getUriWithHost("/qualityprofiles/create");

  private IntegrationTestHelper() {
  }

  public static String getUriWithHost(String url) {
    return System.getProperty("system.host", "http://localhost:8080") + url;
  }

  public static void addNewProfile(QualityProfile profile) throws IOException {
    doPostRequest(ADD_QUALITY_PROFILE_SITE, Arrays.<NameValuePair>asList(new BasicNameValuePair("name", profile.getName())));
  }

  public static void addRandomProject() throws IOException {
    QualityProfile profile = new QualityProfile();
    profile.setId(1L);

    Project project = new Project("Project-" + RandomUtils.nextInt(),
        "* * 3 * * *",
        profile,
        new SonarConnectionSettings("http://nemo.sonarsource.org", "org.apache.cloudstack:cloudstack"),
        new ScmConnectionSettings("http://rapla.googlecode.com/svn/trunk/src/"),
        CodeChangeSettings.defaultSetting(20));

    addNewProject(project);
  }

  private static void addNewProject(Project project) throws IOException {
    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    parameters.add(new BasicNameValuePair("name", project.getName()));
    parameters.add(new BasicNameValuePair("profile.id", "1"));
    parameters.add(new BasicNameValuePair("cronExpression", project.getCronExpression()));
    parameters.add(new BasicNameValuePair("sonarConnectionSettings.url", project.getSonarConnectionSettings().getUrl()));
    parameters.add(new BasicNameValuePair("sonarConnectionSettings.project", project.getSonarConnectionSettings().getProject()));
    parameters.add(new BasicNameValuePair("scmSettings.type", "0"));
    parameters.add(new BasicNameValuePair("scmSettings.url", project.getScmSettings().getUrl()));
    parameters.add(new BasicNameValuePair("codeChangeSettings.method", "1"));
    parameters.add(new BasicNameValuePair("codeChangeSettings.days", "30"));
    doPostRequest(ADD_PROJECT_SITE, parameters);
  }

  private static void doPostRequest(String uri, List<NameValuePair> parameters) throws IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost post = new HttpPost(uri);
    post.setEntity(new UrlEncodedFormEntity(parameters, Consts.UTF_8));
    httpClient.execute(post);
  }
}
