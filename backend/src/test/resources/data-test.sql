INSERT INTO link (id, target_url, created_by, created_date, last_updated, private, access_count)
  VALUES
         ('AZERTY', 'http://localhost:8080/api/link/AZERTY', 'JUNIT', TO_DATE('2018-01-01', 'yyyy-mm-dd'), TO_DATE('2018-01-01', 'yyyy-mm-dd'), false, 0),
         ('1234156', 'http://localhost:8080/api/link/1234156', 'JUNIT', TO_DATE('2018-01-01', 'yyyy-mm-dd'), TO_DATE('2018-01-02', 'yyyy-mm-dd'), true, 2),
         ('ABCDEF', 'http://github.com', 'TEST', TO_DATE('2018-10-10', 'yyyy-mm-dd'), TO_DATE('2018-11-11', 'yyyy-mm-dd'), false, 255),
         ;