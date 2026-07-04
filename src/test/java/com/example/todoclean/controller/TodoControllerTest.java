package com.example.todoclean.controller;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.todoclean.dto.TodoCreateRequest;
import com.example.todoclean.dto.TodoDetailResponse;
import com.example.todoclean.service.TodoService;

/**
 * Controller層のテスト（MVC版）。
 * @WebMvcTest はWeb層（コントローラー）だけを起動し、Service層はモックに差し替える。
 * REST APIなら「返るJSON」を検証するところを、画面アプリでは
 * 「どのビュー(HTMLテンプレート)を返すか」「画面に渡すModelの中身」を検証するのがポイント。
 */
@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc; // 実サーバーを立てずにHTTPリクエストを模擬する

    @MockitoBean
    private TodoService todoService; // Web層から呼ばれるServiceはモック化

    @Test
    void editForm_編集ビューを返しModelにtodoを載せる() throws Exception {
        TodoDetailResponse detail =
                new TodoDetailResponse(1L, "買い物", "牛乳", false, 0L, LocalDate.of(2026, 7, 1));
        when(todoService.getById(1L)).thenReturn(detail);

        mockMvc.perform(get("/todo/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/edit"))                       // 返すテンプレート名
                .andExpect(model().attribute("todo", hasProperty("title", is("買い物")))); // 画面に渡す値
    }

    @Test
    void create_入力が正しければ一覧へリダイレクトしServiceを呼ぶ() throws Exception {
        mockMvc.perform(post("/todo/create")
                        .param("title", "買い物")
                        .param("dueDate", "2026-07-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/todo?*"));

        verify(todoService).create(any(TodoCreateRequest.class));
    }

    @Test
    void completeBulk_一覧へリダイレクトしServiceを呼ぶ() throws Exception {
        mockMvc.perform(post("/todo/complete-bulk")
                        .param("ids", "1")
                        .param("ids", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/todo?*"));

        verify(todoService).markDoneAll(java.util.List.of(1L, 2L));
    }

    @Test
    void create_タイトル未入力ならバリデーションエラーで作成画面に戻る() throws Exception {
        mockMvc.perform(post("/todo/create")
                        .param("title", "")              // @NotBlank に違反
                        .param("dueDate", "2026-07-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/create"))   // 400画面ではなく入力画面に戻る
                .andExpect(model().attributeHasFieldErrors("form", "title"));

        // バリデーションで弾かれるのでServiceは呼ばれない
        verify(todoService, never()).create(any(TodoCreateRequest.class));
    }
}
