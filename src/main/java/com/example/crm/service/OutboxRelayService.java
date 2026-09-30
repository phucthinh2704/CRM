package com.example.crm.service;

import com.example.crm.entity.CrmOutboxEvent;
import java.util.List;

public interface OutboxRelayService {
    List<CrmOutboxEvent> claimAndFetchEvents(String leaseOwner, int batchSize);
    void markEventAsSent(Long id);
    void markEventAsFailed(CrmOutboxEvent event, String errorCode);
}