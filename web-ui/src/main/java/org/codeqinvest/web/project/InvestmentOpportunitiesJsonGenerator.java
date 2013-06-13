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
package org.codeqinvest.web.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.codeqinvest.investment.profit.WeightedProfitCalculator;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The geneator produces the necessary json tree for the
 * investment oppotunities that are displayed on the project template
 * in a tree map.
 *
 * @author fmueller
 */
@Component
class InvestmentOpportunitiesJsonGenerator {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Splitter PACKAGE_SPLITTER = Splitter.on('.');

  private final WeightedProfitCalculator weightedProfitCalculator;

  @Autowired
  InvestmentOpportunitiesJsonGenerator(WeightedProfitCalculator weightedProfitCalculator) {
    this.weightedProfitCalculator = weightedProfitCalculator;
  }

  public String generate(QualityAnalysis analysis) throws JsonProcessingException {
    Set<String> alreadyAddedArtefacts = Sets.newHashSet();
    Map<String, PackageNode> nodeLookupTable = Maps.newHashMap();
    Node rootNode = new Node(analysis.getProject().getName());

    for (QualityViolation violation : analysis.getViolations()) {
      addArtefact(violation, rootNode, alreadyAddedArtefacts, nodeLookupTable);
    }

    for (PackageNode packageNode : nodeLookupTable.values()) {
      // TODO can be optimized: only update the top packages
      packageNode.updateChangeProbability();
    }
    return MAPPER.writeValueAsString(rootNode);
  }

  private void addArtefact(QualityViolation violation, Node root, Set<String> alreadyAddedArtefacts, Map<String, PackageNode> nodeLookupTable) {
    Artefact artefact = violation.getArtefact();
    double weightedProfit = weightedProfitCalculator.calculateWeightedProfit(violation);
    if (weightedProfit <= 0.0) {
      return;
    }

    if (!alreadyAddedArtefacts.contains(artefact.getName())) {

      alreadyAddedArtefacts.add(artefact.getName());
      ArtefactNode artefactNode = new ArtefactNode(artefact, weightedProfit);

      PackageNode currentPackageNode = null;
      for (String packageName : getAllPackageNamesReversed(artefact)) {
        if (nodeLookupTable.containsKey(packageName)) {
          PackageNode node = nodeLookupTable.get(packageName);
          if (currentPackageNode != null) {
            node.addChildren(currentPackageNode);
          } else {
            node.addChildren(artefactNode);
          }
          return;
        } else {
          PackageNode packageNode = new PackageNode(getLastPackageName(packageName));
          nodeLookupTable.put(packageName, packageNode);
          if (currentPackageNode != null) {
            packageNode.addChildren(currentPackageNode);
          } else {
            packageNode.addChildren(artefactNode);
          }
          currentPackageNode = packageNode;
        }
      }

      if (currentPackageNode != null) {
        root.addChildren(currentPackageNode);
      } else {
        root.addChildren(artefactNode);
      }
    }
  }

  private String getLastPackageName(String packageName) {
    String last = null;
    for (String name : PACKAGE_SPLITTER.split(packageName)) {
      last = name;
    }
    return last;
  }

  private List<String> getAllPackageNamesReversed(Artefact artefact) {
    List<String> packages = new ArrayList<String>();
    StringBuilder packagePath = new StringBuilder();
    for (String packageName : PACKAGE_SPLITTER.split(artefact.getName())) {
      if (packageName.equals(artefact.getShortClassName())) {
        break;
      }
      if (packagePath.length() > 0) {
        packagePath.append('.');
      }
      packagePath.append(packageName);
      packages.add(packagePath.toString());
    }
    Collections.reverse(packages);
    return packages;
  }

  @Getter
  private static class Node {

    private final String name;
    private final List<Node> children = new ArrayList<Node>();

    Node(String name) {
      this.name = name;
    }

    void addChildren(Node node) {
      children.add(node);
    }
  }

  @Getter
  @JsonPropertyOrder({"name", "changeProbability", "children"})
  private static class PackageNode extends Node {

    private long changeProbability;

    PackageNode(String name) {
      super(name);
    }

    PackageNode(String name, long changeProbability) {
      super(name);
      this.changeProbability = changeProbability;
    }

    void updateChangeProbability() {
      double sumOfChangeProbabilities = 0.0;
      for (Node node : getChildren()) {
        if (node instanceof PackageNode) {
          PackageNode packageNode = ((PackageNode) node);
          packageNode.updateChangeProbability();
          sumOfChangeProbabilities += packageNode.getChangeProbability();
        }
      }
      changeProbability = Math.round(sumOfChangeProbabilities / (double) getChildren().size());
    }
  }

  @Getter
  @JsonPropertyOrder({"name", "value", "changeProbability"})
  @JsonIgnoreProperties("children")
  private static class ArtefactNode extends PackageNode {

    private final double value;

    ArtefactNode(Artefact artefact, double value) {
      super(artefact.getShortClassName(), Math.round(artefact.getChangeProbability() * 100L));
      this.value = value;
    }

    @Override
    void addChildren(Node node) {
      throw new UnsupportedOperationException();
    }

    @Override
    void updateChangeProbability() {
      // do nothing
    }
  }
}
