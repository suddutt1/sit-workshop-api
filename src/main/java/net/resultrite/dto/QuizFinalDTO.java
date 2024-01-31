package net.resultrite.dto;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import net.resultrite.api.QuizQuestion;

@RegisterForReflection
public class QuizFinalDTO {
    private String quiz_id;
    private String topic;
    private List<QuizQuestion> quizQuestionFinal;
    public String getQuiz_id() {
        return quiz_id;
    }
    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public List<QuizQuestion> getQuizQuestionFinal() {
        return quizQuestionFinal;
    }
    public void setQuizQuestionFinal(List<QuizQuestion> quizQuestionFinal) {
        this.quizQuestionFinal = quizQuestionFinal;
    }
    
    
    
    

    
}
