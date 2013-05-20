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
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * This service retrieves files from a svn repository in a
 * certain revision.
 *
 * @author fmueller
 */
@Slf4j
@Service
class SvnFileRetrieverService {

  /**
   * Loads a given version (revision) of a file from a svn server.
   *
   * @return the loaded file from the svn server
   * @throws SVNException                 if errors occur during communication with the svn server
   * @throws UnsupportedEncodingException if the read bytes cannot be converted to string with encoding supplied by {@code connectionSettings}
   */
  SvnFile getFile(ScmConnectionSettings connectionSettings, String file, long revision) throws SVNException, UnsupportedEncodingException {
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    SVNProperties properties = new SVNProperties();
    SvnRepositoryFactory.create(connectionSettings).getFile(file, revision, properties, content);

    final String eolStyle = properties.getStringValue(SVNProperty.EOL_STYLE);
    final String fileContent = new String(content.toByteArray(), connectionSettings.getEncoding());
    log.debug("Retrieved revision {} of file {} ({} bytes, eol = {}) from subversion", revision, file, content.size(), eolStyle);
    return new SvnFile(fileContent, getLineSeparator(eolStyle));
  }

  private String getLineSeparator(String eolStyle) {
    if (eolStyle.equals(SVNProperty.EOL_STYLE_CR)) {
      return "\r";
    } else if (eolStyle.equals(SVNProperty.EOL_STYLE_LF)) {
      return "\n";
    } else if (eolStyle.equals(SVNProperty.EOL_STYLE_CRLF)) {
      return "\r\n";
    } else {
      return System.getProperty("line.separator", "\n");
    }
  }
}
