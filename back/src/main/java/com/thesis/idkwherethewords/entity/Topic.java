package com.thesis.idkwherethewords.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Table
@Getter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "beautified_name")
    private String beautifiedName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return name.equals(topic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
