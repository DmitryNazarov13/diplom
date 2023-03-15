package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.OrderModel;
import models.ProductModel;
import models.WorkOrdersModel;
import sample.Main;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class ClientWindowController {
    public Label user_label_name;
    public Button back_to_auth, add_product_to_order, delete_product_from_order, otmena_order, info_period_sum_orders, max_product_client;
    public ChoiceBox<ProductModel> choise_box_category;
    public ChoiceBox<String> choise_box_type_order;
    public TextField search_product_field;
    public RadioButton rad_new, rad_in_proc, rad_close;
    public ToggleGroup rad_orders;
    public DatePicker date_past, date_future;

    public TableView <OrderModel> table_order;
    public TableColumn<OrderModel, String> category_name, product_name_order;
    public TableColumn<OrderModel, Integer> number_order;
    public TableColumn<OrderModel, Double> cost_order;

    public TableView<ProductModel> table_produtcs;
    public TableColumn<ProductModel, String> product_name;
    public TableColumn<ProductModel, Integer> number_now;
    public TableColumn<ProductModel, Double> cost;

    public TableView<WorkOrdersModel> client_orders_table;
    public TableColumn<WorkOrdersModel, Integer> id_order;
    public TableColumn<WorkOrdersModel, String> type_order, status_order, date_get;
    public TableColumn<WorkOrdersModel,Double> total_cost_order;

    final String activeUser = Main.activeUser;
   // final String activeRole = Main.checkRole();

    final ObservableList<ProductModel> productModelObservableList = FXCollections.observableArrayList();
    public ObservableList<OrderModel> orderModelObservableList = FXCollections.observableArrayList();
    public ObservableList<WorkOrdersModel> workOrdersModelObservableList =FXCollections.observableArrayList();

    public void initialize() {

        user_label_name.setText(activeUser);
        product_name.setCellValueFactory(new PropertyValueFactory<ProductModel, String>("product_name"));
        cost.setCellValueFactory(new PropertyValueFactory<ProductModel, Double>("cost"));
        number_now.setCellValueFactory(new PropertyValueFactory<ProductModel, Integer>("number_now"));

        category_name.setCellValueFactory(new PropertyValueFactory<OrderModel, String>("category_name"));
        product_name_order.setCellValueFactory(new PropertyValueFactory<OrderModel, String>("product_name_order"));
        number_order.setCellValueFactory(new PropertyValueFactory<OrderModel, Integer>("number_order"));
        cost_order.setCellValueFactory(new PropertyValueFactory<OrderModel, Double>("cost_order"));

        id_order.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel, Integer>("id_order"));
        type_order.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel,String>("type"));
        status_order.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel, String>("status"));
        total_cost_order.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel, Double>("total_cost"));
        date_get.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel,String>("date_get"));




        loadCategoryBox();
        loadTypeOrderBox();


        choise_box_category.setOnAction(event -> {
            int id_cat = Integer.parseInt(choise_box_category.getSelectionModel().getSelectedItem().getId_category());
            System.out.println(choise_box_category.getSelectionModel().getSelectedItem().getId_category());
            loadProductTable( id_cat);
        });

        FilteredList<ProductModel> filteredDataCars = new FilteredList<>(productModelObservableList, e-> true);
        search_product_field.setOnKeyReleased(e ->{

            search_product_field.textProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredDataCars.setPredicate((Predicate<? super ProductModel>) productModel->{
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String filterString = newValue.toUpperCase();
                    //   String  = newValue.toLowerCase();
                    if(productModel.getProduct_name().toUpperCase().contains(filterString)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ProductModel> sortedData = new SortedList<>(filteredDataCars);
            sortedData.comparatorProperty().bind(table_produtcs.comparatorProperty());
            table_produtcs.setItems(sortedData);
        });


        client_orders_table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && !client_orders_table.getSelectionModel().isEmpty()) {
                    ArrayList <String> list = new ArrayList<>();
                    String id = String.valueOf(client_orders_table.getSelectionModel().getSelectedItem().getId_order());
                    list = (ArrayList<String>) Main.getObjectFromServer("getOrderItems" + "~" + id);
                    String[] strings = {""};
                    String answer = "";
                    for (String tmp : list) {
                        strings= tmp.split("~");
                        answer = answer + strings[0] + " --- " + strings[1] + " --- " + strings[2] +"\n";
                    }
                    new Main().allertInfo("Содержимое заказа", answer );

                    System.out.println(list);
                }
            }
        });


    }

    private void loadOrderClient(String type){
        workOrdersModelObservableList.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer(type + "~" + activeUser);
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            workOrdersModelObservableList.add(new WorkOrdersModel(Integer.parseInt(strings[0]), strings[1], strings[2], Double.valueOf(strings[3])));
        }
        client_orders_table.setItems(workOrdersModelObservableList);

    }

    private void loadCloseOrdersClient(String type, LocalDate past, LocalDate future){
        workOrdersModelObservableList.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        System.out.println(type + "~" + activeUser + "~" + past + "~" + future);
        list = (ArrayList<String>) Main.getObjectFromServer(type + "~" + activeUser + "~" + past + "~" + future);
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            workOrdersModelObservableList.add(new WorkOrdersModel(Integer.parseInt(strings[0]), strings[1], strings[2], Double.valueOf(strings[3]), strings[4]));
        }
        client_orders_table.setItems(workOrdersModelObservableList);

    }

    private void  loadTypeOrderBox(){
        ObservableList<String> langs = FXCollections.observableArrayList("Доставка", "Самовывоз");
        choise_box_type_order.setItems(langs);
    }

    private void loadProductTable (int id_cat){
    productModelObservableList.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadProductTableFromCategory" + "~" + id_cat);
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            productModelObservableList.add(new ProductModel(strings[0], strings[1], strings[2],
                    Double.valueOf(strings[3]), Integer.valueOf(strings[4])));
        }
        table_produtcs.setItems(productModelObservableList);
    }

    private void loadCategoryBox() {
        ObservableList<ProductModel> categoryes = FXCollections.observableArrayList();
        ArrayList<String> list = new ArrayList<>();
        String[] categ ={""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadCategoryBox");
        System.out.println(list);
        for (String tmp : list) {
            categ = tmp.split("~");
            categoryes.add(new ProductModel(categ[0], categ[1]));
        }
        choise_box_category.setItems(categoryes);

    }

    public void back_to_auth_Button(ActionEvent actionEvent) {
        new Main().openNewWindow((Stage) back_to_auth.getScene().getWindow(), true , "/views/authorization.fxml", "Авторизация" , false);
    }

    public void add_product_to_order_Button(ActionEvent actionEvent)  throws IOException{
        boolean a = table_produtcs.getSelectionModel().isEmpty();
        if (a == true ) {
            new Main().allertError("Ошибка", "Товар  не выбран",
                    "Выберите товар");
        } else {
            String id_cat = String.valueOf(Integer.parseInt(choise_box_category.getSelectionModel().getSelectedItem().getId_category()));
            String id_prod = String.valueOf(Integer.parseInt(table_produtcs.getSelectionModel().getSelectedItem().getId_product()));
            String cat = choise_box_category.getSelectionModel().getSelectedItem().getCatygory_name();
            String prod = table_produtcs.getSelectionModel().getSelectedItem().getProduct_name();
            Double cost_start = table_produtcs.getSelectionModel().getSelectedItem().getCost();
            int number_choise = table_produtcs.getSelectionModel().getSelectedItem().getNumber_now();
            int number_end;
//        orderModelObservableList.clear();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Выбор количества");
            dialog.setHeaderText("Какое количество необходимо?" +
                    "\nВ наличии - " + number_choise);
            dialog.setContentText("Пожалуйста введите кол-во");
// Traditional way to get the response value.
            Optional<String> result = dialog.showAndWait();
            TextField input = dialog.getEditor();
            if (Integer.valueOf(input.getText()) > number_choise) {
                new Main().allertError("Ошибка", "На складе недостаточно товара для заказа",
                        "Выберите другое кол-во");
            } else {
                number_end = Integer.valueOf(input.getText());
                Double cost_result = cost_start * number_end;
                orderModelObservableList.add(new OrderModel(id_cat, id_prod, cat, prod, Integer.valueOf(number_end), Double.valueOf(cost_result)));
                System.out.println(orderModelObservableList);
                table_order.setItems(orderModelObservableList);
                dialog.close();
            }
        }
    }

    public void delete_product_from_order_Button(ActionEvent actionEvent) {
        table_order.getItems().removeAll(table_order.getSelectionModel().getSelectedItem());
    }

    public void start_order_Button(ActionEvent actionEvent) {
        if (choise_box_type_order.getValue() == null || orderModelObservableList.isEmpty()) {
            new Main().allertError("Ошибка", "Тип доставки или заказы не выбраны", "Выберите тип заказа или товары");
        } else {
            String type_order = choise_box_type_order.getValue();
            String id_cat = "";
            String id_prod = "";
            String prod = "";
            String num = "";
            String cost = "";
            String answer_number_order = (String) Main.getObjectFromServer("createOrder" + "~" + activeUser + "~" + type_order);
            System.out.println(answer_number_order);
            for (int row = 0; row < table_order.getItems().size(); row++) {
                id_cat = orderModelObservableList.get(row).getId_category();
                id_prod = orderModelObservableList.get(row).getId_product();
                prod = orderModelObservableList.get(row).getCategory_name();
                num = String.valueOf(orderModelObservableList.get(row).getNumber_order());
                cost = String.valueOf(orderModelObservableList.get(row).getCost_order());
                Main.sendToServerString("crateOrderProduct" + "~" + id_cat + "~" + id_prod + "~" + prod
                        + "~" + num + "~" + cost + "~" + type_order + "~" + answer_number_order);
            }
            new Main().allertInfo("Оформление заказа", "Заказ оформлен");
            orderModelObservableList.clear();
            table_order.refresh();
            productModelObservableList.clear();
            table_produtcs.refresh();

        }
    }

    public void search_orders_Button(ActionEvent actionEvent) {
        if(rad_orders.getSelectedToggle() != null ){
            LocalDate past = date_past.getValue();
            LocalDate future = date_future.getValue();
            if(rad_new.isSelected()){
                loadOrderClient("loadNewOrderClientTable");
            }else if(rad_in_proc.isSelected()){
                loadOrderClient("loadInProcOrderClientTable");
            }else if(rad_close.isSelected()){
                if(date_past.getValue() != null || date_future.getValue() != null) {
                    if(past.isAfter(future)){
                        LocalDate temp  = null;
                        temp = past;
                        past = future;
                        future = past;
                    }
                    loadCloseOrdersClient("loadCloseOrderClientTable", past, future);
                }else {
                    new Main().allertError("Ошибка", "Не выбран промежуток дат от и до","Выберите промежуток дат в календарях");
                }
            }
        }else {
            new Main().allertError("Ошибка", "Не выбран тип поиска",
                    "Выберите тип поиска");
        }
    }

    public void otmena_order_Button(ActionEvent actionEvent) {
        boolean a = client_orders_table.getSelectionModel().isEmpty();
        if(a == true){
            new Main().allertError("Ошибка","Не выбран заказ","Для отмены выберите заказ");
        }else {
            String stat = client_orders_table.getSelectionModel().getSelectedItem().getStatus();
            if(!(stat.equals("На рассмотрении") || stat.equals("В процессе"))){
                new Main().allertError("Ошибка","Нельзя выбрать закрытый заказ","Выберите другой заказ");
            }else {
                String id_oorder = String.valueOf(client_orders_table.getSelectionModel().getSelectedItem().getId_order());
                Main.sendToServerString("declineOrder" + "~" + id_oorder);
                new Main().allertInfo("Отмена заказ","Заказ отменен");
                loadOrderClient("loadNewOrderClientTable");
            }
        }
    }

    public void info_period_sum_orders_Button(ActionEvent actionEvent) {
        LocalDate past = date_past.getValue();
        LocalDate future = date_future.getValue();
        if(date_past.getValue() != null || date_future.getValue() != null){
            if(past.isAfter(future)){
                LocalDate temp  = null;
                temp = past;
                past = future;
                future = past;
            }
            String answer = (String) Main.getObjectFromServer("infoPeriodSumOrdersButton" + "~" + activeUser +
                                                        "~" + past + "~" + future);
            System.out.println(answer);
            new Main().allertInfo("Информация о тратах за период",
                    "За период " + past + " - " + future + "\n" +
                    "Было потрачено - " + answer + "р.");
        }else {
            new Main().allertError("Ошибка","Выберите промежуток дат","");
        }
    }

    public void max_product_client_Button(ActionEvent actionEvent) {
        ArrayList<String> answer = (ArrayList<String>) Main.getObjectFromServer("maxProductClient" + "~" + activeUser);
        String[] strings ={""};
        for (String tmp : answer) {
            strings= tmp.split("~");
        }
        new Main().allertInfo("Самый популярный предмет у пользователя -" + activeUser,
                "Количество - " + strings[0] + "\nПродукт - " + strings[1]);
    }
}
