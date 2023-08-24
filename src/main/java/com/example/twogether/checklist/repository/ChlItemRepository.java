package com.example.twogether.checklist.repository;

import com.example.twogether.checklist.entity.CheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChlItemRepository extends JpaRepository<CheckListItem, Long> {

}
