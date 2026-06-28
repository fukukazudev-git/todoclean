package com.example.todoclean.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

//DBのテーブルになる
@Entity
public class TodoEntity {

    // Hibernateのアクセス戦略は最初に見つかった永続化アノテーションの位置で決まる
    // プロパティアクセスの方針でいく場合、フィールドにアノテーションは付けず、getterにつける
    // IDを主キーとして自動生成する
    @Id
    // 戦略
    // IDENTITY : DBのAUTO_INCREMENTに任せる → MySQL・PostgreSQL単体向け
    // INSERT後にDBからIDを取得する仕様のため、、一括INSERTのパフォーマンスが低下する欠点がある
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Boolean done;

    @Version
    private Long version = 0L; // 楽観的ロックのためのバージョンフィールド

    // updatable=falseで、更新時に作成日時が変更されないようにする
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDate dueDate;

    protected TodoEntity() {
    }

    public TodoEntity(String title, String description, Boolean done, LocalDateTime createdAt, LocalDate dueDate) {
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

    // boolean型のgetterはisXxxが正しい
    // 一方で、Boolean(ラッパー型)の getterはgetXxx()
    // HibernateはJavaBeans規約に非常に敏感でgetter/setterの命名が崩れるとプロパティ解析が壊れる
    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
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