package com.jigumulmi.admin.place;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;
import com.jigumulmi.common.annotation.ControllerTest;
import com.jigumulmi.config.security.MockMember;
import com.jigumulmi.place.dto.BusinessHour;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ControllerTest(AdminPlaceController.class)
class AdminPlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminPlaceService adminPlaceService;

    private static Stream<Arguments> provideBusinessHours() {
        return Stream.of(
            Arguments.of(BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false)
                    .build(),
                HttpStatus.CREATED
            ),
            Arguments.of(null,
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(true)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(BusinessHour.builder()
                    .isDayOff(false)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .isDayOff(true)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(true)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideBusinessHours")
    @DisplayName("장소 고정 영업시간 수정 요청 검증")
    @MockMember(isAdmin = true)
    public void testUpdateFixedBusinessHourValidation(BusinessHour businessHour,
        HttpStatus expectedStatus) throws Exception {
        // given
        Long placeId = 1L;

        BusinessHour baseBusinessHour = BusinessHour.builder().isDayOff(true).build();
        WeeklyBusinessHourDto requestDto = new WeeklyBusinessHourDto();
        ReflectionTestUtils.setField(requestDto, "sunday", businessHour);
        ReflectionTestUtils.setField(requestDto, "monday", baseBusinessHour);
        ReflectionTestUtils.setField(requestDto, "tuesday", baseBusinessHour);
        ReflectionTestUtils.setField(requestDto, "wednesday", baseBusinessHour);
        ReflectionTestUtils.setField(requestDto, "thursday", baseBusinessHour);
        ReflectionTestUtils.setField(requestDto, "friday", baseBusinessHour);
        ReflectionTestUtils.setField(requestDto, "saturday", baseBusinessHour);

        // when
        ResultActions perform = mockMvc.perform(
            put("/admin/place/{placeId}/business-hour/fixed", placeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().is(expectedStatus.value()))
            .andDo(print());

    }

    private static Stream<Arguments> createTemporaryBusinessHourRequestDtoParams() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        return Stream.of(
            Arguments.of(
                today,
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false)
                    .build(),
                HttpStatus.CREATED
            ),
            Arguments.of(
                yesterday,
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(
                today,
                null,
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(
                today,
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(true)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(
                today,
                BusinessHour.builder()
                    .isDayOff(false)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(
                today,
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .isDayOff(true)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            ),
            Arguments.of(
                today,
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(true)
                    .build(),
                HttpStatus.UNPROCESSABLE_ENTITY
            )
        );
    }

    @ParameterizedTest
    @MethodSource("createTemporaryBusinessHourRequestDtoParams")
    @DisplayName("장소 변동 영업시간 생성 요청 검증")
    @MockMember(isAdmin = true)
    public void testCreateTemporaryBusinessHourValidation(LocalDate date, BusinessHour businessHour,
        HttpStatus expectedStatus) throws Exception {
        // given
        Long placeId = 1L;

        AdminCreateTemporaryBusinessHourRequestDto requestDto = AdminCreateTemporaryBusinessHourRequestDto.builder()
            .date(date)
            .businessHour(businessHour)
            .build();

        // when
        ResultActions perform = mockMvc.perform(
            post("/admin/place/{placeId}/business-hour/temporary", placeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().is(expectedStatus.value()))
            .andDo(print());

    }

    private static Stream<Arguments> getWrongKakaoPlaceId() {
        return Stream.of(
            Arguments.of("", false),
            Arguments.of(" ", false),
            Arguments.of("1234", null)
        );
    }

    @ParameterizedTest
    @MethodSource("getWrongKakaoPlaceId")
    @DisplayName("장소 생성 - 요청 검증")
    @MockMember(isAdmin = true)
    public void testCreatedPlaceValidation(String kakaoPlaceId, Boolean isApproved)
        throws Exception {
        // given
        AdminCreatePlaceRequestDto requestDto = new AdminCreatePlaceRequestDto();
        ReflectionTestUtils.setField(requestDto, "isApproved", isApproved);
        ReflectionTestUtils.setField(requestDto, "kakaoPlaceId", kakaoPlaceId);

        // when
        ResultActions perform = mockMvc.perform(
            post("/admin/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isUnprocessableEntity())
            .andDo(print());

    }

}