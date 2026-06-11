package com.example.todoclean.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import com.example.todoclean.dto.*;
import com.example.todoclean.entity.TodoEntity;
import com.example.todoclean.exception.TodoNotFoundException;
import com.example.todoclean.repository.TodoRepository;

import jakarta.persistence.OptimisticLockException;

@Service
@Transactional  //更新処理が途中で失敗した場合でもロールバック
public class TodoService {
    
    private final TodoRepository repository;

    public TodoService(TodoRepository repository){
        this.repository = repository;
    }

    //登録処理(保存)
    public void create(TodoCreateRequest request){
    TodoEntity entity = new TodoEntity(request.getTitle(), request.getDescription(), request.getDone(), LocalDateTime.now());
    repository.save(entity);
    }

    // 全件取得
    // Sort を組み立てる
    public List<TodoDto> getAll(String sortField, String order, String keyword, String filter){ 
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortField);

        List<TodoEntity> entities;
        
        // 1.フィルタリング (done)
        if ("done".equals(filter)){
            entities = repository.findByDone(true, sort);
        } else if ("notdone".equals(filter)){
            entities = repository.findByDone(false, sort);
        } else {
            // 2.フィルタなし → 全件
            entities = repository.findAll(sort);
        }

        // 3.検索(keyword) がある場合はさらに絞り込み
        if(keyword != null && !keyword.isBlank()){
            entities = entities.stream()
                .filter(e -> e.getTitle().contains(keyword))
                .toList();
        }
        return entities.stream()
            .map(this::toDto)
            .toList();
    }

    //単独取得
    public TodoDetailResponse getById(Long id){
        TodoEntity entity = repository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));

        return new TodoDetailResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getDone(),
            entity.getVersion()
        );
    }

    //更新処理
    @Transactional
    public void update(Long id, TodoUpdateRequest form){

        //orElseThrow()は値が無い場合に例外を投げる
        TodoEntity entity = repository.findById(id)
               .orElseThrow(() -> new TodoNotFoundException(id));
        
        //同時更新防止
        if (!entity.getVersion().equals(form.getVersion())){
            throw new OptimisticLockException();
        }

        entity.setTitle(form.getTitle());
        entity.setDescription(form.getDescription());
        entity.setDone(form.getDone());

    }

    //削除処理
    //削除処理はDBを更新するのでトランザクションを付ける
    @Transactional
    public void delete(Long id){
        TodoEntity entity = repository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));
        repository.delete(entity);
    }
    
    //DTO変換をメソッド化、コードの重複を減らし、保守性も上げる
    private TodoDto toDto(TodoEntity e ){
        return new TodoDto(e.getId(), e.getTitle(), e.getDescription(), e.getDone(), e.getCreatedAt());
    }
}
