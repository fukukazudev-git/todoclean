package com.example.todoclean.exception;

// @Transactional は既定で非チェック例外の時のみロールバックするため、RuntimeExceptionを継承
public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(Long id) {
        super("Todo not found: " + id);
    }
}