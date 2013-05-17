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
package org.codeqinvest.quality.analysis;

import org.codeqinvest.sonar.ResourcesCollectorService;
import org.codeqinvest.sonar.SonarConnectionSettings;
import org.sonar.wsclient.services.Resource;

import java.util.Collection;
import java.util.LinkedList;

class FakeResourcesCollectorService extends ResourcesCollectorService {

  private final Collection<Resource> resources = new LinkedList<Resource>();

  public void addResource(String key) {
    Resource resource = new Resource();
    resource.setKey(key);
    resource.setLongName(key);
    resources.add(resource);
  }

  @Override
  public Collection<Resource> collectAllResourcesForProject(SonarConnectionSettings connectionSettings) {
    return resources;
  }
}
