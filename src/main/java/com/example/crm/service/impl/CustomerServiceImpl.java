package com.example.crm.service.impl;

import com.example.crm.dto.CustomerUpsertReq;
import com.example.crm.entity.Customer;
import com.example.crm.logic.CustomerLogic;
import com.example.crm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerLogic customerLogic;

    @Override
    @Transactional
    public Customer createCustomer(CustomerUpsertReq req) {
        return customerLogic.createCustomer(req);
    }

    @Override
    @Transactional
    public Customer approveCustomer(Long id) {
        return customerLogic.approveCustomer(id);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, CustomerUpsertReq req) {
        return customerLogic.updateCustomer(id, req);
    }

    @Override
    @Transactional
    public void deactivateCustomer(Long id) {
        customerLogic.deactivateCustomer(id);
    }
}