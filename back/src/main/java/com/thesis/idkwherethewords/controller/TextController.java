package com.thesis.idkwherethewords.controller;

import com.thesis.idkwherethewords.dto.TextAdditionDto;
import com.thesis.idkwherethewords.dto.TextDto;
import com.thesis.idkwherethewords.entity.Text;
import com.thesis.idkwherethewords.service.ClassificationService;
import com.thesis.idkwherethewords.service.NlpService;
import com.thesis.idkwherethewords.service.TextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TextController {

    private final TextService textService;
    private final NlpService nlpService;
    private final ClassificationService classificationService;

    @PostMapping("/addtext")
    public ResponseEntity<Object> addText(@RequestBody TextAdditionDto textAdditionDto) {
        textService.addNewTextWithTopics(textAdditionDto);
        return new ResponseEntity<>(HttpEntity.EMPTY, HttpStatus.OK);
    }

    @PostMapping("/refusetext")
    public void refuseText(@RequestParam Long id) {
        textService.deleteTextWithId(id);
    }

    @PostMapping("/suggestText")
    public void suggestText(@RequestBody TextAdditionDto textAdditionDto) {
        textService.suggestText(textAdditionDto, classificationService.getTextTopics(textAdditionDto.getText()));
    }

    @GetMapping("/suggestText")
    public ResponseEntity<TextAdditionDto> getText() {
        return new ResponseEntity<>(textService.getSuggestedText(), HttpStatus.OK);
    }

    @PostMapping("/getTexts")
    public ResponseEntity<List<TextDto>> getTextsForUser(@RequestParam Long id, @RequestParam Long count) {
        List<Text> t = textService.getTextsForUser(id, count);
        List<TextDto> texts = t.stream()
                .map(text -> new TextDto(text, nlpService.extractWordsFromText(text),
                        nlpService.getFirstSentence(text)))
                .collect(Collectors.toList());
        return new ResponseEntity<>(texts, HttpStatus.OK);
    }
}
