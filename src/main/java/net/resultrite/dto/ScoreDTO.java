package net.resultrite.dto;

import java.util.List;

public class ScoreDTO {
    private String quiz_id;
    private List<QuizQuestionDTO> questions;
    
    public List<QuizQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionDTO> questions) {
        this.questions = questions;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }
    
}
