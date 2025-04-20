CREATE TABLE call_log
(
    id             UUID         NOT NULL,
    user_id        VARCHAR(45)  NOT NULL,
    username       VARCHAR(255) NOT NULL,
    onoff_number   VARCHAR(45)  NOT NULL,
    contact_number VARCHAR(45)  NOT NULL,
    status         VARCHAR(45)  NOT NULL,
    incoming       BOOLEAN      NOT NULL,
    duration       INT          NOT NULL,
    started_at     TIMESTAMP(3) NOT NULL,
    ended_at       TIMESTAMP(3) NOT NULL,
    CONSTRAINT pk_call_log PRIMARY KEY (id)
);

CREATE INDEX idx_started_at ON call_log (started_at);

CREATE INDEX idx_status ON call_log (status);

CREATE INDEX idx_user_id ON call_log (user_id);