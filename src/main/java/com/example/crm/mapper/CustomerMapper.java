package com.example.crm.mapper;

import com.example.crm.dto.CustomerPayloadDto;
import com.example.crm.dto.CustomerUpsertReq;
import com.example.crm.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "isApproved", constant = "false") // Mặc định chưa được duyệt
    @Mapping(target = "version", constant = "1L")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Customer toNewEntity(CustomerUpsertReq req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "isApproved", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromReq(CustomerUpsertReq req, @MappingTarget Customer entity);

    @Mapping(target = "crmCustomerId", expression = "java(String.valueOf(customer.getId()))")
    CustomerPayloadDto toPayloadDto(Customer customer);
}