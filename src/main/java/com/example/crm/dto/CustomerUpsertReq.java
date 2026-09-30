package com.example.crm.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerUpsertReq {
    @NotBlank(message = "Tên không được trống")
    private String name;
    @NotBlank @Email
    private String email;
    private String phone;
}