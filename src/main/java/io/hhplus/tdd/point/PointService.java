package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public UserPoint point(long id) {

        return pointRepository.selectById(id);
    }

    public List<PointHistory> history(long id) {

        return pointRepository.selectAllByUserId(id);
    }

    public UserPoint charge(long id, long amount) {

        if (amount < 0) {
            // 금액은 0 이상
        }

        UserPoint userPoint = pointRepository.selectById(id);
        long newPoint = userPoint.point() + amount;

        pointRepository.insertOrUpdate(id, newPoint);
        pointRepository.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return pointRepository.selectById(id);
    }
}
