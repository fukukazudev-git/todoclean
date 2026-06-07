package com.example.todoclean.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

//DBのテーブルになる
@Entity
public class TodoEntity {
    
    // Hibernateのアクセス戦略は最初に見つかった永続化アノテーションの位置で決まる
    // プロパティアクセスの方針でいく場合、フィールドにアノテーションは付けず、getterにつける
    //IDを主キーとして自動生成する設定
    @Id
    //ID生成の戦略をIDENTITYに設定することで、データベースが自動的にIDを生成するようになる
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Boolean done;

    @Column(name = "opt_lock_version")
    @Version
    private Long version=0L; //楽観的ロックのためのバージョンフィールド

    // updatable=falseで、更新時に作成日時が変更されないようにする
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected TodoEntity(){}
    
    public TodoEntity(String title, String description, Boolean done, LocalDateTime createdAt){
        this.title = title;
        this.description = description;
        this.done = done;
        this.createdAt = createdAt;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    // boolean型のgetterはisXxxが正しい
    // 一方で、Boolean(ラッパー型)の getterはgetXxx()
    // HibernateはJavaBeans規約に非常に敏感でgetter/setterの命名が崩れるとプロパティ解析が壊れる
    public Boolean getDone(){return done;}
    public void setDone(Boolean done){this.done = done;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public Long getVersion(){return version;}
    public void setVersion(Long version){this.version = version;}
    
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt;}

}