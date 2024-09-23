package io.hhplus.tdd;

import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointServiceTest {

    private final PointService pointService = new PointService(new PointRepositoryImpl());

    /**
     * 조회 테스트
     * */
    @Test
    void 아이디_정상_조회() {
        //given
        long id = 1;

        //when
        UserPoint userPoint = pointService.point(id);

        //then
        assertEquals(id, userPoint.id());
    }

    /**
     * 조회 테스트
     * 아이디가 정수가 아닐때
     * */
    @Test
    void 아이디가_정수가_아닐경우() {
        //given
        long id = 0;
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> pointService.point(id));
    }

    /**
     * 정상적인 포인트 충전
     * */
    @Test
    void 포인트_정산_충전() {
        //given
        long id = 2;
        long amount = 200;
        //when
        pointService.charge(id, amount);
        UserPoint userPoint = pointService.point(id);
        //then
        assertEquals(userPoint.point(), amount);
    }

    /**
     * 비정상적인 포인트 충전
     * 충전금액 0이하
     * */
    @Test
    void 충전_금액이_정수가_아닐_때() {
        //given
        long id = 2;
        long amount = 0;
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> pointService.charge(id, amount));
    }

    /**
     * 비정상적인 포인트 충전
     * 충전금액이 포인트 정책 최대값보다 많은때
     * 포인트 최대 소유는 10,000
     * */
    @Test
    void 충전_금액이_포인트_최대값_초과() {
        //given
        long id = 2;
        long amount = 10001;
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> pointService.charge(id, amount));
    }

    /**
     * 충전 이력 조회 테스트
     * */
    @Test
    void 충전_이력_조회() {
        //given
        long id = 2L;
        long amount = 200;
        pointService.charge(id, amount);
        //when
        List<PointHistory> pointHistoryList = pointService.history(id);
        //then
        for (PointHistory pointHistory : pointHistoryList) {
            assertEquals(id, pointHistory.userId());
            assertEquals(amount, pointHistory.amount());
            assertEquals(TransactionType.CHARGE, pointHistory.type());
        }
    }

    /**
     * 충전된 포인트가 없을 때 포인트 사용
     * */
    @Test
    void 포인트가_없을떄_포인트_사용() {
        //given
        long id = 1;
        long amount = 100;
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> pointService.use(id, amount));
    }

    /**
     * 충전된 포인트보다 초과하여 사용
     * */
    @Test
    void 충정된_포인트보다_초과_사용() {
        //given
        long id = 1;
        long chargeAmount = 100;
        long useAmount = 200;
        pointService.charge(id, chargeAmount);
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> pointService.use(id, useAmount));
    }

    /**
     * 포인트 사용 이력 조회
     * 충전 후 사용 이력 조회
     * 일단 충전 -> 사용 두단계만...
     * 이럴때는 테스트를 어떻게...?
     * */
    @Test
    void 포인트_사용_이력_조회() {
        //given
        long id = 1;
        long chargeAmount = 200;
        long useAmount = 100;
        pointService.charge(id, chargeAmount);
        pointService.use(id, useAmount);
        //when
        List<PointHistory> pointHistoryList = pointService.history(id);
        //then
        for (PointHistory pointHistory : pointHistoryList) {
            assertEquals(id, pointHistory.userId());
            if (pointHistory.type() == TransactionType.CHARGE) {
                assertEquals(chargeAmount, pointHistory.amount());
                assertEquals(TransactionType.CHARGE, pointHistory.type());
            } else if (pointHistory.type() == TransactionType.USE) {
                assertEquals(useAmount, pointHistory.amount());
                assertEquals(TransactionType.USE, pointHistory.type());
            }
        }
    }
}
