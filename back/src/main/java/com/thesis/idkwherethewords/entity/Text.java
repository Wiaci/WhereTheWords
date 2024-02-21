package com.thesis.idkwherethewords.entity;

import com.thesis.idkwherethewords.entity.enumerated.LanguageLevel;
import com.thesis.idkwherethewords.entity.enumerated.TextStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table
@Data
@NoArgsConstructor
public class Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_text")
    private String rawText;

    @Enumerated(EnumType.STRING)
    private TextStatus status;

    private String source;
    private Date date;

    @Enumerated(EnumType.STRING)
    private LanguageLevel level;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "text_topics",
            joinColumns = @JoinColumn(name = "text_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> topics;

    public Text(String rawText, String source, List<Topic> topics, TextStatus status) {
        this.rawText = rawText;
        this.status = status;
        this.source = source;
        date = new Date();
        this.topics = topics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text = (Text) o;
        return rawText.equals(text.rawText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawText);
    }
}
