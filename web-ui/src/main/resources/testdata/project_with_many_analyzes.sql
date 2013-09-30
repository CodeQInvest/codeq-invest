INSERT INTO PROJECT (ID, PROFILE_ID, NAME, LOWERCASENAME, CRONEXPRESSION, SONAR_URL,
                     SONAR_PROJECT, SONAR_USERNAME, SONAR_PASSWORD,
                     SCM_TYPE, SCM_URL, SCM_USERNAME, SCM_PASSWORD,
                     SCM_FILE_ENCODING, CODE_CHANGE_METHOD, CODE_CHANGE_DAYS, HADANALYSIS)
  VALUES (3, 1, 'Project With Many Analyzes', 'project with many analyzes', '* * 3 * * *',
          'http://localhost', 'sonarProject', NULL, NULL,
          0, 'http://svn.localhost', NULL, NULL, 'UTF-8',
          0, 21, FALSE);

INSERT INTO QUALITY_ANALYSIS (ID, PROJECT_ID, SUCCESSFUL, CREATED) VALUES (1, 3, TRUE, TIMESTAMP '2013-06-04 20:00:00.000+02:00');
INSERT INTO QUALITY_ANALYSIS (ID, PROJECT_ID, SUCCESSFUL, CREATED) VALUES (2, 3, TRUE, TIMESTAMP '2013-06-05 20:00:00.000+02:00');
INSERT INTO QUALITY_ANALYSIS (ID, PROJECT_ID, SUCCESSFUL, CREATED) VALUES (3, 3, TRUE, TIMESTAMP '2013-06-06 20:00:00.000+02:00');

INSERT INTO ARTEFACT (ID, NAME, SONARIDENTIFIER, CHANGEPROBABILITY, SECURECHANGEPROBABILITY)
  VALUES
  (1, 'project.web.AbstractController', 'DUMMY', 0.12, 1.03),
  (2, 'project.web.WebConfiguration', 'DUMMY', 0.32, 1.02),
  (3, 'project.web.user.UserController', 'DUMMY', 0.78, 1.04),
  (4, 'project.web.user.AdminController', 'DUMMY', 0.63, 1.04),
  (5, 'project.web.user.User', 'DUMMY', 0.2, 1.00),
  (6, 'project.web.user.Admin', 'DUMMY', 0.1, 1.00),
  (7, 'project.web.user.Role', 'DUMMY', 0.03, 1.00),
  (8, 'project.web.payment.controller.BankController', 'DUMMY', 0.89, 1.1),
  (9, 'project.web.payment.controller.PaymentController', 'DUMMY', 0.91, 1.13),
  (10, 'project.web.payment.Bank', 'DUMMY', 0.43, 1.03),
  (11, 'project.web.payment.Payment', 'DUMMY', 0.65, 1.03),
  (12, 'project.validation.UserValidator', 'DUMMY', 0.3, 1.04),
  (13, 'project.validation.AdminValidator', 'DUMMY', 0.23, 1.00),
  (14, 'project.validation.BankValidator', 'DUMMY', 0.41, 1.1),
  (15, 'project.validation.PaymentValidator', 'DUMMY', 0.73, 1.04);

INSERT INTO QUALITY_VIOLATION (ANALYSIS_ID, REQUIREMENT_ID, ARTEFACT_ID, REMEDIATIONCOSTS, NONREMEDIATIONCOSTS, WEIGHTINGMETRICVALUE, WEIGHTINGMETRICIDENTIFIER)
  VALUES
  (3, 1, 1, 20, 40, 100, 'ncloc'),
  (3, 4, 2, 20, 40, 50, 'ncloc'),
  (3, 2, 2, 6, 10, 50, 'ncloc'),
  (3, 4, 3, 480, 1440, 200, 'ncloc'),
  (3, 6, 3, 110, 330, 200, 'ncloc'),
  (3, 5, 3, 40, 120, 200, 'ncloc'),
  (3, 4, 4, 240, 960, 80, 'ncloc'),
  (3, 5, 5, 30, 90, 60, 'ncloc'),
  (3, 3, 5, 4, 8, 60, 'ncloc'),
  (3, 2, 6, 3, 5, 40, 'ncloc'),
  (3, 2, 7, 2, 4, 20, 'ncloc'),
  (3, 4, 8, 200, 800, 240, 'ncloc'),
  (3, 2, 8, 60, 100, 240, 'ncloc'),
  (3, 6, 8, 50, 150, 240, 'ncloc'),
  (3, 5, 8, 40, 120, 240, 'ncloc'),
  (3, 1, 9, 30, 60, 310, 'ncloc'),
  (3, 4, 9, 600, 1800, 310, 'ncloc'),
  (3, 1, 10, 5, 10, 80, 'ncloc'),
  (3, 2, 10, 75, 125, 80, 'ncloc'),
  (3, 1, 11, 5, 10, 70, 'ncloc'),
  (3, 2, 11, 30, 50, 70, 'ncloc'),
  (3, 4, 12, 300, 900, 140, 'ncloc'),
  (3, 6, 13, 50, 150, 90, 'ncloc'),
  (3, 4, 14, 400, 1200, 180, 'ncloc'),
  (3, 4, 15, 195, 585, 280, 'ncloc');
