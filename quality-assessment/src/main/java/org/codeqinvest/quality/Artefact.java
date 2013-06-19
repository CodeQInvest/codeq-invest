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

import com.google.common.base.Splitter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * TODO javadoc
 *
 * @author fmueller
 */
@Getter
@EqualsAndHashCode
@ToString
@Entity
public class Artefact implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String sonarIdentifier;

  @Setter
  @Column(nullable = false)
  private double changeProbability;

  @Setter
  private Integer manualEstimate;

  @Setter
  @Column(nullable = false)
  private double secureChangeProbability = 1.0;

  protected Artefact() {
  }

  public Artefact(String name, String sonarIdentifier) {
    this.name = name;
    this.sonarIdentifier = sonarIdentifier;
    this.changeProbability = 0.0;
  }

  public String getFilename() {
    if (name.isEmpty()) {
      return "";
    }
    return name.replace('.', '/') + ".java";
  }

  /**
   * Parses the class name out of the fully qualified class name and returns it.
   */
  public String getShortClassName() {
    String className = null;
    for (String packageName : Splitter.on('.').split(name)) {
      className = packageName;
    }
    return className;
  }

  public boolean hasManualEstimate() {
    return manualEstimate != null;
  }
}
