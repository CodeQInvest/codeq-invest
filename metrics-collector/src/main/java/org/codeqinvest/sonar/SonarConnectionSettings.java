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
package org.codeqinvest.sonar;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.sonar.wsclient.Host;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * This class encapsulates all connection settings for
 * a given sonar instance and the selected project.
 *
 * @author fmueller
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
public class SonarConnectionSettings implements Serializable {

  @Column(name = "SONAR_URL", nullable = false)
  private String url;

  @Column(name = "SONAR_PROJECT", nullable = false)
  private String project;

  @Column(name = "SONAR_USERNAME")
  private String username;

  @Column(name = "SONAR_PASSWORD")
  private String password;

  public SonarConnectionSettings() {
    this("");
  }

  public SonarConnectionSettings(String url) {
    this(url, null, null, null);
  }

  public SonarConnectionSettings(String url, String project) {
    this(url, project, null, null);
  }

  public SonarConnectionSettings(String url, String project, String username, String password) {
    this.url = url;
    this.project = project;
    this.username = username;
    this.password = password;
  }

  public Host asHostObject() {
    return username != null
        ? new Host(url, username, password)
        : new Host(url);
  }

  public boolean hasProject() {
    return project != null;
  }
}
