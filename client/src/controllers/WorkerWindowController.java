package controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.ProductModel;
import models.WorkOrdersModel;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Predicate;

public class WorkerWindowController {
    public LineChart<String, Double> lineChartAnalis;
    public PieChart pieChartIncome;
    public TextField field_login_search, field_category_search, field_product_search, field_prod_cost,field_prod_num_max;
    public TextArea text_area_info_prod;
    public Tab pane_work_with_products, pane_analitic, pane_work_with_orders;

    public TableView<WorkOrdersModel> orders_table;
    public TableColumn<WorkOrdersModel, String>  login, type, status, date_get;
    public TableColumn<WorkOrdersModel, Integer> id_order;
    public TableColumn<WorkOrdersModel,Double> total_cost;

    public TableView<ProductModel> category_table;
    public TableColumn<ProductModel,String> category, product_cat;

    public TableView<ProductModel> product_table;
    public TableColumn<ProductModel,String> product, product_info;
    public TableColumn<ProductModel, Double> product_cost;
    public TableColumn<ProductModel, Integer> number_now, number_max;




    public RadioButton radio_new, radio_close, radio_pre_proc, rad_categ_prod,rad_all_categ,rad_categ_wihout_prod,
            rad_all_prod,rad_prod_wihout_categ, radio_income, radio_product, radio_top_client, radio_line_income,
            radio_line_orders;
    public ToggleGroup type_group,prod_group, categ_group, tougle_pie_chart, tougle_line_chart;
    public Button back_to_auth,close_order, search_orders, confim_order, decline_order,decline_payment, search_categ_butt,
            add_categ, search_prod,add_prod, delete_categ, add_prod_to_categ_butt, delete_prod_from_categ_butt,
            delete_prod_butt, add_prod_to_sklad, make_pie_chart, make_line_chart, download_butt;
    public DatePicker date_past, date_future, date_past_analis,date_future_analis;
    final String activeUser = Main.activeUser;
    final String activeRole = Main.checkRole();
    String type_order_search = "";
    String status_order = "";

    final ObservableList<ProductModel> categoryObserv = FXCollections.observableArrayList();
    final ObservableList<ProductModel> prodObserv = FXCollections.observableArrayList();
    final ObservableList<WorkOrdersModel> workOrdersModelObservableList = FXCollections.observableArrayList();

