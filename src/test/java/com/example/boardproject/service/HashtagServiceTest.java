package com.example.boardproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.boardproject.domain.Hashtag;
import com.example.boardproject.repository.HashtagRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("비즈니스 로직 - 해시태그")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

    @InjectMocks
    private HashtagService sut;

    @Mock
    private HashtagRepository hashtagRepository;

    @MethodSource
    @ParameterizedTest(name = "[{index}] \"{0}\" => {1}")
    void 게시글을_파싱하면_해시태그_이름들을_중복_없이_반환한다(String input, Set<String> expected) {

        //given

        // whenr
        Set<String> actual = sut.parseHashtagNames(input);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        then(hashtagRepository).shouldHaveNoInteractions();
    }

    static Stream<Arguments> 게시글을_파싱하면_해시태그_이름들을_중복_없이_반환한다() {
        return Stream.of(
                arguments(null, Set.of()),
                arguments("", Set.of()),
                arguments("   ", Set.of()),
                arguments("#", Set.of()),
                arguments("  #", Set.of()),
                arguments("#   ", Set.of()),
                arguments("java", Set.of()),
                arguments("java#", Set.of()),
                arguments("ja#va", Set.of("va")),
                arguments("#java", Set.of("java")),
                arguments("#java_spring", Set.of("java_spring")),
                arguments("#java-spring", Set.of("java")),
                arguments("#_java_spring", Set.of("_java_spring")),
                arguments("#-java-spring", Set.of()),
                arguments("#_java_spring__", Set.of("_java_spring__")),
                arguments("#java#spring", Set.of("java", "spring")),
                arguments("#java #spring", Set.of("java", "spring")),
                arguments("#java  #spring", Set.of("java", "spring")),
                arguments("#java   #spring", Set.of("java", "spring")),
                arguments("#java     #spring", Set.of("java", "spring")),
                arguments("  #java     #spring ", Set.of("java", "spring")),
                arguments("   #java     #spring   ", Set.of("java", "spring")),
                arguments("#java#spring#부트", Set.of("java", "spring", "부트")),
                arguments("#java #spring#부트", Set.of("java", "spring", "부트")),
                arguments("#java#spring #부트", Set.of("java", "spring", "부트")),
                arguments("#java,#spring,#부트", Set.of("java", "spring", "부트")),
                arguments("#java.#spring;#부트", Set.of("java", "spring", "부트")),
                arguments("#java|#spring:#부트", Set.of("java", "spring", "부트")),
                arguments("#java #spring  #부트", Set.of("java", "spring", "부트")),
                arguments("   #java,? #spring  ...  #부트 ", Set.of("java", "spring", "부트")),
                arguments("#java#java#spring#부트", Set.of("java", "spring", "부트")),
                arguments("#java#java#java#spring#부트", Set.of("java", "spring", "부트")),
                arguments("#java#spring#java#부트#java", Set.of("java", "spring", "부트")),
                arguments("#java#스프링 아주 긴 글~~~~~~~~~~~~~~~~~~~~~", Set.of("java", "스프링")),
                arguments("아주 긴 글~~~~~~~~~~~~~~~~~~~~~#java#스프링", Set.of("java", "스프링")),
                arguments("아주 긴 글~~~~~~#java#스프링~~~~~~~~~~~~~~~", Set.of("java", "스프링")),
                arguments("아주 긴 글~~~~~~#java~~~~~~~#스프링~~~~~~~~", Set.of("java", "스프링"))
        );
    }

    @Test
    void 해시태그_이름들을_입력하면_저장된_해시태그_중_이름에_매칭하는_것들을_중복_없이_반환한다() {
        // given
        Set<String> hashtagNames = Set.of("spring", "java", "spring_boot");
        given(hashtagRepository.findByHashtagNameIn(hashtagNames)).willReturn(
                List.of(Hashtag.of("spring"), Hashtag.of("java")));

        // when
        Set<Hashtag> hashtags = sut.findHashtagsByNames(hashtagNames);

        // then
        assertThat(hashtags.size()).isEqualTo(2);
        then(hashtagRepository).should().findByHashtagNameIn(hashtagNames);
    }
}