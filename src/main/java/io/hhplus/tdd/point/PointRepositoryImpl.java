package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepository {

    UserPointTable userPointTable = new UserPointTable();
    PointHistoryTable pointHistoryTable = new PointHistoryTable();

    @Override
    public UserPoint selectById(long id) {

        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> selectAllByUserId(long id) {

        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {

        return userPointTable.insertOrUpdate(id, amount);
    }

    @Override
    public PointHistory insert(long id, long amount, TransactionType type, long updateMillis) {
        return pointHistoryTable.insert(id, amount, type, updateMillis);
    }
}
