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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

@Slf4j
@Component
class DefaultSvnRevisionsRetriever implements SvnRevisionsRetriever {

  /**
   * {@inheritDoc}
   */
  @Override
  @Cacheable("svnRevisions")
  public DailyRevisions retrieveRevisions(ScmConnectionSettings connectionSettings, LocalDate day) throws SVNException {
    log.info("Retrieve revisions on day {} for {}", day, connectionSettings);
    final SVNRepository repository = SvnRepositoryFactory.create(connectionSettings);
    final LocalDateTime startTime = day.toDateTimeAtStartOfDay().toLocalDateTime();
    final long startRevision = repository.getDatedRevision(startTime.toDate());
    final long endRevision = repository.getDatedRevision(startTime.withTime(23, 59, 59, 999).toDate());

    final Multimap<String, SvnFileRevision> revisions = ArrayListMultimap.create();
    repository.log(null, startRevision, endRevision, true, true, new ISVNLogEntryHandler() {

      @Override
      public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
        for (SVNLogEntryPath logEntryPath : logEntry.getChangedPaths().values()) {
          if (logEntryPath.getCopyPath() != null) {
            revisions.put(logEntryPath.getPath(), new SvnFileRevision(logEntry.getRevision(), logEntryPath.getCopyPath(), logEntryPath.getPath()));
          } else {
            revisions.put(logEntryPath.getPath(), new SvnFileRevision(logEntry.getRevision(), logEntryPath.getPath(), logEntryPath.getPath()));
          }
        }
      }
    });

    log.debug("Found {} changes for day {} with connection {}", revisions.values().size(), day, connectionSettings);
    return new DailyRevisions(day, revisions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Cacheable("svnRevisions")
  public Revisions retrieveRevisions(ScmConnectionSettings connectionSettings, int numberOfCommits) throws SVNException {
    log.info("Retrieve revisions on last {} commits for {}", numberOfCommits, connectionSettings);
    final SVNRepository repository = SvnRepositoryFactory.create(connectionSettings);
    final long startRevision = repository.getLatestRevision();
    final long endRevision = startRevision - numberOfCommits;

    final Multimap<String, SvnFileRevision> revisions = ArrayListMultimap.create();
    repository.log(null, startRevision, endRevision, true, true, new ISVNLogEntryHandler() {

      @Override
      public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
        for (SVNLogEntryPath logEntryPath : logEntry.getChangedPaths().values()) {
          if (logEntryPath.getCopyPath() != null) {
            revisions.put(logEntryPath.getPath(), new SvnFileRevision(logEntry.getRevision(), logEntryPath.getCopyPath(), logEntryPath.getPath()));
          } else {
            revisions.put(logEntryPath.getPath(), new SvnFileRevision(logEntry.getRevision(), logEntryPath.getPath(), logEntryPath.getPath()));
          }
        }
      }
    });

    log.debug("Found {} changes for last {} commits with connection {}", revisions.values().size(), numberOfCommits, connectionSettings);
    return new Revisions(revisions);
  }
}
