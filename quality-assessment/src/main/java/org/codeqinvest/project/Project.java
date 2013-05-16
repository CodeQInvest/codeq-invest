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
package org.codeqinvest.project;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.codeqinvest.quality.QualityAnalysis;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.codeqinvest.sonar.SonarConnectionSettings;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "PROJECT")
public class Project implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 100)
  private String cronExpression;

  @Embedded
  private SonarConnectionSettings sonarConnectionSettings;

  @Embedded
  private ScmSettings scmSettings;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
  private List<QualityAnalysis> analyzes;

  protected Project() {
  }

  public Project(String name, String cronExpression, SonarConnectionSettings sonarConnectionSettings, ScmSettings scmSettings) {
    this.name = name;
    this.cronExpression = cronExpression;
    this.sonarConnectionSettings = sonarConnectionSettings;
    this.scmSettings = scmSettings;
  }
}
