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
package org.codeqinvest.quality.analysis;

import com.google.common.base.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.codeqinvest.quality.QualityViolation;

import java.util.List;

/**
 * A value object that encapsulates the result of a
 * calculation for all violations of a project that is
 * computed in {@link ViolationsCalculatorService}.
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@ToString
final class ViolationsAnalysisResult {

  private final boolean successful;
  private final List<QualityViolation> violations;
  private final Optional<String> failureReason;

  private ViolationsAnalysisResult(boolean successful, List<QualityViolation> violations, Optional<String> failureReason) {
    this.successful = successful;
    this.violations = violations;
    this.failureReason = failureReason;
  }

  static ViolationsAnalysisResult createSuccessfulAnalysis(List<QualityViolation> violations) {
    return new ViolationsAnalysisResult(true, violations, Optional.<String>absent());
  }

  static ViolationsAnalysisResult createFailedAnalysis(List<QualityViolation> violations, String failureReason) {
    return new ViolationsAnalysisResult(false, violations, Optional.of(failureReason));
  }
}
