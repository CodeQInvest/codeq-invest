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

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * This factory offers methods to build {@code SVNRepository} objects conveniently.
 *
 * @author fmueller
 */
final class SvnRepositoryFactory {

  private SvnRepositoryFactory() {
  }

  /**
   * Creates new {@code SVNRepository} objects based on the data of the
   * supplied {œcode connectionSettings}.
   *
   * @throws SVNException if the supplied url of the {@code connectionSettings} cannot be parsed
   */
  static SVNRepository create(ScmConnectionSettings connectionSettings) throws SVNException {
    SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(connectionSettings.getUrl()));
    if (connectionSettings.hasUsername()) {
      repository.setAuthenticationManager(
          new BasicAuthenticationManager(connectionSettings.getUsername(), connectionSettings.getPassword()));
    }
    return repository;
  }
}
