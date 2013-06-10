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

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This services retrieves all revisions of a certain day in which
 * the specified file was changed.
 *
 * @author fmueller
 */
@Slf4j
@Service
class SvnRevisionsRetrieverService {

  /**
   * Retrieves all revision numbers of one day where the specified file was changed.
   *
   * @throws SVNException if an error occurred during communication with the subversion server
   */
  Collection<SvnFileRevision> getRevisions(final ScmConnectionSettings connectionSettings, final String file, final LocalDate day) throws SVNException {
    final SVNRepository repository = SvnRepositoryFactory.create(connectionSettings);
    final DateTime startTime = day.toDateTimeAtStartOfDay();
    final long startRevision = repository.getDatedRevision(startTime.toDate());
    final long endRevision = repository.getDatedRevision(startTime.withTime(23, 59, 59, 999).toDate());

    final Collection<SvnFileRevision> revisions = new ArrayList<SvnFileRevision>();
    repository.log(new String[]{file}, startRevision, endRevision, true, true, new ISVNLogEntryHandler() {

      @Override
      public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
        for (SVNLogEntryPath logEntryPath : logEntry.getChangedPaths().values()) {
          if (logEntryPath.getPath().endsWith(file)) {
            if (logEntryPath.getCopyPath() != null) {
              revisions.add(new SvnFileRevision(logEntry.getRevision(), logEntryPath.getCopyPath(), logEntryPath.getPath()));
            } else {
              revisions.add(new SvnFileRevision(logEntry.getRevision(), logEntryPath.getPath(), logEntryPath.getPath()));
            }
          }
        }
      }
    });
    log.debug("Found {} revisions for file {} on day {}", revisions.size(), file, day);
    return revisions;
  }
}
