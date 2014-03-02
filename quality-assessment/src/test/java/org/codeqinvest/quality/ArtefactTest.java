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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ArtefactTest {

  @Test
  public void shouldConvertPackageNameToFilenameProperly() {
    assertThat(new Artefact("org.my.class.AbcDe", "").getFilename()).isEqualTo("org/my/class/AbcDe.java");
  }

  @Test
  public void shouldConvertClassNameToFilenameProperly() {
    assertThat(new Artefact("AbcDe", "").getFilename()).isEqualTo("AbcDe.java");
  }

  @Test
  public void shouldConvertEmptyPackageNameToFilenameProperly() {
    assertThat(new Artefact("", "").getFilename()).isEqualTo("");
  }

  @Test
  public void shouldConvertFullyQualifiedClassNameToShortClassName() {
    assertThat(new Artefact("org.util.MyClass", "").getShortClassName()).isEqualTo("MyClass");
  }

  @Test
  public void shouldConvertFullyQualifiedClassNameWithoutPackagesToShortClassName() {
    assertThat(new Artefact("Class123", "").getShortClassName()).isEqualTo("Class123");
  }

  @Test
  public void shouldConvertEmptyFullyQualifiedClassNameToShortClassName() {
    assertThat(new Artefact("", "").getShortClassName()).isEqualTo("");
  }
}
