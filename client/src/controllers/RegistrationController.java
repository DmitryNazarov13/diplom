package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Main;


public class RegistrationController {
    public TextField set_login_field;
    public PasswordField set_password_field;
    public Button complite_checkin_button, back_butt;
    public Label info_field;

    public void complite_checkin_Button(javafx.event.ActionEvent actionEvent) {
        String login = set_login_field.getText();
        String password = set_password_field.getText();
        if (!(login.equals("") || password.equals(""))) {
            String answer = (String) Main.getObjectFromServer("registration~" + login + "~" + password + "~" + "3");
            if(answer.equals("error")){
                new Main().allertError("Ошибка","Ошибка регистрации","Данный логин уже занят");
            }else {
                new Main().allertInfo("Успешно","Пользователь зарегистрирован");
            }

        }else info_field.setText("Присутствует незаполненное поле");
    }

    public void back_Butt(javafx.event.ActionEvent actionEvent) {
        new Main().openNewWindow((Stage) back_butt.getScene().getWindow(), true , "/views/authorization.fxml", "Авторизация", false);
    }
}
