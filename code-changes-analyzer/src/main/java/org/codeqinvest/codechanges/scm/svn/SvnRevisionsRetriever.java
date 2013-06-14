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
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * This component retrieves all revisions of a certain day in which
 * the specified file was changed.
 *
 * @author fmueller
 */
@Slf4j
@Component
class SvnRevisionsRetriever {

  private final ConcurrentMap<CachedRevisionKey, Collection<SvnFileRevision>> cachedRevisions = Maps.newConcurrentMap();

  /**
   * Retrieves all revision numbers of one day where the specified file was changed.
   *
   * @throws SVNException if an error occurred during communication with the subversion server
   */
  Collection<SvnFileRevision> getRevisions(final ScmConnectionSettings connectionSettings, final String file, final LocalDate day) throws SVNException {
    CachedRevisionKey revisionKey = new CachedRevisionKey(connectionSettings.getUrl(), file, day);
    Collection<SvnFileRevision> cached = findCachedRevisions(revisionKey);
    if (cached != null) {
      return cached;
    } else {
      Multimap<String, SvnFileRevision> revisions = retrieveRevisionsFromSubversionServer(connectionSettings, day);
      Collection<SvnFileRevision> foundRevisions = new ArrayList<SvnFileRevision>();
      for (Map.Entry<String, SvnFileRevision> fileRevisionEntry : revisions.entries()) {
        CachedRevisionKey key = new CachedRevisionKey(connectionSettings.getUrl(), fileRevisionEntry.getKey(), day);
        cachedRevisions.putIfAbsent(key, new ArrayList<SvnFileRevision>());
        cachedRevisions.get(key).add(fileRevisionEntry.getValue());

        if (fileRevisionEntry.getKey().endsWith(file)) {
          foundRevisions.add(fileRevisionEntry.getValue());
        }
      }
      return foundRevisions;
    }
  }

  private Collection<SvnFileRevision> findCachedRevisions(CachedRevisionKey key) {
    for (CachedRevisionKey revisionKey : cachedRevisions.keySet()) {
      if (revisionKey.getFile().endsWith(key.getFile())
          && revisionKey.getUrl().equals(key.getUrl())
          && revisionKey.getDay().equals(key.getDay())) {

        log.info("Return cashed revision for {}", key);
        return cachedRevisions.get(revisionKey);
      }
    }
    return null;
  }

  private Multimap<String, SvnFileRevision> retrieveRevisionsFromSubversionServer(ScmConnectionSettings connectionSettings, LocalDate day) throws SVNException {
    final SVNRepository repository = SvnRepositoryFactory.create(connectionSettings);
    final DateTime startTime = day.toDateTimeAtStartOfDay();
    final long startRevision = repository.getDatedRevision(startTime.toDate());
    final long endRevision = repository.getDatedRevision(startTime.withTime(23, 59, 59, 999).toDate());

    log.info("Start retrieving revisions for day {} with connection {}", day, connectionSettings);
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

    log.info("Found {} changes for day {} with connection {}", revisions.values().size(), day, connectionSettings);
    return revisions;
  }

  public void evictCache() {
    cachedRevisions.clear();
  }

  @Data
  private static final class CachedRevisionKey {

    private final String url;
    private final String file;
    private final LocalDate day;
  }
}
