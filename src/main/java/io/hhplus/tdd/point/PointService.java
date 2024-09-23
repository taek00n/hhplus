package io.hhplus.tdd.point;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PointService {

    private final PointRepository pointRepository;
    private final Long MAX_POINT = 10000L;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public UserPoint point(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("정수의 아이디만 사용해주세요.");
        }
        return pointRepository.selectById(id);
    }

    public List<PointHistory> history(long id) {

        return pointRepository.selectAllByUserId(id);
    }

    public UserPoint charge(long id, long amount) {

        changedPoint(id, amount, TransactionType.CHARGE);
        return pointRepository.selectById(id);
    }

    public UserPoint use(long id, long amount) {

        changedPoint(id, amount, TransactionType.USE);
        return pointRepository.selectById(id);
    }

    // 포인트의 충전과 사용 처리 로직
    private void changedPoint(long id, long amount, TransactionType type) {

        if (amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 됩니다.");
        }

        UserPoint userPoint = pointRepository.selectById(id);
        long point = userPoint.point();

        if (type == TransactionType.CHARGE) {
            point += amount;
            if (point > MAX_POINT) {
                throw new IllegalArgumentException("최대 적립 포인트를 초과하였습니다.");
            }
            pointRepository.insertOrUpdate(id, point);
        }
        else if (type == TransactionType.USE) {
            point -= amount;
            if (point < 0) {
                throw new IllegalArgumentException("포인트가 부족합니다.");
            }
            pointRepository.insertOrUpdate(id, point);
        }

        pointRepository.insert(id, amount, type, System.currentTimeMillis());
    }
}
