package com.thesis.idkwherethewords.service;

import com.thesis.idkwherethewords.entity.User;
import com.thesis.idkwherethewords.entity.Word;
import com.thesis.idkwherethewords.entity.Wordlist;
import com.thesis.idkwherethewords.entity.enumerated.WordStatus;
import com.thesis.idkwherethewords.repository.WordListRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordListService {

    private final WordListRepo wordListRepo;

    public void saveWordsForUser(User user, List<Word> words) {
        List<Wordlist> wordlists = words.stream()
                .map(word -> new Wordlist(user, word, WordStatus.LEARNING))
                .collect(Collectors.toList());
        wordListRepo.saveAll(wordlists);
    }

    public Wordlist findByUserAndWord(User user, Word word) {
        return wordListRepo.findByUserAndWord(user, word);
    }

    @Transactional
    public void deleteWord(User user, Word word) {
        wordListRepo.deleteByUserAndWord(user, word);
    }

    public void saveWord(Wordlist wordlist) {
        if (wordListRepo.findByUserAndWord(wordlist.getUser(), wordlist.getWord()) == null)
            wordListRepo.save(wordlist);
    }
}