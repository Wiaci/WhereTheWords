package com.thesis.idkwherethewords.entity;

import com.thesis.idkwherethewords.entity.primaryKeys.WordInTextPK;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "word_in_text")
@Data
public class WordInText {

    @EmbeddedId
    private WordInTextPK pk;

    private Integer count;

    public WordInText() {}

    public WordInText(Word word, Text text) {
        pk = new WordInTextPK();
        pk.setWord(word);
        pk.setText(text);
        count = 1;
    }

    public void countInc() {
        count++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordInText that = (WordInText) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk);
    }
}

