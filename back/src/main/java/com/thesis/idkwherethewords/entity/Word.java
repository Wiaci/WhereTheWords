package com.thesis.idkwherethewords.entity;

import com.thesis.idkwherethewords.entity.enumerated.LanguageLevel;
import com.thesis.idkwherethewords.entity.enumerated.PartOfSpeech;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Table
@Data
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_form")
    private String baseForm;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    @Enumerated(EnumType.STRING)
    private LanguageLevel level;

    public Word() {}

    public Word(String baseForm, PartOfSpeech partOfSpeech, LanguageLevel level) {
        this.baseForm = baseForm;
        this.partOfSpeech = partOfSpeech;
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return baseForm.equals(word.baseForm) && partOfSpeech == word.partOfSpeech;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseForm, partOfSpeech);
    }
}
