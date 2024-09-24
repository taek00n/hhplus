package io.hhplus.tdd;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Test
    @DisplayName("특정 유저의 포인트를 조회")
    void point() throws Exception {
        //given
        long id = 1;
        //when
        //then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회")
    void history() throws Exception {
        //given
        long id = 1;
        //when
        //then
        mockMvc.perform(get("/point/{id}/histories", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전")
    void charge() throws Exception {
        //given
        long id = 1;
        long amount = 100;
        //when
        when(pointService.charge(id, amount))
                .thenReturn(new UserPoint(id, amount, System.currentTimeMillis()));
        //then
        mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용하는 기능")
    void use() throws Exception {
        //given
        long id = 1;
        long amount = 100;
        //when
        when(pointService.use(id, amount))
                .thenReturn(new UserPoint(id, amount, System.currentTimeMillis()));
        //then
        mockMvc.perform(patch("/point/{id}/use", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.valueOf(amount)))
                .andExpect(status().isOk());
    }
}
