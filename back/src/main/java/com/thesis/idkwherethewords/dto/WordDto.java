package com.thesis.idkwherethewords.dto;

import com.thesis.idkwherethewords.entity.enumerated.WordStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WordDto {
    private String original;
    private WordInfoDto wordInfo;

    public WordDto(String original, String baseForm, String partOfSpeech, String level, WordStatus status) {
        this.original = original;
        wordInfo = new WordInfoDto(baseForm, partOfSpeech, level, status.name());
    }

    public WordDto(String original, String baseForm, String partOfSpeech, String level) {
        this.original = original;
        wordInfo = new WordInfoDto(baseForm, partOfSpeech, level, WordStatus.LEARNING.name());
    }
}
