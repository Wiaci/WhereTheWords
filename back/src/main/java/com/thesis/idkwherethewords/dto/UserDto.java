package com.thesis.idkwherethewords.dto;

import com.thesis.idkwherethewords.entity.Topic;
import com.thesis.idkwherethewords.entity.User;
import com.thesis.idkwherethewords.entity.Wordlist;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String role;
    private List<WordInfoDto> words;
    private List<String> topics;
    private String authToken;

    public UserDto() {
        id = -1L;
        username = "";
        role = "";
        words = new ArrayList<>();
        topics = new ArrayList<>();
        authToken = "";
    }

    public UserDto(User user, String authToken) {
        id = user.getId();
        username = user.getName();
        role = user.getRole().name();
        List<Wordlist> wordlist = user.getWords() == null ? new ArrayList<>() : user.getWords();
        words = wordlist.stream()
                .map(WordInfoDto::new)
                .collect(Collectors.toList());
        topics = user.getFavTopics().stream()
                .map(Topic::getBeautifiedName)
                .collect(Collectors.toList());
        this.authToken = authToken;
    }
}
