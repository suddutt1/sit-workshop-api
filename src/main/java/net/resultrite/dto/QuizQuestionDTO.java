package net.resultrite.dto;

import java.util.List;
public class QuizQuestionDTO {
    private String question;
    //private String questionId;
    private String question_id;
    private List<QuizOptionDTO> options;
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public List<QuizOptionDTO> getOptions() {
        return options;
    }
    public void setOptions(List<QuizOptionDTO> options) {
        this.options = options;
    }
    public String getQuestion_id() {
        return question_id;
    }
    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }
    
   
    


}

