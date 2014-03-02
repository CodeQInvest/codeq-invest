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
package org.codeqinvest.quality.repository;

import lombok.Data;

/**
 * This class encapsulate the basic information of a
 * {@link org.codeqinvest.quality.Project}. It's used in {@link ProjectRepository}
 * to prevent retrieving the whole project object graph which can be expensive.
 *
 * @author fmueller
 */
@Data
public class BasicProjectInformation {

  private final long id;
  private final String name;
}
