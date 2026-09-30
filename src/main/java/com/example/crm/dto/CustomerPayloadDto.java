package com.example.crm.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPayloadDto {
    private String crmCustomerId;
    private String name;
    private String email;
    private String phone;
    private Boolean active;
}