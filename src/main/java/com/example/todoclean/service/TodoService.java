package com.example.todoclean.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    public Page<TodoDto> getAll(String sortField, String order, String keyword, String filter, int page, int size){ 
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<TodoEntity> entityPage;
        
        // 1.フィルタリング (done)
        if ("done".equals(filter)){
            entityPage = repository.findByDone(true, pageable);
        } else if ("notdone".equals(filter)){
            entityPage = repository.findByDone(false, pageable);
        } else {
            // 2.フィルタなし → 全件
            entityPage = repository.findAll(pageable);
        }

        // 3.検索(keyword) がある場合はページング後に絞り込む
        Page<TodoDto> dtoPage = entityPage.map(this::toDto);

        if(keyword != null && !keyword.isBlank()){
            List<TodoDto> filtered = dtoPage.getContent().stream()
                .filter(dto -> dto.getTitle().contains(keyword))
                .toList();

            return new PageImpl<>(filtered, pageable, filtered.size());
        }
        
        return dtoPage;
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
/*memo
Sort.Direction
Spring Data Core APIのSort.Directionは指定された方向でソートを実行するための列挙型を提供する。
- Sort.Direction.ASC: 昇順でソートを行うことを示す。
- Sort.Direction.DESC: 降順でソートを行うことを示す
Sort.by
引数で受ける並べ替えの規則(Direction)とプロパティ名を基に、Sortオブジェクトを生成するための静的メソッド。
*/