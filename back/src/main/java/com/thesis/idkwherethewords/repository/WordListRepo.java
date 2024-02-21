package com.thesis.idkwherethewords.repository;

import com.thesis.idkwherethewords.entity.User;
import com.thesis.idkwherethewords.entity.Word;
import com.thesis.idkwherethewords.entity.Wordlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordListRepo extends JpaRepository<Wordlist, Long> {
    Wordlist findByUserAndWord(User user, Word word);
    void deleteByUserAndWord(User user, Word word);
}
