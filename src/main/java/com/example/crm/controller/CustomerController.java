package com.example.crm.controller;

import com.example.crm.dto.CustomerUpsertReq;
import com.example.crm.entity.Customer;
import com.example.crm.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerUpsertReq req) {
        return new ResponseEntity<>(customerService.createCustomer(req), HttpStatus.CREATED);
    }

    // API Duyệt khách hàng -> Kích hoạt đồng bộ sang RMS
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Customer> approve(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.approveCustomer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody CustomerUpsertReq req) {
        return ResponseEntity.ok(customerService.updateCustomer(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivate(@PathVariable Long id) {
        customerService.deactivateCustomer(id);
        return ResponseEntity.ok("Đã chuyển trạng thái khách hàng sang ngừng hoạt động.");
    }
}