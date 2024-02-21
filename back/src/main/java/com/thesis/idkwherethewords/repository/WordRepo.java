package com.thesis.idkwherethewords.repository;

import com.thesis.idkwherethewords.entity.Word;
import com.thesis.idkwherethewords.entity.enumerated.LanguageLevel;
import com.thesis.idkwherethewords.entity.enumerated.PartOfSpeech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepo extends JpaRepository<Word, Long> {
    boolean existsByBaseFormAndPartOfSpeech(String baseForm, PartOfSpeech pos);
    Optional<Word> findByBaseFormAndPartOfSpeech(String baseForm, PartOfSpeech pos);
    List<Word> findByLevel(LanguageLevel level);
}
