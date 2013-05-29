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

import com.google.common.collect.Sets;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * This service collects all projects that are
 * managed by a given sonar server instance.
 *
 * @author fmueller
 */
@Service
public class ProjectsCollectorService {

  public Set<ProjectInformation> collectAllProjects(SonarConnectionSettings connectionSettings) {
    Sonar sonar = new Sonar(new HttpClient4Connector(connectionSettings.asHostObject()));
    List<Resource> projectResources = sonar.findAll(new ResourceQuery());
    Set<ProjectInformation> projects = Sets.newHashSet();
    for (Resource resource : projectResources) {
      projects.add(new ProjectInformation(resource.getName(), resource.getKey()));
    }
    return projects;
  }
}
