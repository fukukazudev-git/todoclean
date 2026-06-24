package com.example.todoclean.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import com.example.todoclean.dto.*;
import com.example.todoclean.service.TodoService;

@Controller
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    // コンストラクタインジェクション
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // 新規作成フォーム表示
    // フォームを準備して、HTMLテンプレートに渡す
    @GetMapping("/create")
    public String createForm(
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("form", new TodoCreateRequest());
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("filter", filter);
        model.addAttribute("page", page);
        return "todo/create";
    }

    // 新規作成処理呼び出し+画面遷移
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("form") TodoCreateRequest form,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sort", sort);
            model.addAttribute("order", order);
            model.addAttribute("keyword", keyword);
            model.addAttribute("filter", filter);
            model.addAttribute("page", page);
            return "todo/create"; // 入力画面に戻す
        }
        todoService.create(form);
        return "redirect:/todo?sort=" + sort +
                "&order=" + order +
                "&keyword=" + (keyword == null ? "" : keyword) +
                "&filter=" + filter +
                "&page=" + page;
    }

    // 一覧ページ表示
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<TodoDto> todoPage = todoService.getAll(sort, order, keyword, filter, page, size);

        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("filter", filter);
        model.addAttribute("todoPage", todoPage);
        model.addAttribute("today", LocalDate.now());
        return "todo/list";
    }

    // 編集ページを表示
    @GetMapping("/{id}/edit")
    // ModelはSpringが注入する。コントローラーからHTMLテンプレートに値を渡すためのオブジェクト
    public String editForm(
            @PathVariable Long id,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        TodoDetailResponse todo = todoService.getById(id);

        model.addAttribute("todo", todo); // HTMLテンプレートで使用する変数名"todo"と、渡す値todoを指定
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("filter", filter);
        model.addAttribute("page", page);
        return "todo/edit"; // templates/todo/edit.html を表示
    }

    // 更新処理
    @PutMapping("/{id}")
    // @ModelAttributeはリクエストパラメータをJavaオブジェクトにバインドするためのアノテーション
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("todo") TodoUpdateRequest form,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sort", sort);
            model.addAttribute("order", order);
            model.addAttribute("keyword", keyword);
            model.addAttribute("filter", filter);
            model.addAttribute("page", page);
            return "todo/edit"; // エラー時は編集画面に戻す
        }
        todoService.update(id, form);
        // 更新後はリダイレクトして一覧画面に遷移する
        // 理由は更新自体されたかわかりにくいのと同時更新を防ぐため
        return "redirect:/todo?sort=" + sort +
                "&order=" + order +
                "&keyword=" + (keyword == null ? "" : keyword) +
                "&filter=" + filter +
                "&page=" + page;
    }

    // 削除とその後のリダイレクト
    @DeleteMapping("/{id}") // 画面遷移なので viod ではなく String を返す
    public String delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page) {
        todoService.delete(id);
        return "redirect:/todo?sort=" + sort +
                "&order=" + order +
                "&keyword=" + (keyword == null ? "" : keyword) +
                "&filter=" + filter +
                "&page=" + page;
    }

    @PostMapping("/delete-bulk")
    public String deleteBulk(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page) {
        if (ids != null && !ids.isEmpty()) {
            todoService.deleteAll(ids);
        }
        return "redirect:/todo?sort=" + sort +
                "&order=" + order +
                "&keyword=" + (keyword == null ? "" : keyword) +
                "&filter=" + filter +
                "&page=" + page;
    }

}
/*
 * memo
 * API(Application Programming Interface)
 * ソフトウェア同士が通信するためのインターフェース。
 * システムの内部構造を知らなくとも、定義されたルールでリクエストを送信するとレスポンスが返ってくる。
 * 
 * @RestController
 * REST API用のコントローラーを定義するアノテーション
 * 内部的には@Controllerと@ResponseBodyを組み合わせたものと同じ動作をする。
 * →戻り値がそのままHTTPレスポンスのボディとして返されるため、
 * HTMLテンプレートを使用せずJSONやテキストを直接返すことが可能。
 * 
 * @PathVariable
 * URLの一部を変数として受け取るためのアノテーション
 * 例えば、URLが/todo/{id}の場合、{id}の部分を変数として受け取ることができる。
 * この変数はメソッドの引数として使用され、リクエストされたURLに応じて値が動的に変わる。
 * 
 * @RequestParam
 * リクエストパラメータをコントローラーメソッドの引数にバインドするためのアノテーション。
 * パラメータの型がString以外の場合、自動的に型変換が行われる。
 */