package net.resultrite.api;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;

@Entity
@Table(name = "SCORE", schema = "ibm_qus_ans")
public class Score extends PanacheEntityBase {

    @Column(name = "quiz_id", nullable = false)
    String quiz_id;

    @Id
    @Column(name = "score_id", nullable = false)
    String score_id;

    @Column(name = "user_id", nullable = false)
    String user_id;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode answers;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "create_ts", nullable = false)
    Timestamp create_ts;

    @Column(name = "update_ts", nullable = false)
    Timestamp update_ts;

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getScore_id() {
        return score_id;
    }

    public void setScore_id(String score_id) {
        this.score_id = score_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public JsonNode getAnswers() {
        return answers;
    }

    public void setAnswers(JsonNode answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((quiz_id == null) ? 0 : quiz_id.hashCode());
        result = prime * result + ((score_id == null) ? 0 : score_id.hashCode());
        result = prime * result + ((user_id == null) ? 0 : user_id.hashCode());
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
        Score other = (Score) obj;
        if (quiz_id == null) {
            if (other.quiz_id != null)
                return false;
        } else if (!quiz_id.equals(other.quiz_id))
            return false;
        if (score_id == null) {
            if (other.score_id != null)
                return false;
        } else if (!score_id.equals(other.score_id))
            return false;
        if (user_id == null) {
            if (other.user_id != null)
                return false;
        } else if (!user_id.equals(other.user_id))
            return false;
        return true;
    }
    
}

