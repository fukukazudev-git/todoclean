package com.example.todoclean.exception;

//Springの例外処理は非チェック例外を前提にしているため、RuntimeExceptionを継承
//チェック例外(Exception)にするとtry-catchが必要になり、Springの思想と逆行する
public class TodoNotFoundException extends RuntimeException{
    public TodoNotFoundException(Long id){
        super("Todo not found: " + id);
    }
}