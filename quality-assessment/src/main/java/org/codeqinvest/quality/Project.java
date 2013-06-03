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
import lombok.Setter;
import lombok.ToString;
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.sonar.SonarConnectionSettings;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@Setter
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

  @Column(nullable = false)
  private String lowercaseName;

  @Column(nullable = false, length = 100)
  private String cronExpression;

  @ManyToOne(optional = false)
  @JoinColumn(name = "PROFILE_ID", nullable = false)
  private QualityProfile profile;

  private boolean hadAnalysis;

  @Embedded
  private SonarConnectionSettings sonarConnectionSettings;

  @Embedded
  private ScmConnectionSettings scmSettings;

  @Embedded
  private CodeChangeSettings codeChangeSettings;

  protected Project() {
  }

  public Project(String name, String cronExpression, QualityProfile profile,
                 SonarConnectionSettings sonarConnectionSettings, ScmConnectionSettings scmSettings,
                 CodeChangeSettings codeChangeSettings) {
    setName(name);
    this.cronExpression = cronExpression;
    this.profile = profile;
    this.sonarConnectionSettings = sonarConnectionSettings;
    this.scmSettings = scmSettings;
    this.codeChangeSettings = codeChangeSettings;
    this.hadAnalysis = false;
  }

  public final void setName(String name) {
    this.name = name;
    this.lowercaseName = name.toLowerCase();
  }

  public boolean hadAnalysis() {
    return hadAnalysis;
  }
}
