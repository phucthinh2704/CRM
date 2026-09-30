UPDATE crm_outbox_event
SET 
    status = 'PROCESSING',
    lease_owner = /* leaseOwner */'uuid',
    lease_until = /* leaseUntil */'2026-09-30 10:06:00'
WHERE 
    (status = 'PENDING' AND next_attempt_at <= /* now */'2026-09-30 10:00:00')
    OR 
    (status = 'PROCESSING' AND lease_until <= /* now */'2026-09-30 10:00:00')
ORDER BY id ASC
LIMIT /* batchSize */50