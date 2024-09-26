package io.hhplus.tdd;

import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class PointServiceConcurrencyTest {

    private final PointService pointService = new PointService(new PointRepositoryImpl());
    private final Long ID = 1L;
    private final Long ID2 = 2L;

    @BeforeEach
    void 포인트_충전() {
        long amount = 500;
        pointService.charge(ID, amount);
        pointService.charge(ID2, amount);
        pointService.actionRequest();
    }

    /*
     * 여러번요청으로 정상적으로 충전이 되는지 확인
     * */
    @Test
    void 여러번_충전_보내기_테스트() throws InterruptedException {
        // given
        UserPoint userInfo = pointService.point(ID);
        Long userId = userInfo.id();
        Long userPoint = userInfo.point();

        int threads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        //when
        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                pointService.charge(userId, 500);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        pointService.actionRequest();
        userInfo = pointService.point(ID);
        List<PointHistory> pointHistoryList = pointService.history(ID);
        //then
        assertEquals(userPoint + (500 * threads), userInfo.point());
        assertEquals(threads+1, pointHistoryList.size());
    }

    /*
     * 여러번요청으로 정상적으로 충전이 되는지 확인
     * */
    @Test
    void 여러번_사용_보내기_테스트() throws InterruptedException {
        //given
        UserPoint userInfo = pointService.point(ID);
        Long userId = userInfo.id();
        Long userPoint = userInfo.point();

        int threads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        //when
        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                pointService.use(userId, 10);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        //then
        pointService.actionRequest();
        userInfo = pointService.point(ID);
        List<PointHistory> pointHistoryList = pointService.history(ID);
        //then
        assertEquals(userPoint - (10 * threads), userInfo.point());
        assertEquals(threads+1, pointHistoryList.size());
    }

    @Test
    void 두개의_쓰래드에서_한명에게_동시_충전과_사용() throws InterruptedException {
        //given
        int threads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        executorService.execute(() -> {
            pointService.charge(ID, 500);
            countDownLatch.countDown();
        });

        executorService.execute(() -> {
            pointService.use(ID, 300);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        executorService.shutdown();

        //when
        pointService.actionRequest();
        UserPoint userInfo = pointService.point(ID);
        //then
        assertEquals(700, userInfo.point());
    }

    @Test
    void 두개의_쓰래드에서_두명에게_충전과_사용() throws InterruptedException {
        //given
        int threads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        executorService.execute(() -> {
            pointService.charge(ID, 500);
            pointService.charge(ID2, 500);
            pointService.use(ID2, 300);
            countDownLatch.countDown();
        });

        executorService.execute(() -> {
            pointService.use(ID, 300);
            pointService.use(ID2, 300);
            pointService.charge(ID, 500);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        executorService.shutdown();


        //when
        pointService.actionRequest();
        UserPoint userInfo = pointService.point(ID);
        UserPoint userInfo2 = pointService.point(ID2);

        //then
        assertEquals(1200, userInfo.point());
        assertEquals(400, userInfo2.point());

        // history 확인
//        List<PointHistory> pointHistoryList = pointService.history(ID);
//        List<PointHistory> pointHistoryList2 = pointService.history(ID2);

//        System.out.println("pointHistory");
//        pointHistoryList.forEach(pointHistory -> {
//            System.out.println(pointHistory);
//        });
//
//        System.out.println("pointHistory2");
//        pointHistoryList2.forEach(pointHistory -> {
//            System.out.println(pointHistory);
//        });
    }
}
