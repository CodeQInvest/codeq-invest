INSERT INTO QUALITY_PROFILE (ID, NAME, LOWERCASENAME) VALUES (1, 'Default Profile', 'default profile');

INSERT INTO QUALITY_REQUIREMENT (PROFILE_ID, REMEDIATIONCOSTS, NONREMEDIATIONCOSTS, METRICIDENTIFIER, OPERATOR,
                                 THRESHOLD, WEIGHTINGMETRICVALUE, WEIGHTINGMETRICIDENTIFIER, AUTOMATICALLYFIXABLE)
  VALUES
  (1, 5, 10, 'function_complexity', '<=', 5, 100, 'ncloc', FALSE),
  (1, 3, 5, 'public_documented_api_density', '>=', 50, 100, 'ncloc', FALSE),
  (1, 1, 2, 'comment_lines_density', '>=', 10, 100, 'ncloc', FALSE),
  (1, 10, 30, 'coverage', '>', 80, 100, 'ncloc', FALSE),
  (1, 20, 60, 'lcom4', '=', 1, 100, 'ncloc', FALSE),
  (1, 5, 15, 'duplicated_lines_density', '=', 0, 100, 'ncloc', FALSE),
  (1, 30, 45, 'dit', '<', 5, 100, 'ncloc', FALSE),
  (1, 20, 30, 'rfc', '<=', 0, 100, 'ncloc', FALSE);

INSERT INTO CHANGE_RISK_FUNCTION (ID, PROFILE_ID, METRICIDENTIFIER) VALUES (1, 1, 'coverage');
INSERT INTO RISK_CHARGE (RISK_FUNCTION_ID, OPERATOR, THRESHOLD, AMOUNT)
  VALUES
  (1, '<', 80, 0.02),
  (1, '<', 60, 0.04),
  (1, '<', 40, 0.1),
  (1, '<', 20, 0.3),
  (1, '<', 10, 0.5);

INSERT INTO CHANGE_RISK_FUNCTION (ID, PROFILE_ID, METRICIDENTIFIER) VALUES (2, 1, 'function_complexity');
INSERT INTO RISK_CHARGE (RISK_FUNCTION_ID, OPERATOR, THRESHOLD, AMOUNT)
  VALUES
  (2, '>', 50, 0.5),
  (2, '>', 20, 0.3),
  (2, '>', 10, 0.1),
  (2, '>', 5, 0.03);
