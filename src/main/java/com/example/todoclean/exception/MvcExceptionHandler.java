package com.example.todoclean.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;

@ControllerAdvice(basePackages = "com.example.todoclean.controller") // コントローラー層の例外を処理することを宣言
public class MvcExceptionHandler {

    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTodoNotFound(Model model) {
        model.addAttribute("errorMessage", "指定されたTodoは存在しません。");
        return "error/404";
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleOptimisticLockingFailure(Model model) {
        model.addAttribute("errorMessage", "ほかのユーザーが先に更新しました。再度編集してください。");
        return "error/409";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(Model model) {
        model.addAttribute("errorMessage", "不正なリクエストが行われました。");
        return "error/400";
    }

}