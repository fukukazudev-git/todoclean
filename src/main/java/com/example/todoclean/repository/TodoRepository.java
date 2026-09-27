package com.example.todoclean.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todoclean.entity.TodoEntity;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    Page<TodoEntity> findByTitleContaining(String keyword, Pageable pageable);

    Page<TodoEntity> findByDone(boolean done, Pageable pageable);

    Page<TodoEntity> findByDoneAndTitleContaining(boolean done, String keyword, Pageable pageable);
}