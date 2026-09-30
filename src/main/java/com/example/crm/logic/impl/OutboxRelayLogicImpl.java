package com.example.crm.logic.impl;

import com.example.crm.dao.OutboxEventDao;
import com.example.crm.entity.CrmOutboxEvent;
import com.example.crm.logic.OutboxRelayLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxRelayLogicImpl implements OutboxRelayLogic {

    private final OutboxEventDao outboxEventDao;
    private static final int MAX_ATTEMPTS = 5;

    @Override
    public List<CrmOutboxEvent> claimAndFetchEvents(String leaseOwner, int batchSize) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime leaseUntil = now.plusSeconds(60); // Giữ lease trong 60 giây

        int claimedCount = outboxEventDao.claimDueEvents(leaseOwner, now, leaseUntil, batchSize);
        if (claimedCount == 0) {
            return Collections.emptyList();
        }
        return outboxEventDao.selectClaimedEvents(leaseOwner);
    }

    @Override
    public void markEventAsSent(Long id) {
        outboxEventDao.markAsSent(id, LocalDateTime.now());
    }

    @Override
    public void markEventAsFailed(CrmOutboxEvent event, String errorCode) {
        int nextAttemptCount = event.getAttemptCount() + 1;
        String newStatus = (nextAttemptCount >= MAX_ATTEMPTS) ? "DEAD" : "PENDING";
        LocalDateTime nextAttemptAt = LocalDateTime.now().plusSeconds(nextAttemptCount * 30L);

        outboxEventDao.markAsFailed(event.getId(), newStatus, nextAttemptAt, errorCode);
    }
}