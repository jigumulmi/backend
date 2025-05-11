package com.jigumulmi.place

import com.jigumulmi.place.domain.Menu
import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.dto.MenuDto
import com.jigumulmi.place.repository.MenuRepository
import com.jigumulmi.place.repository.PlaceRepository
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
internal class AdminPlaceServiceTest {
    @Autowired
    private lateinit var adminPlaceService: AdminPlaceService

    @Autowired
    private lateinit var menuRepository: MenuRepository

    @Autowired
    private lateinit var placeRepository: PlaceRepository

    @ParameterizedTest
    @MethodSource("getMenuImageFilename")
    @DisplayName("메뉴 수정 - 파일 확장자 제거 테스트")
    fun testUpdateMenu(imageFilename: String?) {
        // given
        val menuDto = MenuDto.builder()
            .imageFilename(imageFilename)
            .build()

        val place = Place.builder().build()
        placeRepository.save(place)

        // when
        adminPlaceService.updateMenu(place.id, listOf(menuDto))

        // then
        val menu = menuRepository.findAllByPlaceId(place.id).first()
        val imageS3Key = menu.imageS3Key
        val hasExtension = (StringUtils.contains(imageS3Key, ".")
                && StringUtils.lastIndexOf(imageS3Key, ".") > 0)
        Assertions.assertFalse(hasExtension)
    }

    @ParameterizedTest
    @MethodSource("getMenuImageFilename")
    @DisplayName("메뉴 목록 조회")
    fun testGetMenu(imageFilename: String?) {
        // given
        val place = Place.builder().build()
        placeRepository.save(place)

        val menu = Menu.builder()
            .place(place)
            .imageS3Key("prefix/$imageFilename")
            .build()
        menuRepository.save(menu)

        // when

        // then
        Assertions.assertDoesNotThrow<List<MenuDto>> { adminPlaceService.getMenu(place.id) }
    }

    companion object {
        @JvmStatic
        private fun getMenuImageFilename(): Stream<Arguments> = Stream.of(
            Arguments.of(null as String?),
            Arguments.of("image"),
            Arguments.of("image.png"),
            Arguments.of(""),
            Arguments.of(".png")
        )
    }
}