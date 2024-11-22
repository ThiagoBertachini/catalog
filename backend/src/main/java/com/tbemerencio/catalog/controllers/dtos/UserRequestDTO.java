package com.tbemerencio.catalog.controllers.dtos;

public class UserRequestDTO extends UserDTO{
    private String password;

    public UserRequestDTO(){
        super();
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
