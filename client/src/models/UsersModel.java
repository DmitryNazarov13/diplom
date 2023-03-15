package models;

public class UsersModel {
    private int id_user;
    private String login;
    private String password;
    private String role_value;
    private int role;

    public UsersModel(String role_value, int role) {
        this.role_value = role_value;
        this.role = role;
    }

    public UsersModel(int id_user, String login, String password, String role_value, int role) {
        this.id_user = id_user;
        this.login = login;
        this.password = password;
        this.role_value = role_value;
        this.role = role;
    }

    @Override
    public String toString() {
        return  role_value ;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole_value() {
        return role_value;
    }

    public void setRole_value(String role_value) {
        this.role_value = role_value;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
