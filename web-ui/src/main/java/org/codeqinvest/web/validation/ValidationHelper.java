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
package org.codeqinvest.web.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * This helper class offer some convenient methods
 * for validating data.
 *
 * @author fmueller
 */
public final class ValidationHelper {

  private static final String FIELD_REQUIRED = "field.required";

  private ValidationHelper() {
  }

  /**
   * Validates if a given field is not empty and uses
   * default error message key when violated.
   */
  public static void rejectIfEmpty(Errors errors, String field) {
    ValidationUtils.rejectIfEmpty(errors, field, FIELD_REQUIRED);
  }

  /**
   * Validates if a given field is not empty or blank and uses
   * default error message key when violated.
   */
  public static void rejectIfEmptyOrWhitespace(Errors errors, String field) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, FIELD_REQUIRED);
  }
}
