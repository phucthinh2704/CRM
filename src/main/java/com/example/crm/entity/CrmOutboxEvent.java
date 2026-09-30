package com.example.crm.entity;

import lombok.Data;
import org.seasar.doma.*;
import org.seasar.doma.jdbc.entity.NamingType;
import java.time.LocalDateTime;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Table(name = "crm_outbox_event")
public class CrmOutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String eventId;
    public String aggregateType;
    public String aggregateId;
    public Long aggregateVersion;
    public String eventType;
    public Integer schemaVersion;
    public String payload;
    public String status;
    public Integer attemptCount;
    public LocalDateTime nextAttemptAt;
    public String leaseOwner;
    public LocalDateTime leaseUntil;
    public LocalDateTime createdAt;
    public LocalDateTime publishedAt;
    public String lastError;
}