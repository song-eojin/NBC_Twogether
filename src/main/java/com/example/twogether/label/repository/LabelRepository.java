package com.example.twogether.label.repository;

import com.example.twogether.label.entity.Label;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {

    List<Label> findAllByBoard_Id(Long boardId);

    List<Label> findAllByTitle(String title);
}
