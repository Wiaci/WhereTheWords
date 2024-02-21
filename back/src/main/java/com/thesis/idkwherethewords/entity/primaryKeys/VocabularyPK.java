package com.thesis.idkwherethewords.entity.primaryKeys;

import com.thesis.idkwherethewords.entity.enumerated.PartOfSpeech;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class VocabularyPK implements Serializable {

    private String word;

    @Column(name = "part_of_speech")
    @Enumerated(EnumType.STRING)
    private PartOfSpeech partOfSpeech;

    public VocabularyPK() {
    }

    public VocabularyPK(String word, PartOfSpeech partOfSpeech) {
        this.word = word;
        this.partOfSpeech = partOfSpeech;
    }
}
