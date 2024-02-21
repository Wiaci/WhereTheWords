package com.thesis.idkwherethewords.entity;

import com.thesis.idkwherethewords.entity.enumerated.LanguageLevel;
import com.thesis.idkwherethewords.entity.primaryKeys.VocabularyPK;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table
@Getter
public class Vocabulary {

    @EmbeddedId
    private VocabularyPK pk;

    @Enumerated(EnumType.STRING)
    private LanguageLevel level;
}
