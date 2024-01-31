package net.resultrite.dto;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

public class AuthRequest extends PanacheEntityBase{
    private String email ;
    private String password;
    
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    
}
