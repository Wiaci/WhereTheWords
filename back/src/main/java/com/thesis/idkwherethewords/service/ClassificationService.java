package com.thesis.idkwherethewords.service;

import com.aliasi.classify.*;
import com.aliasi.lm.LanguageModel;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.stats.MultivariateDistribution;
import com.aliasi.stats.MultivariateEstimator;
import com.aliasi.util.AbstractExternalizable;
import com.thesis.idkwherethewords.dto.TextAdditionDto;
import com.thesis.idkwherethewords.entity.Text;
import com.thesis.idkwherethewords.entity.Topic;
import com.thesis.idkwherethewords.entity.enumerated.TextStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassificationService {

    private final TextService textService;
    private final TopicService topicService;
    private LMClassifier<LanguageModel, MultivariateDistribution> classifier;
    private Set<String> topics = new HashSet<>();
    private final boolean NEED_TO_TRAIN = false;

    @PostConstruct
    public void trainModel() throws IOException, ClassNotFoundException {
        if (NEED_TO_TRAIN) {
            Map<String, String> textsMap = getTextTopicMap("C:\\Users\\vyach\\Downloads\\folder\\20_newsgroups_proc");
            DynamicLMClassifier<NGramProcessLM> newClassifier = DynamicLMClassifier
                .createNGramProcess(topics.toArray(new String[0]), 8);
            textsMap.forEach((text, topic) -> {
                //System.out.println("Text " + text.substring(0, Math.min(10, text.length())) + " topic " + topic);
                Classification classification = new Classification(topic);
                Classified<CharSequence> classified = new Classified<>(text, classification);
                newClassifier.handle(classified);
                /*i++;
                if (i % 50 == 0) {
                    System.out.println(i + "/" + textsMap.size());
                }*/
            });
            AbstractExternalizable.compileTo(
                newClassifier,
                new File("model\\classifier.dat"));
            //addAllToDatabase(textsMap);
        }
        classifier = (LMClassifier<LanguageModel, MultivariateDistribution>) AbstractExternalizable
            .readObject(new File("model\\classifier.dat"));
    }

    /*private void addAllToDatabase(Map<String, String> textsMap) {
        textsMap.forEach((text, topic) -> {
            Text txt = new Text(
                text,
                "20_newsgroups",
                Arrays.asList(topicService.getTopicByName(topic)),
                TextStatus.ADDED
            );
            TextAdditionDto textAdditionDto = new TextAdditionDto(txt);
            textService.suggestText(textAdditionDto, new ArrayList<>());
            textAdditionDto.setId(getId());
            textService.addNewTextWithTopics(textAdditionDto);
        });
    }*/

    /*private Long getId() {
        id++;
        return id;
    }*/

    public Map<String, String> getTextTopicMap(String folderPath) {
        Map<String, String> textTopicMap = new HashMap<>();

        File folder = new File(folderPath);
        File[] topicFolders = folder.listFiles(File::isDirectory);

        if (topicFolders != null) {
            for (File topicFolder : topicFolders) {
                String topic = topicFolder.getName();
                topics.add(topic);
                File[] files = topicFolder.listFiles();

                if (files != null) {
                    for (File file : files) {
                        try {
                            String text = new String(Files.readAllBytes(file.toPath()));
                            textTopicMap.put(text, topic);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return textTopicMap;
    }

    public List<Topic> getTextTopics(String text) {
        JointClassification jc = classifier.classify(text);

        System.out.println(text.substring(0, 20));
        topics.stream()
                .filter(topic -> jc.conditionalProbability(topic) > 0)
                .sorted((s1, s2) -> Double.compare(jc.conditionalProbability(s2), jc.conditionalProbability(s1)))
                .forEach(s -> System.out.println(s + " " + jc.conditionalProbability(s)));


        List<String> selectedTopics = topics.stream()
                .filter(topic -> jc.conditionalProbability(topic) > 0)
                .sorted((s1, s2) -> Double.compare(jc.conditionalProbability(s2), jc.conditionalProbability(s1)))
                .limit(3)
                .collect(Collectors.toList());
        return topicService.getTopicListByNames(selectedTopics);
    }
}
