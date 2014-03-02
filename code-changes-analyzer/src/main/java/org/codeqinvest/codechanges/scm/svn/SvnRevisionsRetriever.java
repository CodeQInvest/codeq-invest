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
package org.codeqinvest.codechanges.scm.svn;

import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.joda.time.LocalDate;
import org.tmatesoft.svn.core.SVNException;

/**
 * This component retrieves revisions from a subversion repository.
 *
 * @author fmueller
 */
interface SvnRevisionsRetriever {

  /**
   * Retrieves all revisions of a subversion repository for one specified day.
   *
   * @throws org.tmatesoft.svn.core.SVNException
   *          if an error occurred during communication with the subversion server
   */
  DailyRevisions retrieveRevisions(ScmConnectionSettings connectionSettings, LocalDate day) throws SVNException;

  /**
   * Retrieves the last revisions of a subversion repository. The number of commits to retrieve is
   * specified.
   *
   * @throws org.tmatesoft.svn.core.SVNException
   *          if an error occurred during communication with the subversion server
   */
  Revisions retrieveRevisions(ScmConnectionSettings connectionSettings, int numberOfCommits) throws SVNException;
}
