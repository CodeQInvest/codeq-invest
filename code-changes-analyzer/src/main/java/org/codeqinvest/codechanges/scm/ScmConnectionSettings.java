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
package org.codeqinvest.codechanges.scm;

import com.google.common.base.Strings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * This class encapsulates all connection settings for
 * a given scm server instance.
 *
 * @author fmueller
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "password")
@Embeddable
public class ScmConnectionSettings implements Serializable {

  @Column(name = "SCM_TYPE", nullable = false)
  private int type;

  @Column(name = "SCM_URL", nullable = false)
  private String url;

  @Column(name = "SCM_USERNAME")
  private String username;

  @Column(name = "SCM_PASSWORD")
  private String password;

  @Column(name = "SCM_FILE_ENCODING", nullable = false)
  private String encoding;

  public ScmConnectionSettings() {
    this("");
  }

  public ScmConnectionSettings(String url) {
    this(url, null, null);
  }

  public ScmConnectionSettings(String url, String username, String password) {
    this(url, username, password, "UTF-8");
  }

  public ScmConnectionSettings(String url, String username, String password, String encoding) {
    // default type is subversion
    this.type = 0;
    this.url = url;
    this.username = username;
    this.password = password;
    this.encoding = encoding;
  }

  public boolean hasUsername() {
    return username != null;
  }

  public String getUrl() {
    if (Strings.isNullOrEmpty(url)) {
      return url;
    }

    if (!url.endsWith("/")) {
      return url + "/";
    }
    return url;
  }
}
