package com.example.crm.relay;

import com.example.crm.config.RabbitConfig;
import com.example.crm.entity.CrmOutboxEvent;
import com.example.crm.service.OutboxRelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final OutboxRelayService outboxRelayService;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 2000)
    public void processOutboxEvents() {
        String leaseOwner = UUID.randomUUID().toString();

        // 1. Gọi Service để khóa và lấy danh sách (Transaction DB đóng ngay sau dòng
        // này)
        List<CrmOutboxEvent> events = outboxRelayService.claimAndFetchEvents(leaseOwner, 50);
        if (events.isEmpty())
            return;

        // 2. Gửi mạng sang RabbitMQ (Không giữ kết nối DB trong lúc gửi mạng)
        for (CrmOutboxEvent event : events) {
            publishToRabbit(event);
        }
    }

    private void publishToRabbit(CrmOutboxEvent event) {
        try {
            Message message = MessageBuilder
                    .withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setMessageId(event.getEventId())
                    .setHeader("event_id", event.getEventId())
                    .setHeader("aggregate_type", event.getAggregateType())
                    .setHeader("aggregate_id", event.getAggregateId())
                    .setHeader("aggregate_version", event.getAggregateVersion())
                    .setHeader("event_type", event.getEventType())
                    .setHeader("schema_version", event.getSchemaVersion())
                    .build();

            CorrelationData correlationData = new CorrelationData(event.getEventId());
            String routingKey = "crm.customer." + event.getEventType().toLowerCase();

            rabbitTemplate.send(RabbitConfig.EXCHANGE_NAME, routingKey, message, correlationData);
            CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);

            if (confirm != null && confirm.isAck()) {
                outboxRelayService.markEventAsSent(event.getId());
                log.info("Đã gửi thành công event_id={} lên RabbitMQ", event.getEventId());
            } else {
                outboxRelayService.markEventAsFailed(event, "BROKER_NACK");
            }
        } catch (Exception ex) {
            log.error("Lỗi khi gửi event_id={}", event.getEventId(), ex);
            outboxRelayService.markEventAsFailed(event, "BROKER_TIMEOUT_OR_ERROR");
        }
    }
}