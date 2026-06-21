package com.example.todoclean.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.example.todoclean.dto.*;
import com.example.todoclean.entity.TodoEntity;
import com.example.todoclean.exception.TodoNotFoundException;
import com.example.todoclean.repository.TodoRepository;

import jakarta.persistence.OptimisticLockException;

@Service
@Transactional // 更新処理が途中で失敗した場合でもロールバック
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    // 登録処理(保存)
    public void create(TodoCreateRequest request) {
        TodoEntity entity = new TodoEntity(request.getTitle(), request.getDescription(), request.getDone(),
                LocalDateTime.now(), request.getDueDate());
        repository.save(entity);
    }

    // 全件取得
    public Page<TodoDto> getAll(
            String sortField,
            String order,
            String keyword,
            String filter,
            int page,
            int size) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

        if (!Set.of("title", "createdAt", "dueDate").contains(sortField)) sortField = "dueDate";
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean isDone = "done".equals(filter);
        boolean isNotDone = "notdone".equals(filter);

        Page<TodoEntity> entityPage;
        if (hasKeyword && isDone)
            entityPage = repository.findByDoneAndTitleContaining(true, keyword, pageable);
        else if (hasKeyword && isNotDone)
            entityPage = repository.findByDoneAndTitleContaining(false, keyword, pageable);
        else if (hasKeyword)
            entityPage = repository.findByTitleContaining(keyword, pageable);
        else if (isDone)
            entityPage = repository.findByDone(true, pageable);
        else if (isNotDone)
            entityPage = repository.findByDone(false, pageable);
        else
            entityPage = repository.findAll(pageable);

        return entityPage.map(this::toDto);
    }

    // 単独取得
    public TodoDetailResponse getById(Long id) {
        TodoEntity entity = repository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        return new TodoDetailResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDone(),
                entity.getVersion(),
                entity.getDueDate());
    }

    // 更新処理
    @Transactional
    public void update(Long id, TodoUpdateRequest form) {

        // orElseThrow()は値が無い場合に例外を投げる
        TodoEntity entity = repository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        // 同時更新防止
        if (!entity.getVersion().equals(form.getVersion())) {
            throw new OptimisticLockException();
        }

        entity.setTitle(form.getTitle());
        entity.setDescription(form.getDescription());
        entity.setDone(form.getDone());
        entity.setDueDate(form.getDueDate());

    }

    // 削除処理
    // 削除処理はDBを更新するのでトランザクションを付ける
    @Transactional
    public void delete(Long id) {
        TodoEntity entity = repository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        repository.delete(entity);
    }

    // 一括削除処理
    @Transactional
    public void deleteAll(List<Long> ids) {
        repository.deleteAllById(ids); // JpaRepositoryに既存メソッド有
    }

    // DTO変換をメソッド化、コードの重複を減らし、保守性も上げる
    private TodoDto toDto(TodoEntity e) {
        return new TodoDto(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getDone(),
                e.getCreatedAt(),
                e.getDueDate());
    }
}
/*
 * memo
 * Sort.Direction
 * Spring Data Core APIのSort.Directionは指定された方向でソートを実行するための列挙型を提供する。
 * - Sort.Direction.ASC: 昇順でソートを行うことを示す。
 * - Sort.Direction.DESC: 降順でソートを行うことを示す
 * Sort.by
 * 引数で受ける並べ替えの規則(Direction)とプロパティ名を基に、Sortオブジェクトを生成するための静的メソッド。
 */