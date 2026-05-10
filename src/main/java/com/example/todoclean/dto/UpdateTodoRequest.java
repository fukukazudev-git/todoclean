package com.example.todoclean.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// PUT用 全項目必須パターン
public class UpdateTodoRequest {

    @NotBlank
    private String title;

    @NotNull
    private Boolean done;

    @NotNull
    private Long version;

    public UpdateTodoRequest(){}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    public Boolean getDone(){return done;}
    public void setDone(Boolean done){this.done = done;}

    public Long getVersion(){return version;}
    public void setVersion(Long version){this.version = version;}
}
