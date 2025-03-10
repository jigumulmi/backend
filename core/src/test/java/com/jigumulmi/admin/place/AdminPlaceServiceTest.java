package com.jigumulmi.admin.place;

import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class AdminPlaceServiceTest {

    @Autowired
    private AdminPlaceService adminPlaceService;

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private PlaceRepository placeRepository;

    private static Stream<Arguments> getMenuImageFilename() {
        return Stream.of(
            Arguments.of((String) null),
            Arguments.of("image"),
            Arguments.of("image.png"),
            Arguments.of(""),
            Arguments.of(".png")
        );
    }

    @ParameterizedTest
    @MethodSource("getMenuImageFilename")
    @DisplayName("메뉴 수정 - 파일 확장자 제거 테스트")
    public void testUpdateMenu(String imageFilename) {
        // given
        MenuDto menuDto = MenuDto.builder()
            .imageFilename(imageFilename)
            .build();

        Place place = Place.builder().build();
        placeRepository.save(place);

        // when
        adminPlaceService.updateMenu(place.getId(), List.of(menuDto));

        // then
        Menu menu = menuRepository.findAllByPlaceId(place.getId()).getFirst();
        String imageS3Key = menu.getImageS3Key();
        boolean hasExtension = StringUtils.contains(imageS3Key, ".")
            && StringUtils.lastIndexOf(imageS3Key, ".") > 0;
        Assertions.assertFalse(hasExtension);
    }

    @ParameterizedTest
    @MethodSource("getMenuImageFilename")
    @DisplayName("메뉴 목록 조회")
    public void testGetMenu(String imageFilename) {
        // given
        Place place = Place.builder().build();
        placeRepository.save(place);

        Menu menu = Menu.builder()
            .place(place)
            .imageS3Key("prefix/" + imageFilename)
            .build();
        menuRepository.save(menu);

        // when

        // then
        Assertions.assertDoesNotThrow(() -> adminPlaceService.getMenu(place.getId()));

    }
}