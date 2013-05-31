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
package org.codeqinvest.web.sonar;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codeqinvest.sonar.SonarConnectionSettings;

/**
 * @author fmueller
 */
@Getter
@Setter
@ToString(exclude = "password")
class SonarServer {

  private final String url;
  private final String username;
  private final String password;

  /**
   * This constructor is only used for deserializing form data
   * into instances of this class.
   */
  public SonarServer() {
    this("");
  }

  SonarServer(String url) {
    this(url, "", "");
  }

  SonarServer(String url, String username, String password) {
    this.url = url;
    this.username = username;
    this.password = password;
  }

  SonarConnectionSettings getConnectionSettings() {
    return new SonarConnectionSettings(getUrl(), getUsername(), getPassword(), "");
  }
}
