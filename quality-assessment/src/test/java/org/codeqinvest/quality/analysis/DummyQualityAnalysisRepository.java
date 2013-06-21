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

import org.codeqinvest.quality.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class DummyQualityAnalysisRepository implements QualityAnalysisRepository {

  @Override
  public <S extends QualityAnalysis> S save(S entity) {
    return entity;
  }

  @Override
  public QualityAnalysis findOne(Long id) {
    return null;
  }

  @Override
  public boolean exists(Long id) {
    return false;
  }

  @Override
  public List<QualityAnalysis> findAll() {
    return null;
  }

  @Override
  public Iterable<QualityAnalysis> findAll(Iterable<Long> ids) {
    return null;
  }

  @Override
  public long count() {
    return 0;
  }

  @Override
  public void delete(Long aLong) {
  }

  @Override
  public void delete(QualityAnalysis entity) {
  }

  @Override
  public void delete(Iterable<? extends QualityAnalysis> entities) {
  }

  @Override
  public void deleteAll() {
  }

  @Override
  public List<QualityAnalysis> findAll(Sort sort) {
    return null;
  }

  @Override
  public Page<QualityAnalysis> findAll(Pageable pageable) {
    return null;
  }

  @Override
  public void flush() {
  }

  @Override
  public QualityAnalysis saveAndFlush(QualityAnalysis entity) {
    return null;
  }

  @Override
  public void deleteInBatch(Iterable<QualityAnalysis> entities) {
  }

  @Override
  public void deleteAllInBatch() {
  }

  @Override
  public <S extends QualityAnalysis> List<S> save(Iterable<S> entities) {
    return null;
  }

  @Override
  public List<QualityAnalysis> findByProjectAndSuccessfulOrderByCreatedDesc(Project project, boolean successful) {
    return null;
  }

  @Override
  public QualityAnalysis findOneByIdWithViolations(Long id) {
    return null;
  }
}
