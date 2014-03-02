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
package org.codeqinvest.web.sonar;

import com.jayway.restassured.http.ContentType;
import org.codeqinvest.web.IntegrationTestHelper;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SonarControllerIntegrationTest {

  @Test
  public void shouldReturnProperJsonForSonarReachabilityRequest() {
    given()
        .body("{\"url\": \"http://localhost\"}")
        .contentType(ContentType.JSON)
    .expect()
        .body("ok", equalTo(false))
    .when()
        .put(IntegrationTestHelper.getUriWithHost("/sonar/reachable"));
  }
}
