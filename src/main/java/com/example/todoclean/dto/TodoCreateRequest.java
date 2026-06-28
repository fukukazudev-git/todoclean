package com.example.todoclean.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

// POST用 
public class TodoCreateRequest {

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 50, message = "タイトルは50文字以内で入力してください")
    private String title;

    @Size(max = 200, message = "詳細は200文字以内で入力してください")
    private String description;

    private Boolean done;

    // iso: ISO標準フォーマットを使用
    // ISO.DATE: ISO-8601(yyyy-MM-dd)の日付形式として設定
    @NotNull(message = "期限日は必須です")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

}