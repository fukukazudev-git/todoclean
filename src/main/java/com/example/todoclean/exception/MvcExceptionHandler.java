package com.example.todoclean.exception;

import jakarta.persistence.OptimisticLockException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.ui.Model;

@ControllerAdvice(basePackages = "com.example.todoclean.controller") //コントローラー層の例外を処理することを宣言
public class MvcExceptionHandler {

    @ExceptionHandler(TodoNotFoundException.class)
    public String handleTodoNotFound(Model model){
        model.addAttribute("errorMessage", "指定されたTodoは存在しません。");
        return "error/404";
    }

    @ExceptionHandler(OptimisticLockException.class)
    public String handleOptimisticLock(Model model){
        model.addAttribute("errorMessage", "ほかのユーザーが先に更新しました。再度編集してください。");
        return "error/409";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(Model model){
        model.addAttribute("errorMessage", "不正なリクエストが行われました。");
        return "error/400";
    }

}