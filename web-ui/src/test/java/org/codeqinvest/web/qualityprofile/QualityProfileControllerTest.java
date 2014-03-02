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
package org.codeqinvest.web.qualityprofile;

import org.codeqinvest.quality.QualityProfile;
import org.codeqinvest.quality.repository.QualityProfileRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QualityProfileControllerTest {

  private MockMvc mockMvc;

  private QualityProfileRepository profileRepository;

  @Before
  public void setUp() {
    profileRepository = mock(QualityProfileRepository.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new QualityProfileController(profileRepository)).build();
  }

  @Test
  public void persistNewQualityProfile() throws Exception {
    mockMvc.perform(post("/qualityprofiles/create")
        .param("name", "my-profile"))
        .andExpect(status().isOk());
    verify(profileRepository).save(eq(new QualityProfile("my-profile")));
  }
}
