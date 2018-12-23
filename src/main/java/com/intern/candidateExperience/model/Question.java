package com.intern.candidateExperience.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class Question {

    @Id
    private String id;
    private String questionNo;
    private String questionName;
    private int questionRating;

    public Question(String questionNo, String questionName, int questionRating) {
        this.questionNo = questionNo;
        this.questionName = questionName;
        this.questionRating = questionRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public int getQuestionRating() {
        return questionRating;
    }

    public void setQuestionRating(int questionRating) {
        this.questionRating = questionRating;
    }

}
