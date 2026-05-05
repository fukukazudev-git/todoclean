package com.example.todoclean.exception;

import java.util.List;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//アプリ全体の例外を扱うことを宣言
// @RestControllerAdvice は @ControllerAdvice + @ResponseBody
// ResponseEntityを返す背系になっている場合は下記の実装でよい→ ResponseEntityは常にJSONを返すから
@ControllerAdvice
public class GlobalExceptionHandler {

    //Service層でTodoNotFoundExceptionが発生した場合の処理を定義
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTodoNotFound(
        TodoNotFoundException ex,
        HttpServletRequest request
    ){

        // ErrorResponseクラスのコンストラクタが必要とする引数を渡してインスタンスを作成
        ErrorResponse body = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(), // status 404
            HttpStatus.NOT_FOUND.getReasonPhrase(),  // error "Not Found"
            ex.getMessage(), // message "Todo not found: {id}"
            request.getRequestURI(),  // path "/todos/{id}"
            null // 404エラーでは存在し得ないためnull
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ){
        List<FieldValidationError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> new FieldValidationError(
                err.getField(),
                err.getDefaultMessage()
            ))
            .toList();
        
        ErrorResponse body = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Validation field",
            request.getRequestURI(),
            fieldErrors
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(
            OptimisticLockException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(), // Conflict
                HttpStatus.CONFLICT.getReasonPhrase(),
                "他のユーザーが先に更新しました。",
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

}
/*HttpServletRequest Spring MVCが内部で生成するHTTPリクエスト情報の入れ物。
URL、ヘッダ、メソッド、ボディ等の情報を保持している。
getRequestURI() リクエストされたURLのパス部分を返すメソッド。
err.getField() HTTPリクエストボディのどのフィールドがバリデーションに失敗したか表示する。
getDefaultMessage() そのバリデーションアノテーションが持つデフォルトのエラーメッセージを表示する。
*/ 