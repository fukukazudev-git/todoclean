package com.example.todoclean.dto;

//getById()とupdate()の戻り値に使用するDTO
public class TodoDetailResponse{

    private Long id;
    private String title;
    private Boolean done;
    private Long version;

    public TodoDetailResponse(){}
    
    public TodoDetailResponse(Long id , String title, Boolean done, Long version){
        this.id = id;
        this.title = title;
        this.done = done;
        this.version = version;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    
    public Boolean getDone(){return done;}
    public void setDone(Boolean done){this.done = done;}

    public Long getVersion(){return version;}
    public void setVersion(Long version){this.version = version;}

}
