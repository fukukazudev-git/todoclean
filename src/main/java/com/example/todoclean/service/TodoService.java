package com.example.todoclean.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todoclean.dto.CreateTodoRequest;
import com.example.todoclean.dto.TodoCreateResponse;
import com.example.todoclean.dto.TodoDetailResponse;
import com.example.todoclean.dto.TodoDto;
import com.example.todoclean.dto.UpdateTodoRequest;
import com.example.todoclean.entity.TodoEntity;
import com.example.todoclean.exception.TodoNotFoundException;
import com.example.todoclean.repository.TodoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional  //更新処理が途中で失敗した場合でもロールバック
public class TodoService {
    
    private final TodoRepository repository;

    public TodoService(TodoRepository repository){
        this.repository = repository;
    }

    //登録処理
    public TodoCreateResponse create(CreateTodoRequest request){
    TodoEntity entity = new TodoEntity(request.getTitle(), request.getDone());
    TodoEntity saved = repository.save(entity);
    return new TodoCreateResponse(saved.getId(), saved.getTitle(), saved.getDone());
    }

    //全件取得
    public List<TodoDto> getAll(){
        //repository.findAll()はList<TodoEntity>を返す。これをList<TodoDto>に変換するためにストリームAPIを使用している
        return repository.findAll().stream() // findAllで全件取得、stream()で1件ずつ処理するモードへ
                .map(this::toDto)   //map()でEntity→DTOに変換する処理を適用
                .collect(Collectors.toList());  //ToList()でListに戻す
    }

    //単独取得
    public TodoDetailResponse getById(Long id){
        TodoEntity entity = repository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));

        return new TodoDetailResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getDone(),
            entity.getOptLockVersion()
        );
    }

    @PersistenceContext
    private EntityManager em;
    //更新処理
    @Transactional
    public TodoDetailResponse update(Long id, UpdateTodoRequest request){
        System.out.println("isActive: " + em.isJoinedToTransaction());


        //orElseThrow()は値が無い場合に例外を投げる
        TodoEntity entity = repository.findById(id)
               .orElseThrow(() -> new TodoNotFoundException(id));

        entity.setTitle(request.getTitle());
        entity.setDone(request.getDone());

        return new TodoDetailResponse(
            entity.getId(), 
            entity.getTitle(), 
            entity.getDone(), 
            entity.getOptLockVersion()
        );
    }

    //削除処理
    @Transactional
    public void delete(Long id){
        TodoEntity entity = repository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));
        repository.delete(entity);
    }
    
    //DTO変換をメソッド化、コードの重複を減らし、保守性も上げる
    private TodoDto toDto(TodoEntity e ){
        return new TodoDto(e.getId(), e.getTitle(), e.getDone());
    }
}
