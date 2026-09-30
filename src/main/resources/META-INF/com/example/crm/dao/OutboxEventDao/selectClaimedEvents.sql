SELECT * FROM crm_outbox_event
WHERE status = 'PROCESSING' AND lease_owner = /* leaseOwner */'uuid'
ORDER BY id ASC