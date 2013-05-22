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
import org.codeqinvest.codechanges.scm.ScmConnectionSettings;
import org.codeqinvest.sonar.SonarConnectionSettings;

import javax.persistence.*;
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

  @ManyToOne(optional = false)
  @JoinColumn(name = "PROFILE_ID", nullable = false)
  private QualityProfile profile;

  @Embedded
  private SonarConnectionSettings sonarConnectionSettings;

  @Embedded
  private ScmConnectionSettings scmSettings;

  @Embedded
  private CodeChangeSettings codeChangeSettings;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
  private List<QualityAnalysis> analyzes;

  protected Project() {
  }

  public Project(String name, String cronExpression, QualityProfile profile,
                 SonarConnectionSettings sonarConnectionSettings, ScmConnectionSettings scmSettings,
                 CodeChangeSettings codeChangeSettings) {
    this.name = name;
    this.cronExpression = cronExpression;
    this.profile = profile;
    this.sonarConnectionSettings = sonarConnectionSettings;
    this.scmSettings = scmSettings;
    this.codeChangeSettings = codeChangeSettings;
  }
}
