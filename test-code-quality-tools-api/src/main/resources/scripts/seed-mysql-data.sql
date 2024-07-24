DROP TABLE IF EXISTS test_code_quality_tools_api_key;

CREATE TABLE test_code_quality_tools_api_key(
                                 id VARCHAR(1024) PRIMARY KEY,
                                 test_code_quality_tools_api_key VARCHAR(1024) NOT NULL
);

INSERT INTO test_code_quality_tools_api_key VALUES ('1', 'myHash');
INSERT INTO test_code_quality_tools_api_key VALUES ('1884', 'myHash');

DROP TABLE IF EXISTS greeting;

CREATE TABLE greeting
(
  id      BIGINT PRIMARY KEY,
  message VARCHAR(1024) NOT NULL
);

INSERT INTO greeting
VALUES (123, 'some greeting');
INSERT INTO greeting
VALUES (321, 'another greeting');