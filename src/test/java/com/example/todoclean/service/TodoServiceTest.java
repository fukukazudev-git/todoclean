package com.example.todoclean.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.todoclean.dto.TodoCreateRequest;
import com.example.todoclean.dto.TodoDetailResponse;
import com.example.todoclean.dto.TodoDto;
import com.example.todoclean.dto.TodoUpdateRequest;
import com.example.todoclean.entity.TodoEntity;
import com.example.todoclean.exception.TodoNotFoundException;
import com.example.todoclean.repository.TodoRepository;

/**
 * Service層の単体テスト。
 * DBやSpringコンテナは起動せず、Repositoryを「モック（偽物）」に差し替えて、
 * TodoServiceのロジック（分岐・例外）だけを高速に検証する。
 * 出口がJSONか画面かに関係なく成立する、最も価値の高いテスト層。
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository repository; // 偽物のリポジトリ。戻り値はテスト側で指定する

    @InjectMocks
    private TodoService service; // モックを注入した検証対象

    // ヘルパー: テスト用エンティティを作る
    private TodoEntity entity(String title) {
        return new TodoEntity(title, "desc", false, LocalDateTime.now(), LocalDate.of(2026, 7, 1));
    }

    @Test
    void create_リポジトリのsaveが呼ばれる() {
        TodoCreateRequest req = new TodoCreateRequest();
        req.setTitle("買い物");
        req.setDescription("牛乳");
        req.setDone(false);
        req.setDueDate(LocalDate.of(2026, 7, 1));

        service.create(req);

        // saveに渡されたエンティティのタイトルが正しいことを検証
        verify(repository).save(any(TodoEntity.class));
    }

    @Test
    void getAll_キーワードと完了フィルタの両方で専用クエリが呼ばれる() {
        Page<TodoEntity> page = new PageImpl<>(List.of(entity("買い物")));
        when(repository.findByDoneAndTitleContaining(eq(true), eq("買"), any(Pageable.class)))
                .thenReturn(page);

        Page<TodoDto> result = service.getAll("dueDate", "asc", "買", "done", 0, 10);

        // 期待した分岐（done=true かつ keyword検索）のメソッドが呼ばれ、DTOに変換されている
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("買い物");
        verify(repository).findByDoneAndTitleContaining(eq(true), eq("買"), any(Pageable.class));
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getById_存在しないIDならTodoNotFoundExceptionを投げる() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void getById_存在すればDetailResponseに変換して返す() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity("買い物")));

        TodoDetailResponse res = service.getById(1L);

        assertThat(res.getTitle()).isEqualTo("買い物");
        assertThat(res.getDone()).isFalse();
    }

    @Test
    void update_バージョン不一致なら楽観ロック例外を投げる() {
        TodoEntity stored = entity("買い物"); // version は初期値 0
        when(repository.findById(1L)).thenReturn(Optional.of(stored));

        TodoUpdateRequest form = new TodoUpdateRequest();
        form.setTitle("買い物（更新）");
        form.setDueDate(LocalDate.of(2026, 7, 1));
        form.setVersion(5L); // 画面が持っていた古い/食い違うバージョン

        assertThatThrownBy(() -> service.update(1L, form))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void markDoneAll_対象エンティティのdoneがtrueになる() {
        TodoEntity a = entity("買い物"); // done は初期値 false
        TodoEntity b = entity("掃除");
        when(repository.findAllById(List.of(1L, 2L))).thenReturn(List.of(a, b));

        service.markDoneAll(List.of(1L, 2L));

        // JPAの変更検知で更新されるため、取得したエンティティの done が書き換わっていることを検証
        assertThat(a.isDone()).isTrue();
        assertThat(b.isDone()).isTrue();
    }

    @Test
    void update_バージョン一致ならエンティティが更新される() {
        TodoEntity stored = entity("買い物"); // version 0
        when(repository.findById(1L)).thenReturn(Optional.of(stored));

        TodoUpdateRequest form = new TodoUpdateRequest();
        form.setTitle("買い物（更新）");
        form.setDescription("卵も");
        form.setDone(true);
        form.setVersion(0L); // 一致
        form.setDueDate(LocalDate.of(2026, 8, 1));

        service.update(1L, form);

        // JPAの変更検知で更新されるため、エンティティの中身が書き換わっていることを検証
        assertThat(stored.getTitle()).isEqualTo("買い物（更新）");
        assertThat(stored.isDone()).isTrue();
        assertThat(stored.getDueDate()).isEqualTo(LocalDate.of(2026, 8, 1));
    }
}
