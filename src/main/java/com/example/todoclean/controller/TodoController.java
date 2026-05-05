package com.example.todoclean.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.todoclean.dto.CreateTodoRequest;
import com.example.todoclean.dto.TodoCreateResponse;
import com.example.todoclean.dto.TodoDetailResponse;
import com.example.todoclean.dto.TodoDto;
import com.example.todoclean.dto.UpdateTodoRequest;
import com.example.todoclean.service.TodoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    //新規作成
    @PostMapping("/todo")
    public TodoCreateResponse create(@Valid @RequestBody CreateTodoRequest request){
        return todoService.create(request);
    }
    //全件取得
    @GetMapping("/todo")
    public List<TodoDto> getTodos() {
        return todoService.getAll();
    }

    //単独取得
    @GetMapping("/todo/{id}")
    public TodoDetailResponse getById(@PathVariable Long id){
        return todoService.getById(id);
    }

    //削除
    @DeleteMapping("/todo/{id}")  //{id}はURLの一部を変数として受け取る
    public void delete(@PathVariable Long id){
        todoService.delete(id);
    }

    //更新処理
    @PutMapping("/todo/{id}")
    public TodoDetailResponse update(
        @PathVariable Long id ,
        @Valid @RequestBody UpdateTodoRequest request
    ){
        return todoService.update(id, request);
    }
}
/*memo
API(Application Programming Interface)
ソフトウェア同士が通信するためのインターフェース。
システムの内部構造を知らなくとも、定義されたルールでリクエストを送信するとレスポンスが返ってくる。

@RestController 
REST API用のコントローラーを定義するアノテーション
内部的には@Controllerと@ResponseBodyを組み合わせたものと同じ動作をする。
→戻り値がそのままHTTPレスポンスのボディとして返されるため、
HTMLテンプレートを使用せずJSONやテキストを直接返すことが可能。

@GetMapping
特定のURLに対するHTTP GETリクエストを処理するために使用される。
これにより、URLとメソッドを簡潔にマッピングすることが可能。
定義した該当メソッドはHttpimport jakarta.validation.Valid;Methodの中のGetrequestに対応する。

@PostMapping
HTTPのPOSTリクエストを受け付けるアノテーション
フロントエンドからのデータ送信、
REST APIからの登録処理などを実装する際に使用される。

@DeleteMapping
DELETEリクエストを受け取るためのアノテーション
リソースの削除処理を実装する際に使用される。

@PathVariable
URLの一部を変数として受け取るためのアノテーション
例えば、URLが/todo/{id}の場合、{id}の部分を変数として受け取ることができる。
この変数はメソッドの引数として使用され、リクエストされたURLに応じて値が動的に変わる。

@PutMapping
更新用のHTTPリクエストを処理するためのアノテーション
リソースの更新処理を実装する際に使用される。

@RequestBody
JSON形式のリクエストボディをJavaオブジェクトに変換して受け取るためのアノテーション
フロントエンドから送信されたデータをサーバー側で処理する際に使用される。

@Valid
バリデーションを有効にするためのアノテーション
*/