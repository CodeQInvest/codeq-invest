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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.codeqinvest.investment.profit.WeightedProfitCalculator;
import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.QualityViolation;
import org.codeqinvest.quality.analysis.QualityAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    rootNode.filterProfitableChildren();
    for (PackageNode packageNode : nodeLookupTable.values()) {
      packageNode.updateChangeProbability();
    }
    return MAPPER.writeValueAsString(rootNode);
  }

  private void addArtefact(QualityViolation violation, Node root, Set<String> alreadyAddedArtefacts, Map<String, PackageNode> nodeLookupTable) {
    Artefact artefact = violation.getArtefact();
    double weightedProfit = weightedProfitCalculator.calculateWeightedProfit(violation);

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
          PackageNode packageNode = new PackageNode(getLastPackageName(packageName), packageName);
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
  @EqualsAndHashCode
  private static class Node {

    private final String name;
    private final SortedSet<Node> allChildren = new TreeSet<Node>(new ByNameComparator());
    private final SortedSet<Node> children = new TreeSet<Node>(new ByNameComparator());

    Node(String name) {
      this.name = name;
    }

    void addChildren(Node node) {
      allChildren.add(node);
    }

    void filterProfitableChildren() {
      for (Node child : getAllChildren()) {
        child.filterProfitableChildren();

        if (child instanceof ArtefactNode) {
          ArtefactNode artefactNode = (ArtefactNode) child;
          if (artefactNode.getValue() > 0.0) {
            children.add(child);
          }
        } else if (child instanceof PackageNode) {
          PackageNode packageNode = (PackageNode) child;
          if (packageNode.aggregateProfit() > 0.0) {
            children.add(child);
          }
        }
      }
    }
  }

  @Getter
  @EqualsAndHashCode
  @JsonPropertyOrder({"name", "longName", "changeProbability", "allChildren", "children"})
  private static class PackageNode extends Node {

    private final String longName;
    private long changeProbability;

    PackageNode(String name, String longName) {
      super(name);
      this.longName = longName;
    }

    PackageNode(String name, String longName, long changeProbability) {
      super(name);
      this.longName = longName;
      this.changeProbability = changeProbability;
    }

    double aggregateProfit() {
      double profit = 0.0;
      for (Node child : getAllChildren()) {
        if (child instanceof ArtefactNode) {
          profit += ((ArtefactNode) child).getValue();
        } else if (child instanceof PackageNode) {
          profit += ((PackageNode) child).aggregateProfit();
        }
      }
      return profit;
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
  @EqualsAndHashCode
  @JsonPropertyOrder({"name", "longName", "value", "changeProbability"})
  @JsonIgnoreProperties({"allChildren", "children"})
  private static class ArtefactNode extends PackageNode {

    private final double value;

    ArtefactNode(Artefact artefact, double value) {
      super(artefact.getShortClassName(), artefact.getName(), Math.round(artefact.getChangeProbability() * 100L));
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

  private static final class ByNameComparator implements Comparator<Node> {

    @Override
    public int compare(Node node, Node otherNode) {
      return node.getName().compareToIgnoreCase(otherNode.getName());
    }
  }
}
