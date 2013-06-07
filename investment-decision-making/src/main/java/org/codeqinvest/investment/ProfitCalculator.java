package org.codeqinvest.investment;

import lombok.extern.slf4j.Slf4j;
import org.codeqinvest.quality.QualityViolation;
import org.springframework.stereotype.Component;

/**
 * This is the main component for calculating profits of {@link QualityViolation}.
 *
 * @author fmueller
 */
@Slf4j
@Component
public class ProfitCalculator {

  public double calculateProfit(QualityViolation violation) {
    double nonRemediationCosts = violation.getNonRemediationCosts() * violation.getArtefact().getChangeProbability();
    return violation.getRequirement().isAutomaticallyFixable()
        ? nonRemediationCosts - violation.getRemediationCosts()
        : nonRemediationCosts - violation.getRemediationCosts() * violation.getArtefact().getSecureChangeProbability();
  }
}
