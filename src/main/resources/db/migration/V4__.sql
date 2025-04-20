
ALTER TABLE CALL_LOG ADD COLUMN started_hour INT;

UPDATE CALL_LOG SET started_hour = EXTRACT(HOUR FROM started_at);

CREATE INDEX idx_hour_only ON CALL_LOG(started_hour);
