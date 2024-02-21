package com.thesis.idkwherethewords.service;

import com.thesis.idkwherethewords.dto.UserDetailsDto;
import com.thesis.idkwherethewords.dto.UserDto;
import com.thesis.idkwherethewords.entity.User;
import com.thesis.idkwherethewords.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final TopicService topicService;

    public User getUserByName(String username) {
        userRepo.existsByName(username);
        return userRepo.findByName(username);
    }
    public boolean userExistsByName(String username) { return userRepo.existsByName(username); }

    public UserDto authenticateUser(UserDetailsDto userDetails) {
        String username = userDetails.getUsername();
        String password = userDetails.getPassword();
        User user = getUserByName(username);
        if (user == null) return new UserDto();
        if (!encoder.matches(password, user.getPassword())) return new UserDto();
        String authToken = getAuthToken(username, password);
        return new UserDto(user, authToken);
    }

    public UserDto getUserDto(String username, String password) {
        User user = getUserByName(username);
        return new UserDto(user, getAuthToken(username, password));
    }

    public UserDto addUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        User actUser = userRepo.save(user);
        return new UserDto(actUser, getAuthToken(user.getName(), user.getPassword()));
    }

    public User getUserById(Long id) {
        return userRepo.findById(id).get();
    }

    public void updateTopicsForUserWithId(Long id, List<String> topics) {
        User user = userRepo.findById(id).orElse(null);
        user.setFavTopics(topicService.getTopicList(topics));
        userRepo.save(user);
    }

    private String getAuthToken(String username, String password) {
        return Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }
}