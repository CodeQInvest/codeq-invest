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
package org.codeqinvest;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a helper class for loading some test data
 * into the database when the application is started with
 * test profile.
 *
 * @author fmueller
 */
@Slf4j
@Setter
class TestDataLoaderApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

  private DataSource dataSource;
  private Set<String> scripts;

  private final AtomicBoolean alreadyExecuted = new AtomicBoolean(false);

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (alreadyExecuted.compareAndSet(false, true)) {
      log.info("Loading test data into database...");
      Connection connection = null;
      try {
        ResourceDatabasePopulator resourceDatabasePopulator = createConfiguredResourceDatabasePopulator();
        try {
          connection = dataSource.getConnection();
          resourceDatabasePopulator.populate(connection);
        } catch (SQLException e) {
          log.error("Error while populating database with test data...", e);
          throw new RuntimeException(e);
        }
      } finally {
        if (connection != null) {
          try {
            connection.close();
          } catch (SQLException e) {
            log.error("Error while closing connection!", e);
            throw new RuntimeException(e);
          }
        }
      }
    } else {
      log.info("Already added test data to database.");
    }
  }

  private ResourceDatabasePopulator createConfiguredResourceDatabasePopulator() {
    ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
    for (String script : scripts) {
      log.info("Add script {} for loading into database", script);
      resourceDatabasePopulator.addScript(new ClassPathResource(script));
    }
    return resourceDatabasePopulator;
  }
}
