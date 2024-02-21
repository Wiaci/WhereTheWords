package com.thesis.idkwherethewords.entity;

import com.thesis.idkwherethewords.entity.enumerated.WordStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class Wordlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Word word;

    @Enumerated(EnumType.STRING)
    private WordStatus status;

    public Wordlist(User user, Word word, WordStatus status) {
        this.user = user;
        this.word = word;
        this.status = status;
    }
}
