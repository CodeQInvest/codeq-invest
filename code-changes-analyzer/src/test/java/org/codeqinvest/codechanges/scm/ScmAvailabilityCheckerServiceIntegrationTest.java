package org.codeqinvest.codechanges.scm;

import org.codeqinvest.codechanges.ScmConnectionSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/module-context.xml")
public class ScmAvailabilityCheckerServiceIntegrationTest {

  @Autowired
  private ScmAvailabilityCheckerService connectionCheckerService;

  @Test
  public void apacheSvnServerShouldBeReachable() {
    // TODO improve this with vagrant and puppet
    assertThat(connectionCheckerService.isAvailable(new ScmConnectionSettings("http://svn.apache.org/repos/asf/commons/proper/logging/trunk/src/main/java/"))).isTrue();
  }
}
