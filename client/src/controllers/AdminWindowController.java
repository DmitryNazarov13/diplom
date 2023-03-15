package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.ProductModel;
import models.UsersModel;
import sample.Main;

import java.util.ArrayList;

public class AdminWindowController {
    public Button back_to_auth,add_user, delete_user;
    public TextField login_field, password_field;
    public TableView<UsersModel> users_table;
    public TableColumn<UsersModel , String> login, password, role;
    public ChoiceBox<UsersModel> choise_role;
    final ObservableList<UsersModel> usersModelObservableList = FXCollections.observableArrayList();
    public void initialize() {
        login.setCellValueFactory(new PropertyValueFactory<UsersModel , String>("login"));
        password.setCellValueFactory(new PropertyValueFactory<UsersModel, String>("password"));
        role.setCellValueFactory(new PropertyValueFactory<UsersModel, String>("role_value"));

        loadAllUsers();
        laodChekBoxRoles();
    }

    private void laodChekBoxRoles() {
        ObservableList<UsersModel> roles = FXCollections.observableArrayList();
        ArrayList<String> list = new ArrayList<>();
        String[] categ ={""};
        list = (ArrayList<String>) Main.getObjectFromServer("laodChekBoxRoles");
        System.out.println(list);
        for (String tmp : list) {
            categ = tmp.split("~");
            roles.add(new UsersModel(categ[0], Integer.valueOf(categ[1])));
        }
        choise_role.setItems(roles);
    }


    private void loadAllUsers() {
        usersModelObservableList.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadAllUsers");
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            usersModelObservableList.add(new UsersModel(Integer.valueOf(strings[0]), strings[1], strings[2], strings[3], Integer.valueOf(strings[4])));
        }
        users_table.setItems(usersModelObservableList);
    }


    public void back_to_auth_Button(ActionEvent actionEvent) {
        new Main().openNewWindow((Stage) back_to_auth.getScene().getWindow(), true , "/views/authorization.fxml", "Авторизация" , false);
    }

    public void add_user_Button(ActionEvent actionEvent) {
        String log = login_field.getText();
        String pas = password_field.getText();
        String id_role = String.valueOf(choise_role.getSelectionModel().getSelectedItem().getRole());
        if(log.equals("") || pas.equals("") || id_role == null){
            new Main().allertError("Ошибка", "Заполните поля ввода","");
        }else {
            if(log.length() > 20 || pas.length() > 20){
                new Main().allertError("Ошибка","Логин или пароль слишком длинные", "Не более 20 символов");
            }else {
                System.out.println("registration" + "~" + log + "~" + pas + "~" + id_role);
                String answer = (String) Main.getObjectFromServer("registration" + "~" + log + "~" + pas + "~" + id_role);
                if(answer.equals("error")){
                    new Main().allertError("Ошибка","Ошибка регистрации","Данный логин уже занят");
                }else {
                    new Main().allertInfo("Успешно","Пользователь зарегистрирован");
                    loadAllUsers();
                    login_field.setText("");
                    password_field.setText("");
                }
            }
        }
    }

    public void delete_user_Button(ActionEvent actionEvent) {
        boolean a = users_table.getSelectionModel().isEmpty();
        if ( a == true){
            new Main().allertError("Ошибка","Пользователь не выбран","Выберите пользователя из таблицы");
        }else {
            String id = String.valueOf(users_table.getSelectionModel().getSelectedItem().getId_user());
            Main.sendToServerString("deleteUser" + "~" + id);
            new Main().allertInfo("Успешно","Пользователь удален");
            loadAllUsers();
        }
    }
}
