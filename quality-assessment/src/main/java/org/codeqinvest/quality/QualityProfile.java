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
package org.codeqinvest.quality;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "QUALITY_PROFILE")
public class QualityProfile implements Serializable {

  @Setter
  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String lowercaseName;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile", fetch = FetchType.EAGER, orphanRemoval = true)
  private Set<QualityRequirement> requirements = new HashSet<QualityRequirement>();

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile", fetch = FetchType.EAGER, orphanRemoval = true)
  private Set<ChangeRiskAssessmentFunction> changeRiskAssessmentFunctions = new HashSet<ChangeRiskAssessmentFunction>();

  public QualityProfile() {
    // only used by frameworks
  }

  public QualityProfile(String name) {
    setName(name);
  }

  public final void setName(String name) {
    this.name = name;
    this.lowercaseName = name.toLowerCase();
  }

  public Set<QualityRequirement> getRequirements() {
    return Collections.unmodifiableSet(requirements);
  }

  public void addRequirement(QualityRequirement requirement) {
    requirements.add(requirement);
  }

  public void addChangeRiskAssessmentFunction(ChangeRiskAssessmentFunction changeRiskAssessmentFunction) {
    changeRiskAssessmentFunctions.add(changeRiskAssessmentFunction);
  }
}
