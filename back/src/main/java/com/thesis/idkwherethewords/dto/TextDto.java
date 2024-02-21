package com.thesis.idkwherethewords.dto;

import com.thesis.idkwherethewords.entity.Text;
import com.thesis.idkwherethewords.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class TextDto {
    private Long id;
    private String firstSentence;
    private List<WordDto> words;
    private String level;
    private List<String> topics;

    public TextDto(Text text, List<WordDto> words, String firstSentence) {
        id = text.getId();
        this.firstSentence = firstSentence;
        level = text.getLevel().name();
        this.words = words;
        topics = text.getTopics().stream()
                .map(Topic::getBeautifiedName)
                .collect(Collectors.toList());
    }
}
