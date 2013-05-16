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
package org.codeqinvest.sonar;

import lombok.extern.slf4j.Slf4j;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * This service collects all production classes of a project
 * that is managed by a given sonar server instance.
 *
 * @author fmueller
 */
@Slf4j
@Service
public class ResourcesCollectorService {

  public Collection<Resource> collectAllResourcesForProject(SonarConnectionSettings connectionSettings) {
    if (!connectionSettings.hasProject()) {
      throw new IllegalArgumentException("you can only collect resources with connection settings that has a project");
    }
    Sonar sonar = new Sonar(new HttpClient4Connector(connectionSettings.asHostObject()));
    List<Resource> resources = sonar.findAll(ResourceQuery.create(connectionSettings.getProject())
        .setAllDepths()
        .setScopes("FIL")
        .setQualifiers("CLA"));
    log.info("Found {} classes for project {}", resources.size(), connectionSettings.getProject());
    return resources;
  }
}
