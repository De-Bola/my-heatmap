CREATE TABLE APP_USER (
                      USER_ID UUID PRIMARY KEY,
                      USERNAME VARCHAR(255) NOT NULL UNIQUE,
                      PASSWORD VARCHAR(255) NOT NULL
);

CREATE TABLE CALL_LOG (
                          ID UUID PRIMARY KEY,
                          USER_ID UUID NOT NULL,
                          USERNAME VARCHAR(255) NOT NULL,
                          ONOFF_NUMBER VARCHAR(45) NOT NULL,
                          CONTACT_NUMBER VARCHAR(45) NOT NULL,
                          STATUS VARCHAR(45) NOT NULL,
                          INCOMING BOOLEAN NOT NULL,
                          DURATION INT NOT NULL,
                          STARTED_AT TIMESTAMP(3) NOT NULL,
                          ENDED_AT TIMESTAMP(3) NOT NULL,
                          FOREIGN KEY (USER_ID) REFERENCES APP_USER(USER_ID)
);

CREATE INDEX idx_user_id ON CALL_LOG(USER_ID);
CREATE INDEX idx_status ON CALL_LOG(STATUS);
CREATE INDEX idx_started_at ON CALL_LOG(STARTED_AT);

-- Sample data for 3 days (ASSUMING UUID is generated manually or by app layer)
INSERT INTO APP_USER (USER_ID, USERNAME, PASSWORD) VALUES
                                                   ('11111111-1111-1111-1111-111111111111', 'alice', 'password123'),
                                                   ('22222222-2222-2222-2222-222222222222', 'bob', 'password123');

INSERT INTO CALL_LOG (ID, USER_ID, USERNAME, ONOFF_NUMBER, CONTACT_NUMBER, STATUS, INCOMING, DURATION, STARTED_AT, ENDED_AT) VALUES
-- Day 1
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'alice', '1001', '2001', 'ANSWER', TRUE, 120, '2024-04-10T09:15:00.000', '2024-04-10T09:17:00.000'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'bob', '1002', '2002', 'MISSED', TRUE, 0, '2024-04-10T10:30:00.000', '2024-04-10T10:30:00.000'),
-- Day 2
('cccccccc-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'alice', '1001', '2003', 'ANSWER', TRUE, 90, '2024-04-11T14:00:00.000', '2024-04-11T14:01:30.000'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'bob', '1002', '2004', 'ERROR', FALSE, 0, '2024-04-11T16:00:00.000', '2024-04-11T16:00:00.000'),
-- Day 3
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '11111111-1111-1111-1111-111111111111', 'alice', '1001', '2005', 'ANSWER', TRUE, 150, '2024-04-12T08:00:00.000', '2024-04-12T08:02:30.000'),
('ffffffff-ffff-ffff-ffff-ffffffffffff', '22222222-2222-2222-2222-222222222222', 'bob', '1002', '2006', 'MISSED', TRUE, 0, '2024-04-12T13:00:00.000', '2024-04-12T13:00:00.000');
