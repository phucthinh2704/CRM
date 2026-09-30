package com.example.crm.logic;

import com.example.crm.entity.CrmOutboxEvent;
import java.util.List;

public interface OutboxRelayLogic {
    List<CrmOutboxEvent> claimAndFetchEvents(String leaseOwner, int batchSize);
    void markEventAsSent(Long id);
    void markEventAsFailed(CrmOutboxEvent event, String errorCode);
}