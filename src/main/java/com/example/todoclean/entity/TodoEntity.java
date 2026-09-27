package com.example.todoclean.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class TodoEntity {

    // IDを主キーとして自動生成する
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    // th:field のチェックボックスは未チェック時も hidden の _done により false が送信され
    // null にならないためプリミティブ型で定義
    private boolean done;

    // 楽観的ロックのためのバージョンフィールド
    @Version
    private Long version = 0L;

    // updatable=falseで、更新時に作成日時が変更されないようにする
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDate dueDate;

    protected TodoEntity() {
    }

    public TodoEntity(String title, String description, boolean done, LocalDateTime createdAt, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.done = done;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // プリミティブ boolean の getter は慣例通り isXxx() とする(Boolean ラッパー型なら getXxx())
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

}