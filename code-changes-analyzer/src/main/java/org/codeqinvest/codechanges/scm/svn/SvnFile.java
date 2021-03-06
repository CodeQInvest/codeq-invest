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

/**
 * Value object that encapsulates the file content
 * with the corresponding line separator. The line
 * separator is based on the eol style of the svn
 * server.
 *
 * @author fmueller
 */
final class SvnFile {

  private final String content;
  private final String lineSeparator;

  SvnFile(String content, String lineSeparator) {
    this.content = content;
    this.lineSeparator = lineSeparator;
  }

  /**
   * Counts the lines of the file content.
   * For that, the line separator is used.
   *
   * @return the number of lines of this file
   */
  int countLines() {
    if (content.isEmpty()) {
      return 0;
    }
    return content.split(lineSeparator).length;
  }
}
