package com.example.crm.service;

import com.example.crm.dto.CustomerUpsertReq;
import com.example.crm.entity.Customer;

public interface CustomerService {
    Customer createCustomer(CustomerUpsertReq req);
    Customer approveCustomer(Long id);
    Customer updateCustomer(Long id, CustomerUpsertReq req);
    void deactivateCustomer(Long id);
}