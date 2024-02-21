package com.thesis.idkwherethewords.entity;

import com.thesis.idkwherethewords.entity.enumerated.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "usr")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<Wordlist> words;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "fav_topics",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> favTopics;

    public User(String name, String password, Role role, List<Topic> favTopics) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.favTopics = favTopics;
    }
}
