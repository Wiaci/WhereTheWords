package com.thesis.idkwherethewords.entity.primaryKeys;

import com.thesis.idkwherethewords.entity.Text;
import com.thesis.idkwherethewords.entity.Word;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class WordInTextPK implements Serializable {
    @ManyToOne
    private Word word;

    @ManyToOne
    private Text text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordInTextPK that = (WordInTextPK) o;
        return word.equals(that.word) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, text);
    }
}
