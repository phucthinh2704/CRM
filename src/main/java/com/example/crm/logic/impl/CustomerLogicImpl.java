package com.example.crm.logic.impl;

import com.example.crm.dao.CustomerDao;
import com.example.crm.dao.OutboxEventDao;
import com.example.crm.dto.CustomerPayloadDto;
import com.example.crm.dto.CustomerUpsertReq;
import com.example.crm.entity.CrmOutboxEvent;
import com.example.crm.entity.Customer;
import com.example.crm.exception.BusinessException;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.logic.CustomerLogic;
import com.example.crm.mapper.CustomerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerLogicImpl implements CustomerLogic {

    private final CustomerDao customerDao;
    private final OutboxEventDao outboxEventDao;
    private final CustomerMapper customerMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Customer createCustomer(CustomerUpsertReq req) {
        // 1. Tạo mới ở trạng thái chưa duyệt (isApproved = false)
        Customer customer = customerMapper.toNewEntity(req);
        customerDao.insert(customer);

        // LƯU Ý: KHÔNG gọi saveOutboxEvent() ở đây vì chưa được duyệt!
        return customer;
    }

    @Override
    public Customer approveCustomer(Long id) {
        Customer customer = customerDao.selectById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng có ID: " + id));

        if (Boolean.TRUE.equals(customer.getIsApproved())) {
            throw new BusinessException("Khách hàng ID " + id + " đã được phê duyệt từ trước.");
        }
        if (Boolean.FALSE.equals(customer.getActive())) {
            throw new BusinessException("Không thể phê duyệt khách hàng đã ngừng hoạt động.");
        }

        LocalDateTime now = LocalDateTime.now();
        customer.setIsApproved(true);
        customer.setVersion(customer.getVersion() + 1); // Tăng version đồng bộ
        customer.setUpdatedAt(now);

        customerDao.update(customer);

        // Khi được duyệt -> Bắn event CUSTOMER_UPSERTED để tạo bản sao bên RMS
        saveOutboxEvent(customer, "CUSTOMER_UPSERTED", now);
        return customer;
    }

    @Override
    public Customer updateCustomer(Long id, CustomerUpsertReq req) {
        Customer customer = customerDao.selectById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng có ID: " + id));

        customerMapper.updateEntityFromReq(req, customer);

        // Chỉ tăng version đồng bộ và bắn Outbox Event NẾU khách hàng ĐÃ ĐƯỢC DUYỆT
        if (Boolean.TRUE.equals(customer.getIsApproved())) {
            customer.setVersion(customer.getVersion() + 1);
            customerDao.update(customer);
            saveOutboxEvent(customer, "CUSTOMER_UPSERTED", customer.getUpdatedAt());
        } else {
            // Nếu chưa duyệt, chỉ lưu nội bộ bên CRM, không gửi sang RMS
            customerDao.update(customer);
        }

        return customer;
    }

    @Override
    public void deactivateCustomer(Long id) {
        Customer customer = customerDao.selectById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng có ID: " + id));

        LocalDateTime now = LocalDateTime.now();
        customer.setActive(false);
        customer.setUpdatedAt(now);

        // Chỉ bắn event CUSTOMER_DEACTIVATED sang RMS nếu trước đó khách hàng đã được duyệt (đã có mặt bên RMS)
        if (Boolean.TRUE.equals(customer.getIsApproved())) {
            customer.setVersion(customer.getVersion() + 1);
            customerDao.update(customer);
            saveOutboxEvent(customer, "CUSTOMER_DEACTIVATED", now);
        } else {
            customerDao.update(customer);
        }
    }

    @SneakyThrows
    private void saveOutboxEvent(Customer customer, String eventType, LocalDateTime now) {
        CustomerPayloadDto payloadDto = customerMapper.toPayloadDto(customer);

        CrmOutboxEvent event = new CrmOutboxEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setAggregateType("CUSTOMER");
        event.setAggregateId(String.valueOf(customer.getId()));
        event.setAggregateVersion(customer.getVersion());
        event.setEventType(eventType);
        event.setSchemaVersion(1);
        event.setPayload(objectMapper.writeValueAsString(payloadDto));
        event.setStatus("PENDING");
        event.setAttemptCount(0);
        event.setNextAttemptAt(now);
        event.setCreatedAt(now);

        outboxEventDao.insert(event);
    }
}