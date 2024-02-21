package com.thesis.idkwherethewords.repository;

import com.thesis.idkwherethewords.entity.Vocabulary;
import com.thesis.idkwherethewords.entity.primaryKeys.VocabularyPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyRepo extends JpaRepository<Vocabulary, VocabularyPK> {
    Optional<Vocabulary> findByPk(VocabularyPK pk);
}
