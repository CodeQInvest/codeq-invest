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
package org.codeqinvest.web.investment;

import com.jayway.restassured.http.ContentType;
import org.codeqinvest.web.IntegrationTestHelper;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class QualityInvestmentPlanControllerIntegrationTest {

  @Test
  public void shouldHandleInvestmentRequestProperly() {
    given()
        .body("{\"basePackage\": \"project.web.payment.controller\", \"investment\": \"1h 45m\"}")
        .contentType(ContentType.JSON)
    .expect()
        .body("basePackage", equalTo("project.web.payment.controller"))
        .body("investmentInMinutes", equalTo(90))
        .body("profitInMinutes", equalTo(142))
        .body("roi", equalTo(158))
    .when()
        .put(IntegrationTestHelper.getUriWithHost("/projects/3/investment"));
  }
}
