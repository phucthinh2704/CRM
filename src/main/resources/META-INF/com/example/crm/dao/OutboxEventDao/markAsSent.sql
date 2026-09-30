UPDATE crm_outbox_event
SET 
    status = 'SENT',
    published_at = /* publishedAt */'2026-09-30 10:00:02',
    lease_owner = NULL,
    lease_until = NULL
WHERE id = /* id */1