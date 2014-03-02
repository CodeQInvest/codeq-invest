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

import com.google.common.base.Splitter;
import lombok.Data;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulate the path and the revision number of a file
 * in at svn repository.
 *
 * @author fmueller
 */
@Data
class SvnFileRevision {

  private final long revision;
  private final String oldPath;
  private final String newPath;

  SvnTarget getOldSvnTarget(ScmConnectionSettings connectionSettings) throws SVNException {
    return SvnTarget.fromURL(buildUri(connectionSettings, oldPath), SVNRevision.create(revision - 1));
  }

  SvnTarget getNewSvnTarget(ScmConnectionSettings connectionSettings) throws SVNException {
    return SvnTarget.fromURL(buildUri(connectionSettings, newPath), SVNRevision.create(revision));
  }

  String getFilePartOfOldPath(ScmConnectionSettings connectionSettings) throws SVNException {
    SVNURL svnurl = SVNURL.parseURIEncoded(connectionSettings.getUrl());

    List<String> splittedBasePath = splitUrl(svnurl.getPath());
    List<String> splittedOldPath = splitUrl(oldPath);
    int i = 0;
    boolean foundBeginningOfSomeCommonParts = false;
    for (String s : splittedBasePath) {
      if (!foundBeginningOfSomeCommonParts && s.equalsIgnoreCase(splittedOldPath.get(0))) {
        foundBeginningOfSomeCommonParts = true;
      }
      if (foundBeginningOfSomeCommonParts) {
        i++;
      }
    }

    StringBuilder filePart = new StringBuilder();
    boolean isFirst = true;
    for (; i < splittedOldPath.size(); i++) {
      if (!isFirst) {
        filePart.append('/');
      }
      filePart.append(splittedOldPath.get(i));
      isFirst = false;
    }
    return filePart.toString();
  }

  private SVNURL buildUri(ScmConnectionSettings connectionSettings, String path) throws SVNException {
    SVNURL svnurl = SVNURL.parseURIEncoded(connectionSettings.getUrl());
    return SVNURL.create(svnurl.getProtocol(), svnurl.getUserInfo(), svnurl.getHost(), svnurl.getPort(), buildPath(svnurl.getPath(), path), true);
  }

  private String buildPath(String basePath, String newPath) {
    StringBuilder path = new StringBuilder();
    int i = 0;
    List<String> splittedBasePath = splitUrl(basePath);
    List<String> splittedNewPath = splitUrl(newPath);
    for (String s : splittedBasePath) {
      if (s.equalsIgnoreCase(splittedNewPath.get(0))) {
        break;
      }
      if (i != 0) {
        path.append('/');
      }
      path.append(s);
      i++;
    }
    for (String s : splittedNewPath) {
      if (i != 0) {
        path.append('/');
      }
      path.append(s);
      i++;
    }

    return path.toString();
  }

  private List<String> splitUrl(String url) {
    List<String> splittedUrl = new ArrayList<String>();
    for (String splitted : Splitter.on('/').split(url)) {
      if (!splitted.isEmpty()) {
        splittedUrl.add(splitted);
      }
    }
    return splittedUrl;
  }
}
