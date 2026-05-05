package com.example.todoclean.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// POST用 
public class CreateTodoRequest{

    @NotBlank
    private String title;

    //POST 時は done を送らせない設計も多い
    //必要なら @NotNullを付けてもよい
    @NotNull
    private Boolean done;

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    
    public Boolean getDone(){return done;}
    public void setDone(Boolean done){this.done = done;}

}