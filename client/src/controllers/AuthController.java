package controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;
import java.util.Optional;

public class AuthController {
    public Button authorization_button, registr_butt;
    public TextField login_field;
    public PasswordField password_field;

    public void authorization_Button(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) authorization_button.getScene().getWindow();
        String login = login_field.getText();
        String password = password_field.getText();
        System.out.println("authorization~" + login + "~" + password);


        System.out.println();
        if (!(login.equals("") || password.equals(""))) {
            String answer = Main.sendToServerString("authorization~" + login + "~" + password);
            if (!answer.equals("Error")){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("Выберите действие");
                ButtonType okButton = new ButtonType("Продолжить работу", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Личный кабинет", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(okButton, noButton);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == noButton) {
                    alert.close();
//                    new Main().setActiveUser(login);
//                    new Main().openNewWindow((Stage) authorization_button.getScene().getWindow(), true, "/views/AccountController.fxml", "Личный кабинет", false);
                } else if (result.isPresent() && result.get() == okButton) {
                    if (answer.equals("admin")) {
                        System.out.println(login);
                        new Main().setActiveUser(login);
                        new Main().openNewWindow(stage, true, "/views/adminWindow.fxml", "Админ", false);
                    } else if (answer.equals("worker") || answer.equals("manager")) {
                        new Main().setActiveUser(login);
                        new Main().openNewWindow(stage, true, "/views/workerWindow.fxml", "Работник", false);
                    } else if (answer.equals("deliver")) {
                        new Main().setActiveUser(login);
                        new Main().openNewWindow(stage, true, "/views/workerWindow.fxml", "Доставщик", false);
                    } else if (answer.equals("client")) {
                        new Main().setActiveUser(login);
                        new Main().openNewWindow(stage, true, "/views/clientWindow.fxml", "Клиент", false);
                    }
                }
            }else {
                new Main().allertError("Ошибка авторизации", "Пользователь не найден", "Повторите ввод данных");
                login_field.setText("");
                password_field.setText("");
            }
        }else
            new Main().allertError("Ошибка авторизации", "Присутствует незаполненное поле", " Проверьте введенные данные");
    }

    public void registr_Button(ActionEvent actionEvent) {
        new Main().openNewWindow((Stage) registr_butt.getScene().getWindow(), true , "/views/Registration.fxml", "Регистрация" , false);
    }
}
