package net.resultrite.dto;

public  class QuizOptionDTO{
    private String optId;
    private String option;
    private boolean isCorrect;
    private boolean isSelected;
    private String question;
    private String question_id;

    

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getQuestion_id() {
        return question_id;
    }
    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    public String getOption() {
        return option;
    }
    public void setOption(String option) {
        this.option = option;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public String getOptId() {
        return optId;
    }
    public void setOptId(String optId) {
        this.optId = optId;
    }

} 