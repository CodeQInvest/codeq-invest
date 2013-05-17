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
package org.codeqinvest.quality;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.codeqinvest.project.Project;

import javax.persistence.CascadeType;
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

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "ANALYSIS_ID", nullable = false)
  private List<QualityViolation> violations;

  protected QualityAnalysis() {
  }

  public QualityAnalysis(Project project, List<QualityViolation> violations) {
    this.project = project;
    this.violations = violations;
  }
}
