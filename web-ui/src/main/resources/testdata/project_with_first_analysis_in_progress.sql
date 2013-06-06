INSERT INTO PROJECT (ID, PROFILE_ID, NAME, LOWERCASENAME, CRONEXPRESSION, SONAR_URL,
                     SONAR_PROJECT, SONAR_USERNAME, SONAR_PASSWORD,
                     SCM_TYPE, SCM_URL, SCM_USERNAME, SCM_PASSWORD,
                     SCM_FILE_ENCODING, CODE_CHANGE_METHOD, CODE_CHANGE_DAYS, HADANALYSIS)
  VALUES (2, 1, 'Project With First Analysis In Progress', 'project with first analysis in progress', '* * 3 * * *',
          'http://localhost', 'sonarProject', NULL, NULL,
          0, 'http://svn.localhost', NULL, NULL, 'UTF-8',
          0, 21, TRUE);
