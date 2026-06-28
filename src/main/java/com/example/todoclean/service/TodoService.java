package com.example.todoclean.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.example.todoclean.dto.*;
import com.example.todoclean.entity.TodoEntity;
import com.example.todoclean.exception.TodoNotFoundException;
import com.example.todoclean.repository.TodoRepository;

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

        // List: 順序を保持し、重複要素を許容する
        // Set: 順序を保証せず、重複要素を許容しない
        // of: immutableなSetを作成
        // contains: リスト内に特定の要素が存在するか確認
        if (!Set.of("title", "createdAt", "dueDate").contains(sortField))
            sortField = "dueDate";
        // Pageableは ページに関するリクエスト情報を持つインターフェース
        // PageRequestはその実装クラスで.ofでインスタンスを生成
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean isDone = "done".equals(filter);
        boolean isNotDone = "notdone".equals(filter);

        // Page<T> データ本体+ページ情報のレスポンス
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
                entity.isDone(),
                entity.getVersion(),
                entity.getDueDate());
    }

    // 更新処理
    @Transactional
    public void update(Long id, TodoUpdateRequest form) {

        TodoEntity entity = repository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        // WebアプリではHTTPリクエストをまたぐため明示的比較が必要
        // リクエストA(編集画面開く) findById → entity.version=1 → レスポンス返却 → エンティティ破棄
        // リクエストB(更新ボタン押下) findById → entity.version=?(DBから新規ロード)
        // リクエストをまたいだ時点でHibernateのスナップショットが消えるため、フォームからversion=1を受け取って比較する
        if (!entity.getVersion().equals(form.getVersion())) {
            throw new OptimisticLockingFailureException("他のユーザーが更新しました");
        }

        entity.setTitle(form.getTitle());
        entity.setDescription(form.getDescription());
        entity.setDone(form.getDone());
        entity.setDueDate(form.getDueDate());

    }

    // 削除処理
    // DBを更新するのでトランザクションを付ける
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

    // DTO変換をメソッド化
    private TodoDto toDto(TodoEntity e) {
        return new TodoDto(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.isDone(),
                e.getCreatedAt(),
                e.getDueDate());
    }
}