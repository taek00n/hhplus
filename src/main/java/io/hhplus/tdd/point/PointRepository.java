package io.hhplus.tdd.point;

import java.util.List;

public interface PointRepository {

    UserPoint selectById(long id);
    List<PointHistory> selectAllByUserId(long id);
    UserPoint insertOrUpdate(long id, long amount);
    PointHistory insert(long id, long amount, TransactionType type, long updateMillis);
}
