package io.hhplus.tdd;

import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    void 포인트_정상_충전() {
        //given
        long id = 2;
        long amount = 200;
        //when
        pointService.charge(id, amount);
        pointService.actionRequest();
        UserPoint userPoint = pointService.point(id);
        //then
        assertEquals(userPoint.point(), amount);
    }

    /**
     * 비정상적인 포인트 충전
     * 충전금액 0이하
     * */
    @Test
    void 충전_금액이_정수가_아닐_때_에러메세지_확인() {
        //given
        long id = 2;
        long amount = 0;
        String errorMsg = "";
        //when
        pointService.charge(id, amount);
        try {
            pointService.actionRequest();
        } catch (IllegalArgumentException e) {
            errorMsg = e.getMessage();
        }
        //then
        assertEquals("금액은 0보다 커야 됩니다.", errorMsg);
    }

    /**
     * 비정상적인 포인트 충전
     * 충전금액이 포인트 정책 최대값보다 많은때
     * 포인트 최대 소유는 10,000
     * */
    @Test
    void 충전_금액이_포인트_최대값_초과_에러메세지_확인() {
        //given
        long id = 2;
        long amount = 10001;
        String errorMsg = "";
        //when
        pointService.charge(id, amount);
        try {
            pointService.actionRequest();
        } catch (IllegalArgumentException e) {
            errorMsg = e.getMessage();
        }
        //then
        assertEquals("최대 적립 포인트를 초과하였습니다.", errorMsg);
    }

    /**
     * 포인트 정상 사용
     * 충전 후 사용
     */
    @Test
    void 포인트_정상_사용() {
        //given
        long id = 1;
        long chargeAmount = 100;
        long useAmount = 100;
        pointService.charge(id, chargeAmount);
        pointService.use(id, useAmount);
        //when
        pointService.actionRequest();
        UserPoint userPoint = pointService.point(id);
        //then
        assertEquals(0, userPoint.point());
    }

    /**
     * 충전된 포인트가 없을 때 포인트 사용
     * */
    @Test
    void 포인트가_없을떄_포인트_사용() {
        //given
        long id = 1;
        long amount = 100;
        String errorMsg = "";
        //when
        pointService.use(id, amount);
        try {
            pointService.actionRequest();
        } catch (IllegalArgumentException e) {
            errorMsg = e.getMessage();
        }
        //then
        assertEquals("포인트가 부족합니다.", errorMsg);
    }

    /**
     * 충전된 포인트보다 초과하여 사용
     * */
    @Test
    void 충전된_포인트보다_초과_사용() {
        //given
        long id = 1;
        long chargeAmount = 100;
        long useAmount = 200;
        String errorMsg = "";
        pointService.charge(id, chargeAmount);
        pointService.use(id, useAmount);
        //when
        try {
            pointService.actionRequest();
        } catch (IllegalArgumentException e) {
            errorMsg = e.getMessage();
        }
        //then
        assertEquals("포인트가 부족합니다.", errorMsg);
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
        pointService.actionRequest();
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
     * 포인트 사용 이력 조회
     * 충전 후 사용 이력 조회
     * */
    @Test
    void 포인트_사용_이력_조회() {
        //given
        long id = 1;
        long chargeAmount = 200;
        long useAmount = 100;
        pointService.charge(id, chargeAmount);
        pointService.use(id, useAmount);
        pointService.actionRequest();
        //when
        List<PointHistory> pointHistoryList = pointService.history(id);
        //then
        assertEquals(2, pointHistoryList.size());
    }
}
