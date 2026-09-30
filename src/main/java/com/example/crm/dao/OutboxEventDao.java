package com.example.crm.dao;

import com.example.crm.entity.CrmOutboxEvent;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import java.time.LocalDateTime;
import java.util.List;

@Dao
@ConfigAutowireable
public interface OutboxEventDao {
    @Insert
    int insert(CrmOutboxEvent event);

    // 1. Khóa (Claim) các bản ghi đủ điều kiện gửi để gán lease_owner
    @Update(sqlFile = true)
    int claimDueEvents(String leaseOwner, LocalDateTime now, LocalDateTime leaseUntil, int batchSize);

    // 2. Lấy danh sách các bản ghi đã được khóa bởi leaseOwner hiện tại
    @Select
    List<CrmOutboxEvent> selectClaimedEvents(String leaseOwner);

    // 3. Đánh dấu gửi thành công (SENT)
    @Update(sqlFile = true)
    int markAsSent(Long id, LocalDateTime publishedAt);

    // 4. Đánh dấu lỗi (Tăng attempt_count, trả về PENDING hoặc DEAD)
    @Update(sqlFile = true)
    int markAsFailed(Long id, String newStatus, LocalDateTime nextAttemptAt, String lastError);
}