    public void initialize() {
        date_past_analis.setValue(LocalDate.ofEpochDay(2000 - 01 - 01));
        date_future.setValue(LocalDate.now());
        date_future_analis.setValue(LocalDate.now());


        id_order.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel, Integer>("id_order"));
        login.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel, String>("login"));
        type.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel, String>("type"));
        status.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel,String>("status"));
        date_get.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel,String>("date_get"));
        total_cost.setCellValueFactory(new PropertyValueFactory<WorkOrdersModel, Double>("total_cost"));



        if(activeRole.equals("1")){
            type_order_search = "'Самовывоз'";
        }else if(activeRole.equals("11")){
            radio_pre_proc.setVisible(true);
            confim_order.setVisible(true);
            decline_order.setVisible(true);
            decline_payment.setVisible(true);
            pane_analitic.setDisable(false);
            pane_work_with_products.setDisable(false);
            type_order_search = "'Самовывоз','Доставка'";

            category.setCellValueFactory(new PropertyValueFactory<ProductModel,String>("catygory_name"));
            product_cat.setCellValueFactory(new PropertyValueFactory<ProductModel,String>("product_name"));

            product.setCellValueFactory(new PropertyValueFactory<ProductModel,String>("product_name"));
            product_info.setCellValueFactory(new PropertyValueFactory<ProductModel,String>("product_info"));
            product_cost.setCellValueFactory(new PropertyValueFactory<ProductModel,Double>("cost"));
            number_now.setCellValueFactory(new PropertyValueFactory<ProductModel, Integer>("number_now"));
            number_max.setCellValueFactory(new PropertyValueFactory<ProductModel, Integer>("number_max"));

            loadAllCategoryProducts();



        }else if(activeRole.equals("2")){
            type_order_search = "'Доставка'";
        }

        loadNewOrders(type_order_search);

        FilteredList<WorkOrdersModel> filteredData = new FilteredList<>(workOrdersModelObservableList, e-> true);
        field_login_search.setOnKeyReleased(e ->{
            field_login_search.textProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super WorkOrdersModel>) workOrdersModel->{
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toUpperCase(Locale.ROOT);
                    //   String lowerCaseFilter = newValue.toLowerCase();
                    if(workOrdersModel.getLogin().toUpperCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<WorkOrdersModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(orders_table.comparatorProperty());
            orders_table.setItems(sortedData);
        });

        orders_table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && !orders_table.getSelectionModel().isEmpty()) {
                    ArrayList <String> list = new ArrayList<>();
                    String id = String.valueOf(orders_table.getSelectionModel().getSelectedItem().getId_order());
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

        loadChart("pieChartIncomeCategory", date_past_analis.getValue(), date_future_analis.getValue());

    }


    public void loadLineChart(String type, LocalDate past, LocalDate future){

        lineChartAnalis.getData().clear();
        lineChartAnalis.getData().removeAll();
        ObservableList<XYChart.Data> dataObservableList = FXCollections.observableArrayList();
        XYChart.Series<String, Double> series = new XYChart.Series<String,Double>();
        ArrayList<String> list = new ArrayList<>();
        list =  (ArrayList<String>) Main.getObjectFromServer(type + "~" + past + "~" + future);
        String[] strings = {""};
        for (String tmp:list) {
            strings = tmp.split("~");
            dataObservableList.add(new XYChart.Data(strings[1], strings[0], series));
            series.getData().add(new XYChart.Data<String, Double>(strings[1], Double.valueOf(strings[0])));

        }
        lineChartAnalis.getData().add(series);
        if(radio_line_orders.isSelected()){
            series.setName("Количество заказов в день");
        }else if (radio_line_income.isSelected()){
            series.setName("Прибыль в день");
        }
    }

    public void loadChart( String type ,LocalDate past, LocalDate future){

        ObservableList<PieChart.Data> pieChart = FXCollections.observableArrayList();
        ArrayList<String> list = new ArrayList<>();
        list =  (ArrayList<String>) Main.getObjectFromServer(type + "~" + past + "~" + future);
        String[] strings = {""};
        for (String tmp:list){
            strings = tmp.split("~");
            System.out.println(strings[0] + " " + strings[1]);

            pieChart.add(new PieChart.Data(strings[0], Double.valueOf(strings[1])));
        }
        pieChart.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " - ",   data.pieValueProperty().doubleValue())));
        pieChartIncome.setLabelLineLength(200);
        pieChartIncome.setLegendSide(Side.BOTTOM);
        pieChartIncome.setData(pieChart);
        if (radio_income.isSelected()){
            pieChartIncome.setTitle("Прибыль по категориям");
        }else if (radio_product.isSelected()){
            pieChartIncome.setTitle("Топ кол-ва продукта в заказах");
        }else if(radio_top_client.isSelected()){
            pieChartIncome.setTitle("Топ клиентов по выручке");
        }
    }

    private void loadAllCategoryProducts(){
        categoryObserv.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadCategoryProdTable");
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            categoryObserv.add(new ProductModel(strings[0], strings[1], strings[2]));
        }
        category_table.setItems(categoryObserv);
    }

    private void loadAllCategory(){
        categoryObserv.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadCategoryBox");
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            categoryObserv.add(new ProductModel(strings[0], strings[1]));
        }
        category_table.setItems(categoryObserv);
    }

    private void loadCategWithoutProd(){
        categoryObserv.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadCategWithoutProd");
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            categoryObserv.add(new ProductModel(strings[0], strings[1]));
        }
        category_table.setItems(categoryObserv);
    }

    private void loadPreProcOrders(String type_order_search) {
        workOrdersModelObservableList.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadPreProcOrdersWorkTable" + "~" + type_order_search);
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            workOrdersModelObservableList.add(new WorkOrdersModel(Integer.parseInt(strings[0]), strings[1],strings[2],
                    strings[3], Double.parseDouble(strings[4])));
        }
        orders_table.setItems(workOrdersModelObservableList);
    }

    private void loadNewOrders(String type) {
        workOrdersModelObservableList.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        list = (ArrayList<String>) Main.getObjectFromServer("loadNewOrdersWorkTable" + "~" + type);
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
          workOrdersModelObservableList.add(new WorkOrdersModel(Integer.parseInt(strings[0]), strings[1],strings[2],
                  strings[3], Double.parseDouble(strings[4])));
        }
        orders_table.setItems(workOrdersModelObservableList);
    }

    private void loadCloseOrders(String type_order_search, LocalDate past, LocalDate future) {
        workOrdersModelObservableList.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        System.out.println("loadCloseOrdersWorkTable" + "~" + type_order_search +"~" + past + "~" + future);
        list = (ArrayList<String>) Main.getObjectFromServer("loadCloseOrdersWorkTable" + "~" + type_order_search +"~" + past + "~" + future);
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            workOrdersModelObservableList.add(new WorkOrdersModel(Integer.parseInt(strings[0]), strings[1],strings[2],
                    strings[3], strings[4], Double.parseDouble(strings[5])));
        }
        orders_table.setItems(workOrdersModelObservableList);
    }

    private void loadAllProdutcs(){
        prodObserv.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        System.out.println("loadAllProducts");
        list = (ArrayList<String>) Main.getObjectFromServer("loadAllProducts");
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            prodObserv.add(new ProductModel(strings[0], strings[1], strings[2], Double.parseDouble(strings[3]), Integer.parseInt(strings[4]),
                    Integer.parseInt(strings[5])));
        }
        product_table.setItems(prodObserv);
    }

    private void loadProdWithoutCateg(){
        prodObserv.clear();
        ArrayList<String> list = new ArrayList<>();
        String[] strings = {""};
        System.out.println("loadAllProducts");
        list = (ArrayList<String>) Main.getObjectFromServer("loadProdWithoutCateg");
        System.out.println(list);
        for (String tmp : list) {
            strings= tmp.split("~");
            prodObserv.add(new ProductModel(strings[0], strings[1], strings[2], Double.parseDouble(strings[3]), Integer.parseInt(strings[4]),
                    Integer.parseInt(strings[5])));
        }
        product_table.setItems(prodObserv);
    }


    public void back_to_auth_Button(ActionEvent actionEvent) {
        new Main().openNewWindow((Stage) back_to_auth.getScene().getWindow(), true , "/views/authorization.fxml", "Регистрация" , false);
    }

    public void close_order_Button(ActionEvent actionEvent) {
        boolean a = orders_table.getSelectionModel().isEmpty();
        if (a == true){
            new Main().allertError("Ошибка", "Заказ не выбран", "Выберите заказ");
        }else {
            if(orders_table.getSelectionModel().getSelectedItem().getStatus().equals("Новый")){
                if(date_future.getValue() == null){
                    new Main().allertError("Ошибка","Не выбрана дата оплаты","Выберите дату оплаты\nКалендарь с 'до'");
                }else {
                    LocalDate date_choose = date_future.getValue();
                    if(date_choose.isBefore(LocalDate.now())){
                        new Main().allertError("Ошибка", "Оплата не может быть произведена в прошедший день", "Выберите другую дату");
                    }else {
                        String id_ord = String.valueOf(orders_table.getSelectionModel().getSelectedItem().getId_order());
                        Main.sendToServerString("payOrder" + "~" + id_ord + "~" + date_choose + "~" + activeUser);
                        System.out.println("payOrder" + "~" + id_ord + "~" + date_choose + "~" + activeUser);
                        new Main().allertInfo("Инфо","Заказ оплачен");
                        loadNewOrders(type_order_search);
                    }
                }
            }else {
                new Main().allertError("Ошибка","Вы выбрали не новый заказ","Повторите выбор");
            }
        }

    }

    public void search_orders_Button(ActionEvent actionEvent) {
        LocalDate past = LocalDate.ofEpochDay(2000-01-01);
        LocalDate future = LocalDate.now();
        if(type_group.getSelectedToggle() != null){
            if(radio_new.isSelected()){
                loadNewOrders(type_order_search);
            }else if (radio_pre_proc.isSelected()){
                loadPreProcOrders(type_order_search);
            }else if(radio_close.isSelected()){
                if(date_past.getValue() == null && date_future.getValue() == (null)) {
                    loadCloseOrders(type_order_search, past, future);
                    System.out.println(past);
                    System.out.println(future);
                }else if (date_past.getValue() == null && date_future.getValue() != null){
                    System.out.println(past);
                    future = date_future.getValue();
                    System.out.println(future);
                    loadCloseOrders(type_order_search, past, future);
                }else if (date_past.getValue() != null && date_future == null){
                    past = date_past.getValue();
                    if(past.isAfter(future)){
                        LocalDate temp  = null;
                        temp = past;
                        past = future;
                        future = past;
                    }
                    loadCloseOrders(type_order_search, past, future);
                }else if(date_past.getValue() != null && date_future.getValue() != null){
                    past = date_past.getValue();
                    future = date_future.getValue();
                    if(past.isAfter(future)){
                        LocalDate temp = null;
                        temp = past;
                        past = future;
                        future = temp;
                    }
                    loadCloseOrders(type_order_search, past, future);
                }
            }
        }else {
            new Main().allertError("Ошибка поиска", "Не выбран тип поиска" , "");

        }

    }


    public void confim_order_Button(ActionEvent actionEvent) {
        boolean a = orders_table.getSelectionModel().isEmpty();
        if (a == true){
            new Main().allertError("Ошибка", "Заказ подтверждения не выбран", "Выберите заказ");
        }else {
            if(orders_table.getSelectionModel().getSelectedItem().getStatus().equals("На рассмотрении")){
                String id_order = String.valueOf(orders_table.getSelectionModel().getSelectedItem().getId_order());
                Main.sendToServerString("confimOrder" + "~" + id_order);
                new Main().allertInfo("Инфо", "Заказ подтвержден");
                loadPreProcOrders(type_order_search);
            }else {
                new Main().allertError("Ошибка", "Вы выбрали заказ не на рассмотрении",
                        "Для подтверждения заказа выберите верный заказ");
            }

        }
    }

    public void decline_order_Button(ActionEvent actionEvent) {
        boolean a = orders_table.getSelectionModel().isEmpty();
        if(a == true){
            new Main().allertError("Ошибка","Вы для удаления выбрали заказ", "Выберите заказ");
        }else {
            if(orders_table.getSelectionModel().getSelectedItem().getStatus().equals("На рассмотрении")){
                String id_oorder = String.valueOf(orders_table.getSelectionModel().getSelectedItem().getId_order());
                Main.sendToServerString("declineOrder" + "~" + id_oorder);
                new Main().allertInfo("Отмена заказ","Заказ отменен");
                loadPreProcOrders(type_order_search);
            }else {
                new Main().allertError("Ошибка","Выбран заказ не для удаления","Выберите заказ на рассмотрении");
            }
        }

    }

    public void decline_payment_Button(ActionEvent actionEvent) {
        boolean a = orders_table.getSelectionModel().isEmpty();
        if (a == true){
            new Main().allertError("Ошибка","Заказ для отмены оплаты не выбран","Выберите заказ для оплаты");
        }else {
            if(orders_table.getSelectionModel().getSelectedItem().getStatus().equals("Оплачено")){
                String id_order = String.valueOf(orders_table.getSelectionModel().getSelectedItem().getId_order());
                Main.sendToServerString("declinePaymentOrder" + "~" + id_order);
                new Main().allertInfo("Успешно","Оплата отменена");
                loadNewOrders(type_order_search);
            }else {
                new Main().allertError("Ошибка","Выбран не закрытый заказ","Выберите закрытый заказ");
            }
        }
    }

    public void search_categ_Button(ActionEvent actionEvent) {
        if(categ_group.getSelectedToggle() != null){
            if(rad_categ_prod.isSelected()){
                loadAllCategoryProducts();
            }else if (rad_all_categ.isSelected()){
                loadAllCategory();
            }else if (rad_categ_wihout_prod.isSelected()){
                loadCategWithoutProd();
            }
        }else {
            new Main().allertError("Ошибка","Не выбран тип поиска категорий","Выберите тип поиска");
        }
    }

    public void search_prod_Button(ActionEvent actionEvent) {
        if(prod_group.getSelectedToggle() != null){
            if(rad_all_prod.isSelected()){
                loadAllProdutcs();
            }else if (rad_prod_wihout_categ.isSelected()){
                loadProdWithoutCateg();
            }
        }else {
            new Main().allertError("Ошибка","Не выбран тип фильтра","Выберите тип фильтра");
        }

    }

    public void add_prod_Button(ActionEvent actionEvent) {
        String prod_name = field_product_search.getText();
        String prod_info = text_area_info_prod.getText();
        String prod_cost = field_prod_cost.getText();
        String prod_num = field_prod_num_max.getText();
        if (prod_name.equals("")|| prod_info.equals("") || prod_cost.equals("") || prod_num.equals("")){
            new Main().allertError("Ошибка","Присутствуют незаполненные поля","Заполните все поля");
        }else {
            if(prod_name.length() > 50 || prod_info.length() > 200 || !prod_cost.matches("^[+]?[0-9]*[.,]?[0-9]+(?:[eE][-+]?[0-9]+)?$") ||
                prod_cost.length() > 7 || !prod_num.matches("[+]?\\d+") || prod_num.length() >=4){
                new Main().allertError("Ошибка","Введенные данные не корректны", "Проверетье введенные данные");
            }else {
                Main.sendToServerString("addProduct" + "~" + prod_name + "~" + prod_info + "~" + prod_cost + "~" +
                        "0" + "~" + prod_num);
                new Main().allertInfo("Успешно","Продукт добавлен");
                field_product_search.setText("");
                text_area_info_prod.setText("");
                field_prod_cost.setText("");
                field_prod_num_max.setText("");
                loadAllProdutcs();
            }
        }

    }

    public void add_categ_Button(ActionEvent actionEvent) {
        String categ_name = field_category_search.getText();
        if(categ_name.equals("")){
            new Main().allertError("Ошибка","Название категории не введено",
                    "Введите название категории в строку поиска категорий");
        }else {
            Main.sendToServerString("addNewCategory" + "~" + categ_name);
            new Main().allertInfo("Успешно", "Категория добавлена");
            loadAllCategory();
            field_category_search.clear();
        }
    }

    public void delete_categ_Button(ActionEvent actionEvent) {
        boolean a = category_table.getSelectionModel().isEmpty();
        if(a == true){
            new Main().allertError("Ошибка","Категория не выбрана","Выберите категорию");
        }else {
            String id_categ = category_table.getSelectionModel().getSelectedItem().getId_category();
            Main.sendToServerString("deleteCategory" + "~" + id_categ);
            new Main().allertInfo("Успешно", "Категория удалена, связи убраны");
            loadAllCategory();

        }
    }

    public void add_prod_to_categ_Button(ActionEvent actionEvent) {
        boolean a = category_table.getSelectionModel().isEmpty();
        boolean b = product_table.getSelectionModel().isEmpty();
        if(a==true || b == true){
            new Main().allertError("Ошибка","Не выбран продукт или категория для связки","Выберите оба параметра для связки");
        }else {
            String id_cat = category_table.getSelectionModel().getSelectedItem().getId_category();
            String id_prod = product_table.getSelectionModel().getSelectedItem().getId_product();
            String answer = (String) Main.getObjectFromServer("addProdToCateg" + "~" + id_cat + "~" + id_prod);
            if(answer.equals("good")){
                new Main().allertInfo("Успешно","Продукт привязан к категории");
                loadAllCategoryProducts();
                loadAllProdutcs();
            }else {
                new Main().allertError("Ошибка","Данный продукт уже привязан к категории", "Выберите другой продукт");
            }
            System.out.println(answer);
        }
    }

    public void delete_prod_from_categ_Button(ActionEvent actionEvent) {
        boolean a = product_table.getSelectionModel().isEmpty();
        if ( a == true){
            new Main().allertError("Ошибка","Товар не выбран","Выберите товар из списка");
        }else{
            String id_prod = product_table.getSelectionModel().getSelectedItem().getId_product();
            System.out.println("deleteProdFromCateg" + "~" + id_prod);
            String answer = (String) Main.getObjectFromServer("deleteProdFromCateg" + "~" + id_prod);
            if(answer.equals("error")){
                new Main().allertError("Ошибка","Данный товар не привязан к какой-либо категории ","Выберите другой товар");
            }else {
                new Main().allertInfo("Успешно","Связка полдукта с категорией удалена");
                loadAllCategoryProducts();
            }
        }

    }

    public void delete_prod_Button(ActionEvent actionEvent) {
        boolean a = product_table.getSelectionModel().isEmpty();
        if(a == true){
            new Main().allertError("Ошибка", "Продукт не выбран","Выберите продукт из списка");
        }else {
            String id = product_table.getSelectionModel().getSelectedItem().getId_product();
            System.out.println();
            Main.sendToServerString("deleteProduct" + "~" + id);
            new Main().allertInfo("Успешно","Продукт удален");
            loadAllProdutcs();
        }
    }

    public void add_prod_to_sklad_Button(ActionEvent actionEvent) {
        boolean a = product_table.getSelectionModel().isEmpty();
        if(a == true){
            new Main().allertError("Ошибка", "Для пополнения наличия на складе выберите товар", "Выберите товар из таблицы");
        }else {
            String id_prod = product_table.getSelectionModel().getSelectedItem().getId_product();
            String num_max = String.valueOf(product_table.getSelectionModel().getSelectedItem().getNumber_max());
            Main.sendToServerString("addProdToSklad" + "~" + id_prod + "~" + num_max);
            new Main().allertInfo("Успешно", "Запас на складе пополнен");
            loadAllProdutcs();
        }
    }

    public void make_pie_chart_Button(ActionEvent actionEvent) {
        if(tougle_pie_chart.getSelectedToggle() != null){
            if (date_future_analis.getValue() == null || date_past_analis.getValue() == null){
                new Main().allertError("Ошибка", "Не выбраны даты для построения графика", "Выберите промежуток от и до");
            }else {
                LocalDate past = date_past_analis.getValue();
                LocalDate future = date_future_analis.getValue();
                if(past.isAfter(future)){
                    LocalDate temp  = null;
                    temp = past;
                    past = future;
                    future = past;
                }
                if (radio_income.isSelected()){
                    loadChart("pieChartIncomeCategory", past, future);
                }else if (radio_product.isSelected()){
                    loadChart("pieChartProductSels", past, future);
                }else if(radio_top_client.isSelected()){
                    loadChart("pieChartClientProfit", past, future);
                }
            }
        }else {
            new Main().allertError("Ошибка", "Не выбран тип графика","Выберите тип графика");
        }
    }

    public void make_line_chart_Button(ActionEvent actionEvent) {
        if(tougle_line_chart.getSelectedToggle() != null){
            if (date_future_analis.getValue() == null || date_past_analis.getValue() == null){
                new Main().allertError("Ошибка", "Не выбраны даты для построения графика", "Выберите промежуток от и до");
            }else {
                LocalDate past = date_past_analis.getValue();
                LocalDate future = date_future_analis.getValue();
                if(past.isAfter(future)){
                    LocalDate temp  = null;
                    temp = past;
                    past = future;
                    future = past;
                }
                if (radio_line_orders.isSelected()){
                    loadLineChart("lineChartOrders", past, future);
                }else if (radio_line_income.isSelected()){
                    loadLineChart("lineChartIncome", past, future);
                }
            }
        }else {
            new Main().allertError("Ошибка", "Не выбран тип графика","Выберите тип графика");
        }
    }

    public void download_Button(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохраните файл");
        File saveFile = fileChooser.showSaveDialog(orders_table.getScene().getWindow());
        if (saveFile != null){
            saveFile = new File(saveFile.toString() + ".xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Sheet1");
            XSSFRow firstRow = sheet.createRow(0);

            for (int i=0; i<orders_table.getColumns().size();i++){
                firstRow.createCell(i).setCellValue(orders_table.getColumns().get(i).getText());
            }
            for (int row=0; row<orders_table.getItems().size();row++){
                XSSFRow row1 = sheet.createRow(row+1);
                for (int col=0; col<orders_table.getColumns().size(); col++){
                    Object celValue = orders_table.getColumns().get(col).getCellObservableValue(row).getValue();
                    try {
                        if (celValue != null && Double.parseDouble(celValue.toString()) != 0.0) {
                            row1.createCell(col).setCellValue(Double.parseDouble(celValue.toString()));
                        }
                    } catch (  NumberFormatException e ){
                        row1.createCell(col).setCellValue(celValue.toString());
                    }
                }
            }
            FileOutputStream out = new FileOutputStream(new File(saveFile.toString()));
            workbook.write(out);
            workbook.close();
            out.close();
            new Main().allertInfo("Экспорт в Excel", "Экспорт завершен, можете проверить файл");
        }else {
            new Main().allertInfo("Экспорт в Excel", "Экспорт не был завершен");
        }
    }
}
