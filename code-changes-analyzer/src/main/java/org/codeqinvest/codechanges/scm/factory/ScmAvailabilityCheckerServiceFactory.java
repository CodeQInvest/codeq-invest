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
package org.codeqinvest.codechanges.scm.factory;

import org.codeqinvest.codechanges.scm.ScmAvailabilityCheckerService;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.svn.SvnServerAvailabilityCheckerService;
import org.springframework.stereotype.Component;

/**
 * @author fmueller
 */
@Component
public class ScmAvailabilityCheckerServiceFactory {

  public ScmAvailabilityCheckerService create(ScmConnectionSettings connectionSettings) {
    if (connectionSettings.getType() == SupportedScmSystem.SVN.getType()) {
      return new SvnServerAvailabilityCheckerService();
    }
    // currenty only subversion is supported
    throw new UnsupportedScmSystem();
  }
}
