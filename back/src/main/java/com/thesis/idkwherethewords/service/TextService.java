package com.thesis.idkwherethewords.service;

import com.thesis.idkwherethewords.dto.TextAdditionDto;
import com.thesis.idkwherethewords.entity.*;
import com.thesis.idkwherethewords.entity.enumerated.LanguageLevel;
import com.thesis.idkwherethewords.entity.enumerated.TextStatus;
import com.thesis.idkwherethewords.repository.TextRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextService {

    private final TextRepo textRepo;
    private final NlpService nlpService;
    private final TopicService topicService;
    private final WordService wordService;
    private final UserService userService;

    public int i = 0;

    /*@PostConstruct
    public void reset() {
        List<Text> texts = textRepo.findAll();
        texts.forEach(text -> {
            i++;
            text.setLevel(nlpService.defineLanguageLevel(text));
            textRepo.save(text);
            if (i % 50 == 0) {
                System.out.println(i + "/" + texts.size());
            }
        });
    }*/

    public void suggestText(TextAdditionDto textAdditionDto, List<Topic> topics) {
        Text text = new Text(textAdditionDto.getText(),
                textAdditionDto.getSrc(), topics, TextStatus.SUGGESTED);
        text.setLevel(LanguageLevel.A1);
        textRepo.save(text);
    }

    public TextAdditionDto getSuggestedText() {
        Optional<Text> text = textRepo.findFirstByStatus(TextStatus.SUGGESTED);
        return text.map(TextAdditionDto::new).orElseGet(TextAdditionDto::new);
    }

    public void addNewTextWithTopics(TextAdditionDto textDto) {
        String rawText = textDto.getText();
        String src = textDto.getSrc();
        List<String> topics = textDto.getTopics();
        List<Topic> topicList = topicService.getTopicList(topics);

        Optional<Text> textOpt = textRepo.findById(textDto.getId());
        Text text = textOpt.orElseGet(() -> new Text(rawText, src, topicList, TextStatus.ADDED));
        text.setTopics(topicList);
        text.setStatus(TextStatus.ADDED);
        text.setRawText(rawText);
        text.setSource(src);
        if (textRepo.existsByRawTextAndStatus(rawText, TextStatus.ADDED)) return;

        List<WordInText> wordInTextList = nlpService.getWordsFromText(text);
        List<Word> words = new ArrayList<>();
        wordInTextList.forEach(w -> words.add(w.getPk().getWord()));
        Text savedText = textRepo.save(text);
        wordService.saveWordList(words);
        wordService.saveWordInTextList(wordInTextList);
        assignLanguageLevel(savedText);
    }

    public void deleteTextWithId(Long id) {
        textRepo.deleteById(id);
    }

    public void assignLanguageLevel(Text text) {
        text.setLevel(nlpService.defineLanguageLevel(text));
        textRepo.save(text);
    }

    public List<Text> getTextsForUser(Long userId, Long count) {
        User user = userService.getUserById(userId);
        List<Wordlist> words = user.getWords();
        List<WordInText> wordInTextList = wordService.getWordInTextsByWordlist(words.stream()
                .map(Wordlist::getWord)
                .collect(Collectors.toList()));
        Map<Text, Double> score = new HashMap<>();
        wordInTextList.forEach(wordInText -> {
            Text text = wordInText.getPk().getText();
            if (score.containsKey(text)) {
                score.put(text, score.get(text) + wordInText.getCount());
            } else score.put(text, (double) wordInText.getCount());
        });

        List<Topic> topics = user.getFavTopics();
        topics.forEach(topic -> {
            score.keySet().forEach(text -> {
                if (text.getTopics().contains(topic)) {
                    score.put(text, score.get(text)*1.5);
                }
            });
        });

        LanguageLevel level = wordService.getWordListLevel(words);
        score.keySet().forEach(text -> {
            if (text.getLevel().compareTo(level) > 0) {
                score.put(text, score.get(text)/1.5);
            }
        });

        List<Text> texts = new ArrayList<>(score.keySet()).stream()
                .sorted((t1, t2) -> score.get(t2).compareTo(score.get(t1)))
                .collect(Collectors.toList());
        if (texts.size() <= count) return texts;

        List<Text> selectedTexts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            selectedTexts.add(texts.get(i));
        }
        return selectedTexts;
    }

    public List<Text> findAll() {
        return textRepo.findAll();
    }
}
