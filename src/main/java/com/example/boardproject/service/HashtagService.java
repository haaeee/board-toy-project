package com.example.boardproject.service;

import com.example.boardproject.domain.Hashtag;
import com.example.boardproject.repository.HashtagRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public HashtagService(final HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    public Set<String> parseHashtagNames(String content) {
        if (Objects.isNull(content)) {
            return Set.of();
        }

        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());

        Set<String> result = new HashSet<>();

        while (matcher.find()) {
            result.add(matcher.group().replace("#", ""));
        }

        // return unmodifiable 보장하는 방법
        return Set.copyOf(result);
    }

    public Set<Hashtag> findHashtagsByNames(Set<String> expectedHashtagNames) {
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(expectedHashtagNames));
    }

    public void deleteHashtagWithoutArticles(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);

        if (hashtag.getArticles().isEmpty()) {
            hashtagRepository.delete(hashtag);
        }
    }

    public List<String> findAllHashtagNames() {
        return hashtagRepository.findAllHashtagNames();
    }
}
