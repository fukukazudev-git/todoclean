package com.example.todoclean.entity;

import jakarta.persistence.*;

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
    private Boolean done;

    @Column(name = "opt_lock_version")
    @Version
    private Long optLockVersion=0L; //楽観的ロックのためのバージョンフィールド

    protected TodoEntity(){}
    
    public TodoEntity(String title, Boolean done){
        this.title = title;
        this.done = done;
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

    public Long getOptLockVersion(){return optLockVersion;}
    
}