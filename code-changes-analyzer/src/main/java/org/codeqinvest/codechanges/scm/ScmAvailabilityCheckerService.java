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

import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codeqinvest.codechanges.ScmConnectionSettings;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This service verifies the availability of a given scm server.
 *
 * @author fmueller
 */
@Slf4j
@Service
public class ScmAvailabilityCheckerService {

  public boolean isAvailable(ScmConnectionSettings connectionSettings) {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    if (connectionSettings.hasUsername()) {
      httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(connectionSettings.getUsername(), connectionSettings.getPassword()));
    }

    try {
      return httpClient.execute(new HttpGet(connectionSettings.getUrl())).getStatusLine().getStatusCode() == 200;
    } catch (IOException e) {
      ScmAvailabilityCheckerService.log.info("The SCM server is not reachable during connection check.", e);
      return false;
    }
  }
}
