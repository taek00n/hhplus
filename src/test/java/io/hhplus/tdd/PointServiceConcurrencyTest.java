package io.hhplus.tdd;

import io.hhplus.tdd.point.PointRepositoryImpl;
import io.hhplus.tdd.point.PointService;
import org.junit.jupiter.api.Test;

public class PointServiceConcurrencyTest {

    private final PointService pointService = new PointService(new PointRepositoryImpl());

    @Test
    void 동시성_테스트() {

    }
}
