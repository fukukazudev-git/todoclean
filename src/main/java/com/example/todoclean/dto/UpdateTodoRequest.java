package com.example.todoclean.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// PUT用 全項目必須パターン
public class UpdateTodoRequest {

    @NotBlank
    private String title;

    @NotNull
    private Boolean done;

    public UpdateTodoRequest(){}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    public Boolean getDone(){return done;}
    public void setDone(Boolean done){this.done = done;}
    
}
