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
package org.codeqinvest.codechanges.scm.factory;

import org.codeqinvest.codechanges.scm.CodeChurnCalculator;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.codechanges.scm.svn.SvnCodeChurnCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This helper component can be used to get a fitting {@code CodeChurnCalculator} instance.
 *
 * @author fmueller
 */
@Component
public class CodeChurnCalculatorFactory {

  private final SvnCodeChurnCalculatorService svnCodeChurnCalculator;

  @Autowired
  public CodeChurnCalculatorFactory(SvnCodeChurnCalculatorService svnCodeChurnCalculator) {
    this.svnCodeChurnCalculator = svnCodeChurnCalculator;
  }

  /**
   * Creates a new {@code CodeChurnCalculator} instance with the specified {@code connectionSettings}
   * and the saved SCM type in these settings.
   */
  public CodeChurnCalculator create(ScmConnectionSettings connectionSettings) {
    return getCodeChurnCalculator(connectionSettings);
  }

  public void reset(ScmConnectionSettings connectionSettings) {
    getCodeChurnCalculator(connectionSettings).reset();
  }

  private CodeChurnCalculator getCodeChurnCalculator(ScmConnectionSettings connectionSettings) {
    if (connectionSettings.getType() == SupportedScmSystem.SVN.getType()) {
      return svnCodeChurnCalculator;
    }
    throw new UnsupportedScmSystem();
  }
}
