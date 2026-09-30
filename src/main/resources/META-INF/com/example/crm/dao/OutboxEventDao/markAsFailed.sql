UPDATE crm_outbox_event
SET 
    status = /* newStatus */'PENDING',
    attempt_count = attempt_count + 1,
    next_attempt_at = /* nextAttemptAt */'2026-09-30 10:05:00',
    last_error = /* lastError */'BROKER_TIMEOUT',
    lease_owner = NULL,
    lease_until = NULL
WHERE id = /* id */1