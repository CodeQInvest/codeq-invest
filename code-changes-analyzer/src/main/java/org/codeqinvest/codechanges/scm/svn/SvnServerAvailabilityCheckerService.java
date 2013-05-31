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
package org.codeqinvest.codechanges.scm.svn;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.ScmAvailabilityCheckerService;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.tmatesoft.svn.core.SVNException;

/**
 * Checks the availability of a given SVN server.
 *
 * @author fmueller
 */
@Slf4j
public class SvnServerAvailabilityCheckerService implements ScmAvailabilityCheckerService {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAvailable(ScmConnectionSettings connectionSettings) {
    if (connectionSettings == null || Strings.isNullOrEmpty(connectionSettings.getUrl())) {
      return false;
    }

    try {
      SvnRepositoryFactory.create(connectionSettings).testConnection();
      log.info("The given svn server is reachable with connection settings: {}", connectionSettings);
      return true;
    } catch (SVNException e) {
      log.warn("The given svn server is not reachable during connection check.", e);
      return false;
    }
  }
}
