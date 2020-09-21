INSERT INTO link (id, target_url, created_by, created_date, last_updated, is_private, access_counter, creation_counter)
  VALUES ('AZERTY', 'http://localhost:8080/api/link/AZERTY', 'JUNIT', TO_DATE('2018-01-01', 'yyyy-mm-dd'), TO_DATE('2018-01-01', 'yyyy-mm-dd'), false, 0, 1);
INSERT INTO link (id, target_url, created_by, created_date, last_updated, is_private, access_counter, creation_counter)
  VALUES ('1234156', 'http://localhost:8080/api/link/1234156', 'TEST', TO_DATE('2018-01-01', 'yyyy-mm-dd'), TO_DATE('2018-01-02', 'yyyy-mm-dd'), true, 2, 1);
INSERT INTO link (id, target_url, created_by, created_date, last_updated, is_private, access_counter, creation_counter)
  VALUES ('ABCDEF', 'https://github.com', 'JUNIT', TO_DATE('2018-10-10', 'yyyy-mm-dd'), TO_DATE('2018-11-11', 'yyyy-mm-dd'), false, 255, 4);
