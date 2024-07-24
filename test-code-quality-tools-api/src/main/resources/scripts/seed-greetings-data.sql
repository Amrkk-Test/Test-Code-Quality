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