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

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

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
            return "todo/create";
        }
        todoService.create(form);
        return "redirect:/todo?sort=" + sort +
                "&order=" + order +
                "&keyword=" + (keyword == null ? "" : keyword) +
                "&filter=" + filter +
                "&page=" + page;
    }

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

    @GetMapping("/{id}/edit")
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
        return "todo/edit";
    }

    @PutMapping("/{id}")
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
            return "todo/edit";
        }
        todoService.update(id, form);

        return "redirect:/todo?sort=" + sort +
                "&order=" + order +
                "&keyword=" + (keyword == null ? "" : keyword) +
                "&filter=" + filter +
                "&page=" + page;
    }

    @DeleteMapping("/{id}")
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

    // 一括削除
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

    // 一括完了
    @PostMapping("/complete-bulk")
    public String completeBulk(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "notdone") String filter,
            @RequestParam(defaultValue = "0") int page) {
        if (ids != null && !ids.isEmpty()) {
            todoService.markDoneAll(ids);
        }
        return "redirect:/todo?sort=" + sort +
                "&order=" + order +
                "&keyword=" + (keyword == null ? "" : keyword) +
                "&filter=" + filter +
                "&page=" + page;
    }

}