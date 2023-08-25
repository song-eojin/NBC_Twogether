package com.example.twogether.checklist.repository;

import com.example.twogether.checklist.entity.CheckList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckListRepository extends JpaRepository<CheckList, Long> {

    List<CheckList> findAllByCardId(Long cardId);

    void deleteAllByCard_Id(Long id);
}
