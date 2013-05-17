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
