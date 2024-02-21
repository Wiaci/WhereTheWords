package com.thesis.idkwherethewords.service;

import com.thesis.idkwherethewords.dto.WordDto;
import com.thesis.idkwherethewords.dto.WordInfoDto;
import com.thesis.idkwherethewords.entity.*;
import com.thesis.idkwherethewords.entity.enumerated.LanguageLevel;
import com.thesis.idkwherethewords.entity.enumerated.PartOfSpeech;
import com.thesis.idkwherethewords.entity.enumerated.WordStatus;
import com.thesis.idkwherethewords.entity.primaryKeys.VocabularyPK;
import com.thesis.idkwherethewords.repository.UserRepo;
import com.thesis.idkwherethewords.repository.VocabularyRepo;
import com.thesis.idkwherethewords.repository.WordInTextRepo;
import com.thesis.idkwherethewords.repository.WordRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordInTextRepo wordInTextRepo;
    private final WordRepo wordRepo;
    private final VocabularyRepo vocabularyRepo;
    private final UserService userService;
    private final WordListService wordListService;

    /*@PostConstruct
    public void resetLevels() {
        List<Word> words = wordRepo.findAll();
        words.forEach(word -> {
            vocabularyRepo
                .findByPk(new VocabularyPK(word.getBaseForm(), word.getPartOfSpeech()))
                .ifPresent(vocabulary -> word.setLevel(vocabulary.getLevel()));
            wordRepo.save(word);
        });
    }*/

    public Word findByBaseFormAndPartOfSpeech(String baseForm, PartOfSpeech pos) {
        return wordRepo.findByBaseFormAndPartOfSpeech(baseForm, pos).get();
    }

    public void saveWordInTextList(List<WordInText> wordInTextList) {
        wordInTextList.forEach(wordInText -> {
            String baseForm = wordInText.getPk().getWord().getBaseForm();
            PartOfSpeech pos = wordInText.getPk().getWord().getPartOfSpeech();

            Optional<Word> wordOpt = wordRepo.findByBaseFormAndPartOfSpeech(baseForm, pos);
            wordOpt.ifPresent(wordInText.getPk()::setWord);
        });
        wordInTextRepo.saveAll(wordInTextList);
    }

    public void saveWordList(List<Word> wordList) {
        wordList.forEach(word -> {
            if (!wordRepo.existsByBaseFormAndPartOfSpeech(word.getBaseForm(), word.getPartOfSpeech())) {
                Optional<Vocabulary> optVocabulary = vocabularyRepo.findByPk(
                        new VocabularyPK(word.getBaseForm(), word.getPartOfSpeech()));
                optVocabulary.ifPresent(vocabulary -> word.setLevel(vocabulary.getLevel()));
                wordRepo.save(word);
            }
        });
    }

    public LanguageLevel getWordLevel(String word, PartOfSpeech pos) {
        Optional<Word> wordOptional = wordRepo.findByBaseFormAndPartOfSpeech(word, pos);
        if (wordOptional.isPresent()) {
            return wordOptional.get().getLevel();
        } else return LanguageLevel.C2;
    }

    public LanguageLevel getWordListLevel(List<Wordlist> wordlists) {
        return wordlists.stream()
                .filter(w -> w.getStatus().equals(WordStatus.LEARNING))
                .map(w -> w.getWord().getLevel())
                .sorted()
                .findFirst()
                .get();
    }

    public List<WordInfoDto> getStartWords() {
        List<WordInfoDto> words = new ArrayList<>();
        for (LanguageLevel level : LanguageLevel.values()) {
            List<Word> wordList = wordRepo.findByLevel(level);
            int count = Math.min(wordList.size(), 10);
            getRandomNumbers(wordList.size(), count)
                    .forEach(i -> words.add(new WordInfoDto(wordList.get(i))));

        }
        return words;
    }

    public List<Word> getSimplestUnknownWords(List<WordInfoDto> words) {
        List<Word> actualWords = new ArrayList<>();
        for (WordInfoDto word: words) {
            Word actWord = wordRepo.findByBaseFormAndPartOfSpeech(word.getBaseForm(),
                    PartOfSpeech.valueOf(word.getPartOfSpeech())).get();
            actualWords.add(actWord);
        }
        for (LanguageLevel level : LanguageLevel.values()) {
            long count = actualWords.stream()
                    .filter(word -> word.getLevel().equals(level))
                    .count();
            if (count > 2) {
                return actualWords.stream()
                        .filter(word -> word.getLevel().equals(level))
                        .collect(Collectors.toList());
            }
        }
        return actualWords;
    }

    public List<WordInText> getWordInTextsByWordlist(List<Word> words) {
        return wordInTextRepo.findAllByPk_WordIn(words);
    }

    public void updateWordlist(Long userId, WordInfoDto wordInfoDto) {
        Word word = wordRepo.findByBaseFormAndPartOfSpeech(wordInfoDto.getBaseForm(),
                PartOfSpeech.valueOf(wordInfoDto.getPartOfSpeech())).get();
        User user = userService.getUserById(userId);
        Wordlist wordlist = wordListService.findByUserAndWord(user, word);
        String status = wordInfoDto.getWordStatus();
        if (status.equals("DELETE")) {
            wordListService.deleteWord(user, word);
        } else {
            wordlist.setStatus(WordStatus.valueOf(status));
            wordListService.saveWord(wordlist);
        }
    }

    public WordInfoDto addWord(Long userId, WordInfoDto wordInfoDto) {
        User user = userService.getUserById(userId);
        String baseForm = wordInfoDto.getBaseForm();
        PartOfSpeech pos = PartOfSpeech.valueOf(wordInfoDto.getPartOfSpeech());
        Word word = wordRepo.findByBaseFormAndPartOfSpeech(baseForm, pos).orElse(
                new Word(baseForm, pos, getWordLevel(baseForm, pos)));
        Wordlist wordlist = new Wordlist(user, word, WordStatus.LEARNING);
        wordListService.saveWord(wordlist);
        return new WordInfoDto(wordlist);
    }

    private Set<Integer> getRandomNumbers(int max, int count) {
        Set<Integer> set = new HashSet<>();
        for (int i = max - count; i < max; i++) {
            int r = (int) (Math.random() * (i + 1));
            if (!set.contains(r)) set.add(r);
            else set.add(i);
        }
        return set;
    }
}
