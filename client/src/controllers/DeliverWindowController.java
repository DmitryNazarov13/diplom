package controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.Main;

public class DeliverWindowController {
    public Button back_to_auth;
    public void back_to_auth_Button(ActionEvent actionEvent) {
        new Main().openNewWindow((Stage) back_to_auth.getScene().getWindow(), true , "/views/authorization.fxml", "Регистрация" , false);
    }
}
