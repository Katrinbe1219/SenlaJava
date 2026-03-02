package com.example.application.dto;

public class StrinResponse {
    String answer;
    public String getAnswer(){
        return answer;
    };
    public void setAnswer(String answer){
        this.answer = answer;
    }

    public StrinResponse(){}
    public StrinResponse(String answer){
        this.answer = answer;
    }
}
