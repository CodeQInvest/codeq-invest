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
package org.codeqinvest.sonar;


import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author oliver.wehrens
 */
public class ProjectInformationTest {

  private ProjectInformation A = new ProjectInformation("A", "");
  private ProjectInformation B = new ProjectInformation("B", "");
  private ProjectInformation a = new ProjectInformation("a", "");
  private ProjectInformation nullName = new ProjectInformation(null, "");

  private int comesBefore = -1;
  private int areTheSame = 0;
  private int comesAfter = 1;

  @Test
  public void A_is_sorted_before_B() {
    assertThat(A.compareTo(B)).isEqualTo(comesBefore);
  }

  @Test
  public void a_is_sorted_before_B() {
    assertThat(a.compareTo(B)).isEqualTo(comesBefore);
  }

  @Test
  public void B_is_sorted_after_A() {
    assertThat(B.compareTo(A)).isEqualTo(comesAfter);
  }

  @Test
  public void A_and_a_are_the_same() {
    assertThat(a.compareTo(A)).isEqualTo(areTheSame);
  }

  @Test
  public void compared_to_null_is_the_same() {
    assertThat(B.compareTo(null)).isEqualTo(comesAfter);
  }

  @Test
  public void compared_to_null_name_is_sorted_after() {
    assertThat(B.compareTo(nullName)).isEqualTo(comesAfter);
  }

  @Test
  public void compared__valid_projectInfo_to_null_is_sorted_after() {
    assertThat(nullName.compareTo(A)).isEqualTo(comesAfter);
  }

}
