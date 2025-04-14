package com.jigumulmi.place

import com.fasterxml.jackson.databind.ObjectMapper
import com.jigumulmi.annotation.ControllerTest
import com.jigumulmi.place.dto.BusinessHour
import com.jigumulmi.place.dto.WeeklyBusinessHourDto
import com.jigumulmi.place.dto.request.AdminCreatePlaceRequestDto
import com.jigumulmi.security.MockMember
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalTime
import java.util.stream.Stream

@ControllerTest(AdminPlaceController::class)
internal class AdminPlaceControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var adminPlaceService: AdminPlaceService

    @ParameterizedTest
    @MethodSource("provideBusinessHours")
    @DisplayName("장소 고정 영업시간 수정 요청 검증")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testUpdateFixedBusinessHourValidation(
        businessHour: BusinessHour?,
        expectedStatus: HttpStatus
    ) {
        // given
        val placeId = 1L

        val baseBusinessHour = BusinessHour.builder().isDayOff(true).build()
        val requestDto = WeeklyBusinessHourDto()
        ReflectionTestUtils.setField(requestDto, "sunday", businessHour)
        ReflectionTestUtils.setField(requestDto, "monday", baseBusinessHour)
        ReflectionTestUtils.setField(requestDto, "tuesday", baseBusinessHour)
        ReflectionTestUtils.setField(requestDto, "wednesday", baseBusinessHour)
        ReflectionTestUtils.setField(requestDto, "thursday", baseBusinessHour)
        ReflectionTestUtils.setField(requestDto, "friday", baseBusinessHour)
        ReflectionTestUtils.setField(requestDto, "saturday", baseBusinessHour)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.put("/admin/place/{placeId}/business-hour/fixed", placeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus.value()))
            .andDo(MockMvcResultHandlers.print())
    }

    @ParameterizedTest
    @MethodSource("getKakaoPlaceId")
    @DisplayName("장소 생성 - 요청 검증")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testCreatedPlaceValidation(kakaoPlaceId: String?, expectedStatus: HttpStatus) {
        // given
        val requestDto = AdminCreatePlaceRequestDto()
        ReflectionTestUtils.setField(requestDto, "kakaoPlaceId", kakaoPlaceId)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.post("/admin/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus.value()))
            .andDo(MockMvcResultHandlers.print())
    }

    companion object {
        @JvmStatic
        private fun provideBusinessHours(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
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
                    null,
                    HttpStatus.UNPROCESSABLE_ENTITY
                ),
                Arguments.of(
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
                    BusinessHour.builder()
                        .isDayOff(false)
                        .build(),
                    HttpStatus.UNPROCESSABLE_ENTITY
                ),
                Arguments.of(
                    BusinessHour.builder()
                        .openTime(LocalTime.of(10, 0))
                        .closeTime(LocalTime.of(20, 0))
                        .breakStart(LocalTime.of(14, 0))
                        .isDayOff(true)
                        .build(),
                    HttpStatus.UNPROCESSABLE_ENTITY
                ),
                Arguments.of(
                    BusinessHour.builder()
                        .openTime(LocalTime.of(10, 0))
                        .closeTime(LocalTime.of(20, 0))
                        .breakEnd(LocalTime.of(15, 0))
                        .isDayOff(true)
                        .build(),
                    HttpStatus.UNPROCESSABLE_ENTITY
                )
            )
        }

        @JvmStatic
        private fun getKakaoPlaceId(): Stream<Arguments> = Stream.of(
            Arguments.of(null, HttpStatus.CREATED),
            Arguments.of("1234", HttpStatus.CREATED),
            Arguments.of("", HttpStatus.UNPROCESSABLE_ENTITY),
            Arguments.of(" ", HttpStatus.UNPROCESSABLE_ENTITY)
        )
    }
}