package org.codeqinvest.investment;

import org.codeqinvest.quality.Artefact;
import org.codeqinvest.quality.QualityRequirement;
import org.codeqinvest.quality.QualityViolation;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProfitCalculatorTest {

  private ProfitCalculator calculator;

  private Artefact artefact;
  private QualityRequirement requirement;
  private QualityViolation violation;

  @Before
  public void setUp() {
    calculator = new ProfitCalculator();
    artefact = mock(Artefact.class);
    when(artefact.getChangeProbability()).thenReturn(0.6);
    requirement = mock(QualityRequirement.class);
    violation = new QualityViolation(artefact, requirement, 10, 20);
  }

  @Test
  public void profitForAutomaticFixableViolation() {
    when(requirement.isAutomaticallyFixable()).thenReturn(true);
    assertThat(calculator.calculateProfit(violation)).isEqualTo(2.0);
  }

  @Test
  public void profitForNotAutomaticFixableViolation() {
    when(requirement.isAutomaticallyFixable()).thenReturn(false);
    when(artefact.getSecureChangeProbability()).thenReturn(1.1);
    assertThat(calculator.calculateProfit(violation)).isEqualTo(1.0);
  }
}
