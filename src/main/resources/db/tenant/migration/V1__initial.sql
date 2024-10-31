CREATE TABLE persons
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    version    BIGINT                NULL,
    first_name VARCHAR(255)          NULL,
    last_name  VARCHAR(255)          NULL,
    CONSTRAINT pk_persons PRIMARY KEY (id)
);