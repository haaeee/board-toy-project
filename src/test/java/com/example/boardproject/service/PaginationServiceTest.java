package com.example.boardproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/***
 * 스프링 부트 테스트 경량화: NONE, classes: 설정 클래스 default (@SpringBootApplication) 임의로 지정할 수 있다.
 * Void.class: ComponentScan x
 */
@DisplayName("비즈니스 로직 - 페이지네이션")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
class PaginationServiceTest {

    private final PaginationService sut;

    // Test 에서는 꼭 생성자 주입에 @Autowired (X) -> org.junit.jupiter.api.extension.ParameterResolutionException
    // 주입 받는 것이 자기 자신이므로 그냥 new 로 구체화 할 수 있다.
     PaginationServiceTest(@Autowired PaginationService sut) {
        this.sut = sut;
    }

    /**
     * 파라미터 값을 여러번 주입해서, 여러번 테스트를 진행할 수 있다. 입력값을 넣어주는 MethodSource 는 Method 형식으로 만든다. 이 때 테스트 메소드가 default와 다르면 적어줘야 한다.
     * ParameterizedTest DisplayName 설정 (JUNIT5 공식문서)
     */
    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면, 페이징 바 리스트를 만들어준다.")
    @MethodSource
    @ParameterizedTest(name = "[{index}] 현재 페이지: {0}, 총 페이지: {1} => {2}")
    void givenCurrentPageNumberAndTotalPages_whenCalculating_thenReturnsPaginationBarNumbers(int currentPageNumber,
                                                                                             int totalPages,
                                                                                             List<Integer> expected
    ) {
        // Given

        // When
        List<Integer> actual = sut.getPaginationBarNumbers(currentPageNumber, totalPages);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenCurrentPageNumberAndTotalPages_whenCalculating_thenReturnsPaginationBarNumbers() {
        return Stream.of(
                arguments(0, 12, List.of(0, 1, 2, 3, 4)),
                arguments(1, 12, List.of(0, 1, 2, 3, 4)),
                arguments(2, 12, List.of(0, 1, 2, 3, 4)),
                arguments(3, 12, List.of(1, 2, 3, 4, 5)),
                arguments(6, 12, List.of(4, 5, 6, 7, 8)),
                arguments(7, 12, List.of(5, 6, 7, 8, 9)),
                arguments(10, 12, List.of(8, 9, 10, 11)),
                arguments(11, 12, List.of(9, 10, 11))
        );
    }

    /**
     * 현재 페이지네이션 바의 길이를 협업 할 때 기본 값을 알 수 있다. 즉, 스펙의 명세
     */
    @DisplayName("현재 설정되어 있는 페이지네이션 바의 길이를 알려준다.")
    @Test
    void givenNothing_whenCalling_thenReturnsCurrentBarLength() {
        // Given

        // When
        int barLength = sut.currentBarLength();

        // Then
        assertThat(barLength).isEqualTo(5);
    }
}
