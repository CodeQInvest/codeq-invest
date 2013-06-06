INSERT INTO PROJECT (ID, PROFILE_ID, NAME, LOWERCASENAME, CRONEXPRESSION, SONAR_URL,
                     SONAR_PROJECT, SONAR_USERNAME, SONAR_PASSWORD,
                     SCM_TYPE, SCM_URL, SCM_USERNAME, SCM_PASSWORD,
                     SCM_FILE_ENCODING, CODE_CHANGE_METHOD, CODE_CHANGE_DAYS, HADANALYSIS)
  VALUES (3, 1, 'Project With Many Analysis', 'project with many analysis', '* * 3 * * *',
          'http://localhost', 'sonarProject', NULL, NULL,
          0, 'http://svn.localhost', NULL, NULL, 'UTF-8',
          0, 21, FALSE);

INSERT INTO QUALITY_ANALYSIS (ID, PROJECT_ID, SUCCESSFUL) VALUES (1, 3, TRUE);
INSERT INTO QUALITY_ANALYSIS (ID, PROJECT_ID, SUCCESSFUL) VALUES (2, 3, TRUE);
INSERT INTO QUALITY_ANALYSIS (ID, PROJECT_ID, SUCCESSFUL) VALUES (3, 3, TRUE);

INSERT INTO ARTEFACT (ID, NAME, SONARIDENTIFIER, CHANGEPROBABILITY, SECURECHANGEPROBABILITY)
  VALUES
  (1, 'project.web.AbstractController', 'DUMMY', 0.12, 1.00),
  (2, 'project.web.WebConfiguration', 'DUMMY', 0.32, 1.00),
  (3, 'project.web.user.UserController', 'DUMMY', 0.78, 1.00),
  (4, 'project.web.user.AdminController', 'DUMMY', 0.63, 1.00),
  (5, 'project.web.user.User', 'DUMMY', 0.2, 1.00),
  (6, 'project.web.user.Admin', 'DUMMY', 0.1, 1.00),
  (7, 'project.web.user.Role', 'DUMMY', 0.03, 1.00),
  (8, 'project.web.payment.BankController', 'DUMMY', 0.89, 1.00),
  (9, 'project.web.payment.PaymentController', 'DUMMY', 0.91, 1.00),
  (10, 'project.web.payment.Bank', 'DUMMY', 0.43, 1.00),
  (11, 'project.web.payment.Payment', 'DUMMY', 0.65, 1.00),
  (12, 'project.validation.UserValidator', 'DUMMY', 0.3, 1.00),
  (13, 'project.validation.AdminValidator', 'DUMMY', 0.23, 1.00),
  (14, 'project.validation.BankValidator', 'DUMMY', 0.41, 1.00),
  (15, 'project.validation.PaymentValidator', 'DUMMY', 0.73, 1.00);

INSERT INTO QUALITY_VIOLATION (ANALYSIS_ID, REQUIREMENT_ID, VIOLATION_ID, REMEDIATIONCOSTS, NONREMEDIATIONCOSTS)
  VALUES
  (3, 1, 1, 20, 40),
  (3, 4, 2, 20, 40),
  (3, 2, 2, 6, 10),
  (3, 4, 3, 480, 1440),
  (3, 6, 3, 110, 330),
  (3, 5, 3, 40, 120),
  (3, 4, 4, 240, 960),
  (3, 5, 5, 30, 90),
  (3, 3, 5, 4, 8),
  (3, 2, 6, 3, 5),
  (3, 2, 7, 2, 4),
  (3, 4, 8, 200, 800),
  (3, 2, 8, 60, 100),
  (3, 6, 8, 50, 150),
  (3, 5, 8, 40, 120),
  (3, 1, 9, 30, 60),
  (3, 4, 9, 600, 1800),
  (3, 1, 10, 5, 10),
  (3, 2, 10, 75, 125),
  (3, 1, 11, 5, 10),
  (3, 2, 11, 30, 50),
  (3, 4, 12, 300, 900),
  (3, 6, 13, 50, 150),
  (3, 4, 14, 400, 1200),
  (3, 4, 15, 195, 585);
