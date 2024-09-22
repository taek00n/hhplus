package io.hhplus.tdd;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointRepositoryImpl;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointServiceTest {

    private final PointService pointService = new PointService(new PointRepositoryImpl());

    @Test
    void 조회_테스트() {

        long id = 2L;
        UserPoint userPoint = pointService.point(id);
        assertEquals(id, userPoint.id());
        assertEquals(0, userPoint.point());
    }

    @Test
    void 충전_테스트() {

        long id = 2L;
        long amount = 200;

        pointService.charge(id, amount);
        UserPoint userPoint = pointService.point(id);

        assertEquals(userPoint.point(), amount);
    }

    @Test
    void 충전_이력_조회_테스트() {

        long id = 2L;
        long amount = 200;

        pointService.charge(id, amount);
        List<PointHistory> pointHistoryList = pointService.history(id);

        for (PointHistory pointHistory : pointHistoryList) {
            assertEquals(id, pointHistory.userId());
            assertEquals(amount, pointHistory.amount());
        }
    }
}
