package net.resultrite.api;

import java.sql.Timestamp;

//import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;


/**
 * Example JPA entity defined as a Panache Entity.
 * An ID field of Long type is provided, if you want to define your own ID field extends <code>PanacheEntityBase</code> instead.
 *
 * This uses the active record pattern, you can also use the repository pattern instead:
 * .
 *
 * Usage (more example on the documentation)
 *
 * {@code
 *     public void doSomething() {
 *         MyEntity entity1 = new MyEntity();
 *         entity1.field = "field-1";
 *         entity1.persist();
 *
 *         List<MyEntity> entities = MyEntity.listAll();
 *     }
 * }
 */
@Entity
@Table(name="quiz_details",schema = "ibm_qus_ans")
public class QuizDetails extends PanacheEntityBase {

    @Id
    @Column(name="quiz_id",unique = true,nullable = false)
    String quiz_id;
    @Column(name = "title", nullable = false)
    String title;
    @Column(name = "create_ts", nullable = false)
    Timestamp create_ts;

    @Column(name = "update_ts", nullable = false)
    Timestamp update_ts;

    @Column(name = "status", nullable = false)
    String status;

    @JoinColumn(name = "owner_email",table="user_profile", referencedColumnName = "email", nullable = false)
    String owner_email;

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner_email() {
        return owner_email;
    }

    public void setOwner_email(String owner_email) {
        this.owner_email = owner_email;
    }

    
}
