package io.hhplus.tdd.point;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class PointService {

    private final PointRepository pointRepository;
    private final Long MAX_POINT = 10000L;
    private final Queue<Request> requestQueue = new ConcurrentLinkedQueue<>();

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Scheduled(fixedDelay = 2000)
    public void actionRequest() {
        while (!requestQueue.isEmpty()) {
            Request request = requestQueue.poll();
            changedPoint(request);
        }
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

        requestQueue.offer(new Request(id, amount, TransactionType.CHARGE));
        return pointRepository.selectById(id);
    }

    public UserPoint use(long id, long amount) {

        requestQueue.offer(new Request(id, amount, TransactionType.USE));
        return pointRepository.selectById(id);
    }

    // 포인트의 충전과 사용 처리 로직
    private void changedPoint(Request request) {

        if (request.amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 됩니다.");
        }

        UserPoint userPoint = pointRepository.selectById(request.id);
        long point = userPoint.point();

        if (request.transactionType == TransactionType.CHARGE) {
            point += request.amount;
            if (point > MAX_POINT) {
                throw new IllegalArgumentException("최대 적립 포인트를 초과하였습니다.");
            }
            pointRepository.insertOrUpdate(request.id, point);
        }
        else if (request.transactionType == TransactionType.USE) {
            point -= request.amount;
            if (point < 0) {
                throw new IllegalArgumentException("포인트가 부족합니다.");
            }
            pointRepository.insertOrUpdate(request.id, point);
        }

        pointRepository.insert(request.id, request.amount, request.transactionType, System.currentTimeMillis());
    }
}
