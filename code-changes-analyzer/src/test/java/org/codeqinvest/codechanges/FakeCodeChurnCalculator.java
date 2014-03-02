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
package org.codeqinvest.codechanges;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.codeqinvest.codechanges.scm.CodeChurn;
import org.codeqinvest.codechanges.scm.CodeChurnCalculationException;
import org.codeqinvest.codechanges.scm.CodeChurnCalculator;
import org.codeqinvest.codechanges.scm.DailyCodeChurn;
import org.codeqinvest.codechanges.scm.ScmConnectionEncodingException;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class FakeCodeChurnCalculator implements CodeChurnCalculator {

  private final Map<String, Map<LocalDate, DailyCodeChurn>> codeChurnByFileAndDay = Maps.newHashMap();
  private final Map<String, List<CodeChurn>> codeChurnByFile = Maps.newHashMap();

  void addCodeChurn(String file, DailyCodeChurn codeChurn) {
    if (!codeChurnByFileAndDay.containsKey(file)) {
      codeChurnByFileAndDay.put(file, new HashMap<LocalDate, DailyCodeChurn>());
    }
    codeChurnByFileAndDay.get(file).put(codeChurn.getDay(), codeChurn);
  }

  void addCodeChurnWithoutDay(String file, CodeChurn codeChurn) {
    if (!codeChurnByFile.containsKey(file)) {
      codeChurnByFile.put(file, new ArrayList<CodeChurn>());
    }
    codeChurnByFile.get(file).add(codeChurn);
  }

  @Override
  public Collection<DailyCodeChurn> calculateCodeChurn(ScmConnectionSettings connectionSettings, String file, LocalDate day, int numberOfDays)
      throws CodeChurnCalculationException, ScmConnectionEncodingException {
    if (!codeChurnByFileAndDay.containsKey(file) || !codeChurnByFileAndDay.get(file).containsKey(day)) {
      throw new CodeChurnCalculationException();
    }

    Set<DailyCodeChurn> codeChurns = Sets.newHashSet();
    for (int i = 0; i <= numberOfDays; i++) {
      DailyCodeChurn currentCodeChurn = codeChurnByFileAndDay.get(file).get(day.minusDays(i));
      if (currentCodeChurn != null) {
        codeChurns.add(currentCodeChurn);
      }
    }
    return codeChurns;
  }

  @Override
  public CodeChurn calculateCodeChurnForLastCommits(ScmConnectionSettings connectionSettings, String file, int numberOfCommits)
      throws CodeChurnCalculationException, ScmConnectionEncodingException {
    if (!codeChurnByFile.containsKey(file)) {
      throw new CodeChurnCalculationException();
    }

    List<CodeChurn> codeChurnOfCommits = new ArrayList<CodeChurn>();
    int i = 0;
    for (CodeChurn codeChurn : codeChurnByFile.get(file)) {
      if (i >= numberOfCommits) {
        break;
      }
      codeChurnOfCommits.add(codeChurn);
      i++;
    }

    CodeChurn codeChurns = new CodeChurn(new ArrayList<Double>());
    for (CodeChurn codeChurnOfCommit : codeChurnOfCommits) {
      codeChurns.addCodeChurnProportions(codeChurnOfCommit.getCodeChurnProportions());
    }
    return codeChurns;
  }
}
