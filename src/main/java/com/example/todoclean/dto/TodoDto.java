package com.example.todoclean.dto;
//一覧用
public class TodoDto{

    private Long id;
    private String title;
    private Boolean done;

    public TodoDto(){}

    public TodoDto(Long id , String title, Boolean done){
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    
    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    public Boolean getDone(){return done;}
    public void setDone(Boolean done){this.done = done;}

}

/*
@NotBlank
文字列がnullでなく、空でなく、空白のみでないことを検証するアノテーション
文字列の入力が必須である場合に使用される。
@Size(max = 255)
文字列の長さが指定された最大値以下であることを検証するアノテーション
入力の長さ制限を設ける場合に使用される。
@NotNull
値がnullでないことを検証するアノテーション
// id に@NotNull は付けない→
// POSTでエラーになるため @NotNull(message = "id must not be null")
// idはControllerのパスパラメータで受け取る
// Boolean (ラッパークラス)
// nullを取れる→@NotNullをつけられる。JSONからの入力でnullが来た場合に検知可能。
//REST APIの入力はJSONのため、クライアントがdoneを送ってこない→null になるケースが存在する。
 */