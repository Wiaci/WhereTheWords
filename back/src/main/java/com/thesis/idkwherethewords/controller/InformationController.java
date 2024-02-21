package com.thesis.idkwherethewords.controller;

import com.thesis.idkwherethewords.dto.WordDto;
import com.thesis.idkwherethewords.dto.WordInfoDto;
import com.thesis.idkwherethewords.entity.Topic;
import com.thesis.idkwherethewords.service.TopicService;
import com.thesis.idkwherethewords.service.UserService;
import com.thesis.idkwherethewords.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class InformationController {

    private final TopicService topicService;
    private final WordService wordService;
    private final UserService userService;

    @GetMapping("/topics")
    public @ResponseBody List<String> getTopics() {
        return topicService.getTopicList().stream()
                .map(Topic::getBeautifiedName)
                .collect(Collectors.toList());
    }

    @GetMapping("/startWords")
    public ResponseEntity<List<WordInfoDto>>  getStartWords() {
        List<WordInfoDto> words = wordService.getStartWords();
        return new ResponseEntity<>(words, HttpStatus.OK);
    }

    @PostMapping("/updateTopics")
    public void updateTopics(@RequestParam Long id,
                             @RequestBody List<String> topics) {
        userService.updateTopicsForUserWithId(id, topics);
    }

    @PostMapping("/changeWordStatus")
    public void changeWordStatus(@RequestParam Long id,
                                 @RequestBody WordInfoDto wordDto) {
        wordService.updateWordlist(id, wordDto);
    }

    @PostMapping("/addWord")
    public ResponseEntity<WordInfoDto> addWord(@RequestParam Long id,
                                               @RequestBody WordInfoDto wordDto) {
        return new ResponseEntity<>(wordService.addWord(id, wordDto), HttpStatus.OK);
    }
}
