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
package org.codeqinvest.web.project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.codeqinvest.investment.profit.WeightedProfitCalculator;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.Project;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvestmentOpportunitiesJsonGeneratorTest {

  private InvestmentOpportunitiesJsonGenerator generator;

  private WeightedProfitCalculator weightedProfitCalculator;

  private Project project;

  private ObjectMapper mapper;

  @Before
  public void setUp() {
    project = mock(Project.class);
    when(project.getName()).thenReturn("Dummy Project");

    weightedProfitCalculator = mock(WeightedProfitCalculator.class);
    generator = new InvestmentOpportunitiesJsonGenerator(weightedProfitCalculator);
    mapper = new ObjectMapper();
  }

  @Test
  public void emptyAnalysis() throws IOException {
    QualityAnalysis analysis = QualityAnalysis.success(project, Collections.<QualityViolation>emptyList());
    String expectedJson = "{ \"name\": \"Dummy Project\", \"allChildren\": [], \"children\": []}";
    assertThat(generate(analysis).toString()).isEqualTo(mapper.readTree(expectedJson).toString());
  }

  @Test
  public void analysisWithOneArtefact() throws IOException {
    Artefact artefact = new Artefact("org.project.MyClass", "DUMMY");
    artefact.setChangeProbability(0.6);
    QualityViolation violation = new QualityViolation(artefact, null, 5, 10, 0, "");
    QualityAnalysis analysis = QualityAnalysis.success(project, Arrays.asList(violation));

    when(weightedProfitCalculator.calculateWeightedProfit(violation)).thenReturn(1234.0);

    JsonNode generatedJson = generate(analysis);
    ArrayNode rootPackagNode = (ArrayNode) generatedJson.get("children");
    assertThat(rootPackagNode.get(0).get("changeProbability").asInt()).isEqualTo(60);
    assertThat(rootPackagNode.get(0).get("children").get(0).get("children").get(0).get("value").asDouble()).isEqualTo(1234.0);
  }

  @Test
  public void analysisWithManyArtefactsAndManyViolations() throws IOException {
    Artefact artefact1 = new Artefact("project.A", "DUMMY");
    Artefact artefact2 = new Artefact("project.B", "DUMMY");
    Artefact artefact3 = new Artefact("project.test.util.C", "DUMMY");
    Artefact artefact4 = new Artefact("project.test.util.D", "DUMMY");
    Artefact artefact5 = new Artefact("E", "DUMMY");

    artefact1.setChangeProbability(0.1);
    artefact2.setChangeProbability(0.2);
    artefact3.setChangeProbability(0.3);
    artefact4.setChangeProbability(0.4);
    artefact5.setChangeProbability(0.5);

    QualityViolation violation1 = new QualityViolation(artefact1, null, 0, 0, 0, "");
    QualityViolation violation2 = new QualityViolation(artefact2, null, 0, 0, 0, "");
    QualityViolation violation3 = new QualityViolation(artefact3, null, 0, 0, 0, "");
    QualityViolation violation4 = new QualityViolation(artefact4, null, 0, 0, 0, "");
    QualityViolation violation5 = new QualityViolation(artefact5, null, 0, 0, 0, "");

    QualityAnalysis analysis = QualityAnalysis.success(project,
        Arrays.asList(violation1, violation2, violation3, violation4, violation5));

    when(weightedProfitCalculator.calculateWeightedProfit(violation1)).thenReturn(10.0);
    when(weightedProfitCalculator.calculateWeightedProfit(violation2)).thenReturn(20.0);
    when(weightedProfitCalculator.calculateWeightedProfit(violation3)).thenReturn(30.0);
    when(weightedProfitCalculator.calculateWeightedProfit(violation4)).thenReturn(40.0);
    when(weightedProfitCalculator.calculateWeightedProfit(violation5)).thenReturn(50.0);

    JsonNode generatedJson = generate(analysis);
    ArrayNode rootPackagNode = (ArrayNode) generatedJson.get("children");
    assertThat(rootPackagNode.get(1).get("changeProbability").asInt()).isEqualTo(22);
  }

  @Test
  public void profitablePacakgesAndArtefactsShouldBeFilteredProperly() throws IOException {
    Artefact artefact1 = new Artefact("org.project.test.util.C", "DUMMY");
    Artefact artefact2 = new Artefact("org.project.test.util.D", "DUMMY");
    Artefact artefact3 = new Artefact("org.project.test.E", "DUMMY");
    Artefact artefact4 = new Artefact("org.project.F", "DUMMY");

    artefact1.setChangeProbability(0.1);
    artefact1.setManualEstimate(40);

    artefact2.setChangeProbability(0.2);
    artefact2.setManualEstimate(80);

    artefact3.setChangeProbability(0.0);
    artefact4.setChangeProbability(0.0);

    QualityViolation violation1 = new QualityViolation(artefact1, null, 0, 0, 0, "");
    QualityViolation violation2 = new QualityViolation(artefact2, null, 0, 0, 0, "");
    QualityViolation violation3 = new QualityViolation(artefact3, null, 0, 0, 0, "");
    QualityViolation violation4 = new QualityViolation(artefact4, null, 0, 0, 0, "");

    QualityAnalysis analysis = QualityAnalysis.success(project, Arrays.asList(violation1, violation2, violation3, violation4));

    when(weightedProfitCalculator.calculateWeightedProfit(violation1)).thenReturn(10.0);
    when(weightedProfitCalculator.calculateWeightedProfit(violation2)).thenReturn(20.0);
    when(weightedProfitCalculator.calculateWeightedProfit(violation3)).thenReturn(-10.0);
    when(weightedProfitCalculator.calculateWeightedProfit(violation4)).thenReturn(-20.0);

    JsonNode generatedJson = generate(analysis);
    ArrayNode rootPackagNode = (ArrayNode) generatedJson.get("children");
    assertThat(rootPackagNode.get(0).get("changeProbability").asInt()).isEqualTo(60);
  }

  private JsonNode generate(QualityAnalysis analysis) throws IOException {
    String jsonString = generator.generate(analysis);
    return mapper.readTree(jsonString);
  }
}
