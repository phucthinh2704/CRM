package com.example.crm.dao;
import com.example.crm.entity.Customer;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import java.util.Optional;

@Dao
@ConfigAutowireable
public interface CustomerDao {
    @Select
    Optional<Customer> selectById(Long id);
    @Insert
    int insert(Customer customer);
    @Update
    int update(Customer customer);
}