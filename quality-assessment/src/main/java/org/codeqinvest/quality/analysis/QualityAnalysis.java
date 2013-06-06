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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityViolation;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "QUALITY_ANALYSIS")
public class QualityAnalysis implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "PROJECT_ID", nullable = false, updatable = false)
  private Project project;

  private DateTime created;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "ANALYSIS_ID", nullable = false)
  private List<QualityViolation> violations;

  @Column(nullable = false)
  private boolean successful;

  private String failureReason;

  protected QualityAnalysis() {
  }

  private QualityAnalysis(Project project, List<QualityViolation> violations, boolean successful, String failureReason) {
    this.project = project;
    this.violations = violations;
    this.successful = successful;
    this.failureReason = failureReason;
    created = DateTime.now();
  }

  public static QualityAnalysis success(Project project, List<QualityViolation> violations) {
    return new QualityAnalysis(project, violations, true, null);
  }

  public static QualityAnalysis failed(Project project, List<QualityViolation> violations, String failureReason) {
    return new QualityAnalysis(project, violations, false, failureReason);
  }
}
