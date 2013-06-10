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
import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.CodeChurnCalculator;
import org.codeqinvest.codechanges.scm.DailyCodeChurn;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc2.ng.SvnDiffGenerator;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Calculates the code churn for files in a SVN repository.
 *
 * @author fmueller
 */
@Slf4j
@Service
public class SvnCodeChurnCalculatorService implements CodeChurnCalculator {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

  private final SvnRevisionsRetrieverService revisionsRetrieverService;
  private final SvnFileRetrieverService fileRetrieverService;

  @Autowired
  public SvnCodeChurnCalculatorService(SvnRevisionsRetrieverService revisionsRetrieverService, SvnFileRetrieverService fileRetrieverService) {
    this.revisionsRetrieverService = revisionsRetrieverService;
    this.fileRetrieverService = fileRetrieverService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DailyCodeChurn calculateCodeChurn(ScmConnectionSettings connectionSettings, String file, LocalDate day) throws CodeChurnCalculationException, ScmConnectionEncodingException {
    try {
      final Collection<SvnFileRevision> revisions = revisionsRetrieverService.getRevisions(connectionSettings, file, day);
      List<Double> codeChurnProportions = new ArrayList<Double>(revisions.size());
      for (SvnFileRevision revision : revisions) {
        long codeChurn = retrieveCodeChurn(connectionSettings, revision);
        long linesPreviousCommit = fileRetrieverService.getFile(connectionSettings, revision.getOldPath(), revision.getRevision() - 1).countLines();
        codeChurnProportions.add(codeChurn / (double) linesPreviousCommit);
      }
      return new DailyCodeChurn(day, codeChurnProportions);
    } catch (SVNException e) {
      log.error("Error with svn server communication occurred!", e);
      throw new CodeChurnCalculationException(e);
    } catch (UnsupportedEncodingException e) {
      log.error("An error with encoding settings of scm connection occurred! (settings: " + connectionSettings.toString() + ")", e);
      throw new ScmConnectionEncodingException(e);
    }
  }

  private long retrieveCodeChurn(ScmConnectionSettings connectionSettings, SvnFileRevision fileRevision) throws SVNException, UnsupportedEncodingException {
    long codeChurn = 0L;
    for (String line : retrieveDiffFromSvnServer(connectionSettings, fileRevision).split(LINE_SEPARATOR)) {
      if ((line.startsWith("+") && !line.startsWith("+++")) || (line.startsWith("-") && !line.startsWith("---"))) {
        codeChurn++;
      }
    }
    return codeChurn;
  }

  private String retrieveDiffFromSvnServer(ScmConnectionSettings connectionSettings, SvnFileRevision fileRevision) throws SVNException, UnsupportedEncodingException {
    SvnOperationFactory operationFactory = null;
    try {
      operationFactory = new SvnOperationFactory();
      if (connectionSettings.hasUsername()) {
        operationFactory.setAuthenticationManager(
            new BasicAuthenticationManager(connectionSettings.getUsername(), connectionSettings.getPassword()));
      }

      SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
      diffGenerator.setEncoding(connectionSettings.getEncoding());
      diffGenerator.setEOL(LINE_SEPARATOR.getBytes(Charset.forName(connectionSettings.getEncoding())));

      ByteArrayOutputStream diffOutput = new ByteArrayOutputStream();
      SvnDiff diff = operationFactory.createDiff();
      diff.setSources(fileRevision.getOldSvnTarget(connectionSettings), fileRevision.getNewSvnTarget(connectionSettings));
      diff.setDiffGenerator(diffGenerator);
      diff.setOutput(diffOutput);
      diff.run();

      return new String(diffOutput.toByteArray(), connectionSettings.getEncoding());
    } finally {
      if (operationFactory != null) {
        operationFactory.dispose();
      }
    }
  }
}
