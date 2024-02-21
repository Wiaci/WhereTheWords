package com.thesis.idkwherethewords.service;

import com.thesis.idkwherethewords.dto.WordDto;
import com.thesis.idkwherethewords.dto.WordInfoDto;
import com.thesis.idkwherethewords.entity.Text;
import com.thesis.idkwherethewords.entity.Topic;
import com.thesis.idkwherethewords.entity.Word;
import com.thesis.idkwherethewords.entity.WordInText;
import com.thesis.idkwherethewords.entity.enumerated.LanguageLevel;
import com.thesis.idkwherethewords.entity.enumerated.PartOfSpeech;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NlpService {

    private StanfordCoreNLP stanfordCoreNLP;
    private final WordService wordService;
    private final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList("i", "me", "my", "myself", "we", "our", "ours",
            "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her",
            "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which",
            "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or",
            "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into",
            "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on",
            "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how",
            "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only",
            "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now", "many",
            "much", "anyone", "nobody"));

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        String propertiesName = "tokenize, ssplit, pos, lemma";
        properties.setProperty("annotators", propertiesName);
        stanfordCoreNLP = new StanfordCoreNLP(properties);
    }

    public List<WordInText> getWordsFromText(Text text) {
        CoreDocument coreDocument = new CoreDocument(text.getRawText());
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();

        List<WordInText> words = new ArrayList<>();

        coreLabels.forEach(coreLabel -> {
            String lemma = coreLabel.lemma();
            String posLabel = coreLabel.getString(CoreAnnotations.PartOfSpeechAnnotation.class);
            PartOfSpeech pos = posByPosLabel(posLabel);
            if (!wordIsIgnorable(lemma, pos, posLabel)) {
                WordInText word = new WordInText(new Word(lemma, pos, LanguageLevel.C2), text);
                int i = words.indexOf(word);

                if (i < 0) words.add(word);
                else words.get(i).countInc();
            }
        });
        return words;
    }

    public LanguageLevel defineLanguageLevel(Text text) {
        CoreDocument coreDocument = new CoreDocument(text.getRawText());
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();
        Map<LanguageLevel, Integer> levelMap = prepareLevelMap();
        for (CoreLabel coreLabel : coreLabels) {
            String lemma = coreLabel.lemma();
            String posLabel = coreLabel.getString(CoreAnnotations.PartOfSpeechAnnotation.class);
            PartOfSpeech pos = posByPosLabel(posLabel);

            LanguageLevel level = wordIsIgnorable(lemma, pos, posLabel) ?
                    LanguageLevel.A1 : wordService.getWordLevel(lemma, pos);
            levelMap.put(level, levelMap.get(level) + 1);
        }
        return defineLanguageLevelByMap(levelMap);
    }

    public List<WordDto> extractWordsFromText(Text text) {
        text.setRawText(text.getRawText().replace("\n\n", "|"));
        CoreDocument coreDocument = new CoreDocument(text.getRawText());
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();
        List<WordDto> words = new ArrayList<>();
        for (CoreLabel coreLabel : coreLabels) {
            String lemma = coreLabel.lemma();
            String posLabel = coreLabel.getString(CoreAnnotations.PartOfSpeechAnnotation.class);
            PartOfSpeech pos = posByPosLabel(posLabel);
            if (wordIsIgnorable(lemma, pos, posLabel)) {
                words.add(new WordDto(coreLabel.originalText(), lemma, "ignore", "A1"));
            } else {
                words.add(new WordDto(coreLabel.originalText(), new WordInfoDto(
                        wordService.findByBaseFormAndPartOfSpeech(lemma, pos))));
            }
        }
        words.forEach(word -> {
            System.out.println(word.getOriginal());
            if (word.getOriginal().equals("|")) word.setOriginal("\n");
        });
        return deletePunctuation(words);
    }

    public String getFirstSentence(Text text) {
        CoreDocument coreDocument = new CoreDocument(text.getRawText());
        stanfordCoreNLP.annotate(coreDocument);
        return coreDocument.sentences().get(0).toString();
    }

    private List<WordDto> deletePunctuation(List<WordDto> words) {
        for (int i = words.size() - 1; i > 0; i--) {
            String w = words.get(i).getOriginal();
            if (w.matches("^[\\.|,|!|\\?|’|\']") || w.matches("^n’") || w.matches("^n\'")) {
                words.get(i-1).setOriginal(words.get(i-1).getOriginal() + w);
                words.remove(i);
            }
        }
        return words;
    }

    private PartOfSpeech posByPosLabel(String posLabel) {
        return switch (posLabel.charAt(0)) {
            case 'J' -> PartOfSpeech.ADJECTIVE;
            case 'N' -> PartOfSpeech.NOUN;
            case 'R' -> PartOfSpeech.ADVERB;
            case 'V' -> PartOfSpeech.VERB;
            default -> null;
        };
    }

    private Map<LanguageLevel, Integer> prepareLevelMap() {
        Map<LanguageLevel, Integer> levelMap = new HashMap<>();
        for (LanguageLevel level : LanguageLevel.values()) {
            levelMap.put(level, 0);
        }
        return levelMap;
    }

    private boolean wordIsIgnorable(String wordLemma, PartOfSpeech pos, String posLabel) {
        return pos == null || STOP_WORDS.contains(wordLemma) ||
                posLabel.startsWith("NNP") || !wordLemma.matches("^[a-z]+$");
    }

    private LanguageLevel defineLanguageLevelByMap(Map<LanguageLevel, Integer> levelMap) {
        int total = 0;
        for (LanguageLevel level : LanguageLevel.values()) {
            total += levelMap.get(level);
        }
        Map<LanguageLevel, Double> percentageMap = new HashMap<>();
        for (LanguageLevel level : LanguageLevel.values()) {
            percentageMap.put(level, (double) levelMap.get(level) / (double) total);
        }
        double sum = percentageMap.get(LanguageLevel.A1);
        if (sum > 0.95) return LanguageLevel.A1;
        sum += percentageMap.get(LanguageLevel.A2);
        if (sum > 0.95) return LanguageLevel.A2;
        sum += percentageMap.get(LanguageLevel.B1);
        if (sum > 0.95) return LanguageLevel.B1;
        sum += percentageMap.get(LanguageLevel.B2);
        if (sum > 0.95) return LanguageLevel.B2;
        sum += percentageMap.get(LanguageLevel.C1);
        if (sum > 0.95) return LanguageLevel.C1;
        return LanguageLevel.C2;
    }
}