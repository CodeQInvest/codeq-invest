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
package org.codeqinvest.quality;

import org.codeqinvest.test.utils.AbstractDatabaseIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This test checks that the database configuration is working as expected
 * in this module.
 *
 * @author fmueller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/module-context.xml", "classpath:inmemory-db-context.xml"})
@Transactional
public class DatabaseConfigurationIntegrationTest extends AbstractDatabaseIntegrationTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private DummyEntityRepository repository;

  @Test
  public void persistWithEntityManager() {
    DummyEntity dummy = new DummyEntity("abc");
    entityManager.persist(dummy);
    Long id = dummy.getId();
    entityManager.flush();
    entityManager.clear();
    DummyEntity fromDb = entityManager.find(DummyEntity.class, id);
    assertThat(fromDb.getName()).isEqualTo("abc");
  }

  @Test
  public void persistWithRepository() {
    DummyEntity dummy = repository.save(new DummyEntity("abc123"));
    Long id = dummy.getId();
    entityManager.flush();
    entityManager.clear();
    DummyEntity fromDb = repository.findOne(id);
    assertThat(fromDb.getName()).isEqualTo("abc123");
  }
}
