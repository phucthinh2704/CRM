package com.example.crm.entity;

import lombok.Data;
import org.seasar.doma.*;
import org.seasar.doma.jdbc.entity.NamingType;
import java.time.LocalDateTime;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String email;
    public String phone;
    public Boolean active;
    public Boolean isApproved;  
    public Long version;
    public LocalDateTime updatedAt;
}