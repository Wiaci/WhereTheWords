package com.thesis.idkwherethewords.repository;

import com.thesis.idkwherethewords.entity.Text;
import com.thesis.idkwherethewords.entity.enumerated.TextStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TextRepo extends JpaRepository<Text, Long> {

    boolean existsByRawTextAndStatus(String text, TextStatus status);
    Optional<Text> findFirstByStatus(TextStatus status);
}
