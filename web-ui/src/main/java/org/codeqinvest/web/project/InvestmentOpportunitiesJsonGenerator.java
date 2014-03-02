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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
import java.util.TreeSet;

/**
 * The generator produces the necessary json tree for the
 * investment opportunities that are displayed on the project template
 * in a tree map.
 *
 * @author fmueller
 */
@Component
public class InvestmentOpportunitiesJsonGenerator {

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
    RootNode rootNode = new RootNode(analysis.getProject().getName());

    for (QualityViolation violation : analysis.getViolations()) {
      addArtefact(violation, rootNode, alreadyAddedArtefacts, nodeLookupTable);
    }

    rootNode.filterProfitableChildren();
    rootNode.updateChangeProbabilityOfProfitableChildren();
    rootNode.updateAutomaticChangeProbabilityAndEstimateOfAllChildren();
    return MAPPER.writeValueAsString(rootNode);
  }

  private void addArtefact(QualityViolation violation, RootNode root, Set<String> alreadyAddedArtefacts, Map<String, PackageNode> nodeLookupTable) {
    Artefact artefact = violation.getArtefact();
    if (!alreadyAddedArtefacts.contains(artefact.getName())) {

      alreadyAddedArtefacts.add(artefact.getName());
      double weightedProfit = weightedProfitCalculator.calculateWeightedProfit(violation);
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
  @JsonPropertyOrder({"name", "longName", "changeProbability", "automaticChangeProbability", "manualEstimate", "allChildren", "children"})
  private static class PackageNode {

    private final String name;
    private final String longName;

    private final Set<PackageNode> allChildren = new TreeSet<PackageNode>(new ByNameComparator());
    private final Set<PackageNode> children = new TreeSet<PackageNode>(new ByNameComparator());

    @Setter
    private int changeProbability;

    @Setter
    private int automaticChangeProbability;

    @Setter
    private Integer manualEstimate;

    PackageNode(String name, String longName) {
      this.name = name;
      this.longName = longName;
    }

    void addChildren(PackageNode node) {
      allChildren.add(node);
    }

    void filterProfitableChildren() {
      for (PackageNode child : getAllChildren()) {
        child.filterProfitableChildren();
        if (child.aggregateProfit() > 0.0) {
          children.add(child);
        }
      }
    }

    double aggregateProfit() {
      double profit = 0.0;
      for (PackageNode child : getAllChildren()) {
        double packageProfit = child.aggregateProfit();
        if (packageProfit > 0.0) {
          profit += packageProfit;
        }
      }
      return profit;
    }

    void updateChangeProbabilityOfProfitableChildren() {
      float sumOfChangeProbabilities = 0.0f;
      for (PackageNode node : getChildren()) {
        node.updateChangeProbabilityOfProfitableChildren();
        sumOfChangeProbabilities += node.getChangeProbability();
      }
      changeProbability = Math.round(sumOfChangeProbabilities / (float) getChildren().size());
    }

    void updateAutomaticChangeProbabilityAndEstimateOfAllChildren() {
      float sumOfAutomaticChangeProbabilities = 0.0f;

      Integer manualEstimateOfOneChildren = null;
      boolean isFirstEstimate = true;
      boolean hasEachChildrenSameManualEstimate = true;

      for (PackageNode node : getAllChildren()) {
        node.updateAutomaticChangeProbabilityAndEstimateOfAllChildren();

        if (manualEstimateOfOneChildren == null) {
          if (isFirstEstimate) {
            manualEstimateOfOneChildren = node.getManualEstimate();
          } else if (node.getManualEstimate() != null) {
            hasEachChildrenSameManualEstimate = false;
          }
        } else if (!manualEstimateOfOneChildren.equals(node.getManualEstimate()) || node.getManualEstimate() == null) {
          hasEachChildrenSameManualEstimate = false;
        }

        isFirstEstimate = false;
        sumOfAutomaticChangeProbabilities += node.getAutomaticChangeProbability();
      }
      automaticChangeProbability = Math.round(sumOfAutomaticChangeProbabilities / (float) getAllChildren().size());

      if (hasEachChildrenSameManualEstimate && manualEstimateOfOneChildren != null) {
        manualEstimate = manualEstimateOfOneChildren;
      }
    }
  }

  @EqualsAndHashCode(callSuper = true)
  @JsonPropertyOrder({"name", "allChildren", "children"})
  @JsonIgnoreProperties({"longName", "value", "changeProbability", "automaticChangeProbability", "manualEstimate"})
  private static class RootNode extends PackageNode {

    RootNode(String name) {
      super(name, "");
    }
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @JsonPropertyOrder({"name", "longName", "value", "changeProbability", "automaticChangeProbability", "manualEstimate"})
  @JsonIgnoreProperties({"allChildren", "children"})
  private static class ArtefactNode extends PackageNode {

    private final double value;

    ArtefactNode(Artefact artefact, double value) {
      super(artefact.getShortClassName(), artefact.getName());
      this.value = value;

      final int automaticChangeProbability = (int) Math.round(artefact.getChangeProbability() * 100);
      setManualEstimate(artefact.getManualEstimate());
      setAutomaticChangeProbability(automaticChangeProbability);

      if (artefact.hasManualEstimate()) {
        setChangeProbability(artefact.getManualEstimate());
      } else {
        setChangeProbability(automaticChangeProbability);
      }
    }

    @Override
    double aggregateProfit() {
      return value;
    }

    @Override
    void addChildren(PackageNode node) {
      throw new UnsupportedOperationException();
    }

    @Override
    void updateChangeProbabilityOfProfitableChildren() {
      // do nothing
    }

    @Override
    void updateAutomaticChangeProbabilityAndEstimateOfAllChildren() {
      // do nothing
    }
  }

  private static final class ByNameComparator implements Comparator<PackageNode> {

    @Override
    public int compare(PackageNode node, PackageNode otherNode) {
      return node.getName().compareToIgnoreCase(otherNode.getName());
    }
  }
}
