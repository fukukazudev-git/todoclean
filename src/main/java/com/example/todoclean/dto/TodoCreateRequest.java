package com.example.todoclean.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// POST用 
public class TodoCreateRequest{

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 50, message = "タイトルは50文字以内で入力してください")
    private String title;

    private Boolean done;

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    
    public Boolean getDone(){return done;}
    public void setDone(Boolean done){this.done = done;}

}