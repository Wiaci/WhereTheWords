package com.thesis.idkwherethewords.repository;

import com.thesis.idkwherethewords.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepo extends JpaRepository<Topic, Long> {

    List<Topic> findAllByNameIn(List<String> names);
    List<Topic> findAllByBeautifiedNameIn(List<String> names);
    Topic getByName(String name);
}
