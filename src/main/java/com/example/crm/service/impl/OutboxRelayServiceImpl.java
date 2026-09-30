package com.example.crm.service.impl;

import com.example.crm.entity.CrmOutboxEvent;
import com.example.crm.logic.OutboxRelayLogic;
import com.example.crm.service.OutboxRelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxRelayServiceImpl implements OutboxRelayService {

    private final OutboxRelayLogic outboxRelayLogic;

    @Override
    @Transactional // Mở Transaction ngắn để UPDATE lease_owner và SELECT ra danh sách
    public List<CrmOutboxEvent> claimAndFetchEvents(String leaseOwner, int batchSize) {
        return outboxRelayLogic.claimAndFetchEvents(leaseOwner, batchSize);
    }

    @Override
    @Transactional // Mở Transaction ngắn để UPDATE status = 'SENT'
    public void markEventAsSent(Long id) {
        outboxRelayLogic.markEventAsSent(id);
    }

    @Override
    @Transactional // Mở Transaction ngắn để UPDATE status = 'PENDING' / 'DEAD'
    public void markEventAsFailed(CrmOutboxEvent event, String errorCode) {
        outboxRelayLogic.markEventAsFailed(event, errorCode);
    }
}