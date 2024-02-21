package com.thesis.idkwherethewords.dto;

import com.thesis.idkwherethewords.entity.Word;
import com.thesis.idkwherethewords.entity.Wordlist;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WordInfoDto {
    private String baseForm;
    private String partOfSpeech;
    private String level;
    private String wordStatus;

    public WordInfoDto(Wordlist wordlist) {
        baseForm = wordlist.getWord().getBaseForm();
        partOfSpeech = wordlist.getWord().getPartOfSpeech().name();
        level = wordlist.getWord().getLevel().name();
        wordStatus = wordlist.getStatus().name();
    }

    public WordInfoDto(Word word) {
        baseForm = word.getBaseForm();
        partOfSpeech = word.getPartOfSpeech().name();
        level = word.getLevel().name();
        wordStatus = "LEARNING";
    }

    public WordInfoDto() {};
}
