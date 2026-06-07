package com.example.todoclean.repository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.example.todoclean.entity.TodoEntity;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
List<TodoEntity> findByTitleContaining(String keyword, Sort sort);
}
/*memo
save() → INSERT/UPDATE
findAll() → SELECT
deleteById() → DELETE
Spring Data JPA がすべて自動で実装してくれるため、
開発者はインターフェースを定義するだけで基本的なCRUD操作が利用可能になる。
 */