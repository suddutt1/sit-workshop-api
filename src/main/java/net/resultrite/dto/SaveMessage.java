package net.resultrite.dto;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SaveMessage {
    private String message;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
