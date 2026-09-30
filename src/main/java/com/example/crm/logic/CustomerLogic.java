package com.example.crm.logic;

import com.example.crm.dto.CustomerUpsertReq;
import com.example.crm.entity.Customer;

public interface CustomerLogic {
    Customer createCustomer(CustomerUpsertReq req);
    Customer approveCustomer(Long id); // Hàm duyệt khách hàng mới thêm
    Customer updateCustomer(Long id, CustomerUpsertReq req);
    void deactivateCustomer(Long id);
}