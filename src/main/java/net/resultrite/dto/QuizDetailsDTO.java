package net.resultrite.dto;

import java.util.List;
public class QuizDetailsDTO {
    private String topic;
    private List<QuizQuestionDTO> questions;
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public List<QuizQuestionDTO> getQuestions() {
        return questions;
    }
    public void setQuestions(List<QuizQuestionDTO> questions) {
        this.questions = questions;
    }
    
    

}
