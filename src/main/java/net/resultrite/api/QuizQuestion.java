package net.resultrite.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.sql.Timestamp;

@Entity
@Table(name = "QUIZ_QUESTION", schema = "ibm_qus_ans")
public class QuizQuestion extends PanacheEntityBase {

    @Id
    @Column(name = "quiz_id", nullable = false)
    private String quiz_id;

    @Id
    @Column(name = "question_id", nullable = false)
    private String question_id;


    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode options;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode answer;

    @Column(name = "answer_type", nullable = false)
    private String answer_type;

    @Column(name = "create_ts", nullable = false)
    private Timestamp create_ts;

    @Column(name = "update_ts", nullable = false)
    private Timestamp update_ts;

    @Column(name = "score", nullable = false)
    private int score;

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public QuizQuestion() {
        // Default constructor
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public JsonNode getOptions() {
        return options;
    }

    public void setOptions(JsonNode options) {
        this.options = options;
    }

    public JsonNode getAnswer() {
        return answer;
    }

    public void setAnswer(JsonNode answer) {
        this.answer = answer;
    }

    public String getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(String answer_type) {
        this.answer_type = answer_type;
    }

    public Timestamp getCreate_ts() {
        return create_ts;
    }

    public void setCreate_ts(Timestamp create_ts) {
        this.create_ts = create_ts;
    }

    public Timestamp getUpdate_ts() {
        return update_ts;
    }

    public void setUpdate_ts(Timestamp update_ts) {
        this.update_ts = update_ts;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((quiz_id == null) ? 0 : quiz_id.hashCode());
        result = prime * result + ((question_id == null) ? 0 : question_id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QuizQuestion other = (QuizQuestion) obj;
        if (quiz_id == null) {
            if (other.quiz_id != null)
                return false;
        } else if (!quiz_id.equals(other.quiz_id))
            return false;
        if (question_id == null) {
            if (other.question_id != null)
                return false;
        } else if (!question_id.equals(other.question_id))
            return false;
        return true;
    }



    // Additional methods if needed
}
