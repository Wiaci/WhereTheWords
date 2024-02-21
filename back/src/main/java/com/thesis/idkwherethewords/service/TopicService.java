package com.thesis.idkwherethewords.service;

import com.thesis.idkwherethewords.entity.Topic;
import com.thesis.idkwherethewords.repository.TopicRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepo topicRepo;

    public List<Topic> getTopicList(List<String> topics) {
        return topicRepo.findAllByBeautifiedNameIn(topics);
    }
    public List<Topic> getTopicListByNames(List<String> names) {
        return topicRepo.findAllByNameIn(names);
    }
    public List<Topic> getTopicList() {
        return topicRepo.findAll();
    }
    public Topic getTopicByName(String name) { return topicRepo.getByName(name); }
}
