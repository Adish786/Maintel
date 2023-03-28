package com.about.mantle.infocat.model;

import java.io.Serializable;

public class QuestionAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QuestionAnswer{");
        sb.append("question='").append(question).append('\'');
        sb.append(", answer='").append(answer).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
