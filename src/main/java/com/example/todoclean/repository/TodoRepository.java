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
/*
 * memo
 * save() → INSERT/UPDATE
 * findAll() → SELECT
 * deleteById() → DELETE
 * Spring Data JPA がすべて自動で実装してくれるため、
 * 開発者はインターフェースを定義するだけで基本的なCRUD操作が利用可能になる。
 */