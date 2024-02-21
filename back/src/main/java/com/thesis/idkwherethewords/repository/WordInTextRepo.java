package com.thesis.idkwherethewords.repository;

import com.thesis.idkwherethewords.entity.Word;
import com.thesis.idkwherethewords.entity.WordInText;
import com.thesis.idkwherethewords.entity.primaryKeys.WordInTextPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordInTextRepo extends JpaRepository<WordInText, WordInTextPK> {
    List<WordInText> findAllByPk_WordIn(List<Word> words);
}
