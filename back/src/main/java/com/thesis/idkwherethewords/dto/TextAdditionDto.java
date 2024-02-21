package com.thesis.idkwherethewords.dto;

import com.thesis.idkwherethewords.entity.Text;
import com.thesis.idkwherethewords.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class TextAdditionDto {
    private Long id;
    private String text;
    private String src;
    private List<String> topics;

    public TextAdditionDto(Text text) {
        id = text.getId();
        this.text = text.getRawText();
        src = text.getSource();
        topics = text.getTopics().stream()
                .map(Topic::getBeautifiedName)
                .collect(Collectors.toList());
    }

    public TextAdditionDto() {
        id = 0L; text = ""; src = ""; topics = new ArrayList<>();
    }
}
