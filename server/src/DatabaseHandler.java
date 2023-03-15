import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Queue;

public class DatabaseHandler {
    Connection databaseCnct;
    private Statement statement;
    String insertStr = "";


    public DatabaseHandler() {
        dbConnection();
    }

    public void dbConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            databaseCnct = DriverManager.getConnection("jdbc:mysql://localhost/diplom?autoReconnect=true", "root", "root");

            statement = databaseCnct.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String quotate(String content) {
        return "'" + content + "'";
    }

    public void authorization (String[] action, ObjectOutputStream soos){
        ResultSet users = null;
        try {
            users = statement.executeQuery("SELECT * FROM users");
            String answer = "Error";
            while (users.next()){
                if (users.getString("login").equals(action[1]) &&
                        users.getString("password").equals(action[2])){
                    if (users.getString("role").equals("0")) answer = "admin";
                    else if (users.getString("role").equals("1") ) answer = "worker";
                    else if (users.getString("role").equals("2")) answer = "deliver";
                    else if (users.getString("role").equals("3")) answer = "client";
                    else if(users.getString("role").equals("11")) answer = "manager";
                    else answer = "Error";
                }
            }
            users.close();
            soos.writeObject(answer);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCategoryBox(String[] tmp, ObjectOutputStream soos) {
        ArrayList<String> list =new ArrayList<>();
        try{
            ResultSet rs = statement.executeQuery("SELECT id_category, category_name FROM `categoryes` ");
            while (rs.next()) {
                list.add(rs.getString("id_category") +
                        "~" + rs.getString("category_name"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProductTableFromCategory(String[] tmp, ObjectOutputStream soos) {
        ArrayList<String> list =new ArrayList<>();
        try{
            ResultSet rs = statement.executeQuery("SELECT categoryes.id_category, products.id_product, products.product_name," +
                    " products.cost, products.number_now\n" +
                    "FROM categoryes JOIN category_product on categoryes.id_category = category_product.id_category \n" +
                    "JOIN products on category_product.id_product = products.id_product\n" +
                    "WHERE categoryes.id_category = " + quotate(tmp[1]));
            while (rs.next()) {
                list.add(rs.getString("categoryes.id_category") +
                        "~" + rs.getString("products.id_product") +
                        "~" + rs.getString("products.product_name") +
                        "~" + rs.getString("products.cost") +
                        "~" + rs.getString("products.number_now"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createOrder(String[] tmp, ObjectOutputStream soos) {
        try {
            String number ="";
            String answer = "";
            String id_user = "";

            ResultSet rs = statement.executeQuery("SELECT max(id_order) as qwe FROM orders");
            while (rs.next()){
                number = rs.getString("qwe");
            }
            rs.close();
            System.out.println("null = " + number);
            if (number == null){
                answer = "0";
            }else {
                answer = String.valueOf(Integer.parseInt(number) + 1);
            }

            ResultSet rs_id_us = statement.executeQuery("SELECT id_user FROM users WHERE login = " + quotate(tmp[1]));
            while (rs_id_us.next()){
                id_user = rs_id_us.getString("id_user");
            }
            rs_id_us.close();

            insertStr = "INSERT INTO `orders`(`id_order`, `id_user`, `type`, `status`) VALUES (" +
                    quotate(answer) + "," + quotate(id_user) + "," + quotate(tmp[2]) + ",'0')";
            System.out.println(insertStr);
            statement.executeUpdate(insertStr);
            soos.writeObject(answer);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void crateOrderProduct(String[] tmp, ObjectOutputStream soos) {
        try{
            String number_is = "";
            String answer = "";
            ResultSet rs = statement.executeQuery("SELECT number_now FROM products WHERE id_product = " + quotate(tmp[2]));
            while (rs.next()){
                number_is = rs.getString("number_now");
            }
            rs.close();
            if(Integer.parseInt(number_is) - Integer.parseInt(tmp[4]) < 0){
                answer = "error";
            }else {
                int newnum = Integer.parseInt(number_is) - Integer.parseInt(tmp[4]);
                System.out.println("INSERT INTO `order_product`(`id_order`, `id_product`, `number`, `total_cost`) VALUES (" +
                        quotate(tmp[7]) + "," + quotate(tmp[2]) + "," + quotate(tmp[4]) + "," + quotate(tmp[5]) +")");
                insertStr = "INSERT INTO `order_product`(`id_order`, `id_product`, `number`, `total_cost`) VALUES (" +
                        quotate(tmp[7]) + "," + quotate(tmp[2]) + "," + quotate(tmp[4]) + "," + quotate(tmp[5]) +")";
                statement.executeUpdate(insertStr);
                System.out.println("UPDATE `products` SET `number_now`= " + quotate(String.valueOf(newnum)) + "WHERE `id_product` = " + quotate(tmp[2]));
                insertStr = "UPDATE `products` SET `number_now`= " + quotate(String.valueOf(newnum)) + "WHERE `id_product` = " + quotate(tmp[2]);
                statement.executeUpdate(insertStr);
                answer = "good";
            }
            soos.writeObject(answer);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNewOrdersWorkTable(String[] tmp, ObjectOutputStream soos) {
        ArrayList<String> list =new ArrayList<>();
        try{
            System.out.println("SELECT sum(order_product.total_cost) as totcost," +
                    " order_product.id_order, users.login, orders.type, orders.status, orders.date_get \n" +
                    "FROM order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN users on  orders.id_user = users.id_user GROUP by order_product.id_order" +
                    " WHERE orders.type in( " + tmp[1] + ") and orders.status = '1'");
            ResultSet rs = statement.executeQuery("SELECT sum(order_product.total_cost) as totcost," +
                    " order_product.id_order, users.login, orders.type," +
                    "CASE WHEN  orders.status = 1 THEN 'Новый' end as ord_stat , orders.date_get \n" +
                    "FROM order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN users on  orders.id_user = users.id_user  WHERE orders.type in( " + tmp[1] + ") and orders.status = '1'" +
                    "GROUP by order_product.id_order");
            while (rs.next()) {
                list.add(rs.getString("order_product.id_order") +
                        "~" + rs.getString("users.login") +
                        "~" + rs.getString("orders.type") +
                        "~" + rs.getString("ord_stat") +
                        "~" + rs.getString("totcost"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkRole(String[] tmp, ObjectOutputStream soos) {
        try{
            String role = "";
            ResultSet rs = statement.executeQuery("Select role from users where login = " + quotate(tmp[1]));
            while (rs.next()){
                role = rs.getString("role");
            }
            rs.close();
            soos.writeObject(role);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPreProcOrdersWorkTable(String[] tmp, ObjectOutputStream soos) {
        ArrayList<String> list =new ArrayList<>();
        try{
            System.out.println("SELECT sum(order_product.total_cost) as totcost," +
                    " order_product.id_order, users.login, orders.type, orders.status, orders.date_get \n" +
                    "FROM order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN users on  orders.id_user = users.id_user GROUP by order_product.id_order" +
                    " WHERE orders.type in( " + tmp[1] + ") and orders.status = '1'");
            ResultSet rs = statement.executeQuery("SELECT sum(order_product.total_cost) as totcost," +
                    " order_product.id_order, users.login, orders.type," +
                    "CASE WHEN  orders.status = 0 THEN 'На рассмотрении' end as ord_stat , orders.date_get \n" +
                    "FROM order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN users on  orders.id_user = users.id_user  WHERE orders.type in( " + tmp[1] + ") and orders.status = '0'" +
                    "GROUP by order_product.id_order");
            while (rs.next()) {
                list.add(rs.getString("order_product.id_order") +
                        "~" + rs.getString("users.login") +
                        "~" + rs.getString("orders.type") +
                        "~" + rs.getString("ord_stat") +
                        "~" + rs.getString("totcost"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCloseOrdersWorkTable(String[] tmp, ObjectOutputStream soos) {
        ArrayList<String> list =new ArrayList<>();
        try{
            System.out.println("SELECT sum(order_product.total_cost) as totcost," +
                    " order_product.id_order, users.login, orders.type," +
                    "CASE WHEN  orders.status = 2 THEN 'Оплачено' end as ord_stat , orders.date_get \n" +
                    "FROM order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN users on  orders.id_user = users.id_user  WHERE orders.type in( " + tmp[1] + ") and orders.status = '2'" +
                    "and date(orders.date_get) between " + quotate(tmp[2]) + " and " + quotate(tmp[3]) +
                    " GROUP by order_product.id_order");
            ResultSet rs = statement.executeQuery("SELECT sum(order_product.total_cost) as totcost," +
                    " order_product.id_order, users.login, orders.type," +
                    "CASE WHEN  orders.status = 2 THEN 'Оплачено' end as ord_stat , orders.date_get \n" +
                    "FROM order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN users on  orders.id_user = users.id_user  WHERE orders.type in( " + tmp[1] + ") and orders.status = '2'" +
                    "and date(orders.date_get) between " + quotate(tmp[2]) + " and " + quotate(tmp[3]) +
                    " GROUP by order_product.id_order");
            while (rs.next()) {
                list.add(rs.getString("order_product.id_order") +
                        "~" + rs.getString("users.login") +
                        "~" + rs.getString("orders.type") +
                        "~" + rs.getString("ord_stat") +
                        "~" + rs.getString("orders.date_get") +
                        "~" + rs.getString("totcost"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void payOrder(String[] tmp, ObjectOutputStream soos) {
        try{
            String id_user = "";
            ResultSet rs = statement.executeQuery("Select id_user from users where login = " + quotate(tmp[3]));
            while (rs.next()){
                id_user = rs.getString("id_user");
            }
            rs.close();
            insertStr = "UPDATE `orders` SET `date_get` = "  +quotate(tmp[2]) + ", `id_user_stop`= " + quotate(id_user) +
                    ",`status` = '2' where id_order = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void declineOrder(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> list1 = new ArrayList<>();
            String[] strings = {""};
            ResultSet rs1 = null;
            ResultSet rs = statement.executeQuery("SELECT order_product.id_product, order_product.number , products.number_now, products.number_max \n" +
                    "FROM order_product JOIN products on order_product.id_product = products.id_product \n" +
                    "WHERE order_product.id_order = " + quotate(tmp[1]));
            while (rs.next()){
                list.add(rs.getString("order_product.id_product") +
                        "~" + rs.getString("order_product.number") +
                        "~" + rs.getString("products.number_now") +
                        "~" + rs.getString("products.number_max"));
            }
            rs.close();
            System.out.println(list);
            for (String tmp1 : list) {
                strings = tmp1.split("~");
                    int temp_sum ;
                    temp_sum = Integer.parseInt(strings[1] + strings[2]);
                    if(temp_sum >= Integer.parseInt(strings[3])){
                        temp_sum = Integer.parseInt(strings[3]);
                    }
                    System.out.println("UPDATE `products` SET `number_now`= " +quotate(String.valueOf(temp_sum)) +
                            " where id_product = " + quotate(strings[0]));
                    insertStr = "UPDATE `products` SET `number_now`= " +quotate(String.valueOf(temp_sum)) +
                            " where id_product = " + quotate(strings[0]);
                    statement.executeUpdate(insertStr);
                }
            insertStr = "UPDATE `orders` SET `status`= 3 where id_order = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
            }

         catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void declinePaymentOrder(String[] tmp, ObjectOutputStream soos) {
        try{
            insertStr = "UPDATE `orders` SET `status` = 1, `date_get`= null, `id_user_stop`= null where `id_order` = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCategoryProdTable(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT categoryes.id_category, categoryes.category_name, products.product_name FROM categoryes\n" +
                    "JOIN category_product on categoryes.id_category = category_product.id_category \n" +
                    "JOIN products on category_product.id_product = products.id_product");
            while (rs.next()){
                list.add(rs.getString("categoryes.id_category") + "~"
                + rs.getString("categoryes.category_name") + "~"
                + rs.getString("products.product_name"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCategWithoutProd(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT categoryes.id_category, categoryes.category_name FROM categoryes\n" +
                    "LEFT JOIN category_product on categoryes.id_category = category_product.id_category\n" +
                    "WHERE category_product.id_product is null");
            while (rs.next()){
                list.add(rs.getString("categoryes.id_category") + "~" + rs.getString("categoryes.category_name"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllProducts(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT id_product, product_name, info, cost, number_now, number_max FROM products");
            while (rs.next()){
                list.add(rs.getString("id_product") + "~" +
                        rs.getString("product_name") + "~" +
                        rs.getString("info") + "~" +
                        rs.getString("cost") + "~" +
                        rs.getString("number_now") + "~" +
                        rs.getString("number_max"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProdWithoutCateg(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT products.id_product, products.product_name, products.info, products.cost, products.number_now, products.number_max\n" +
                    "FROM products LEFT JOIN category_product on products.id_product = category_product.id_product \n" +
                    "WHERE category_product.id_category is null");
            while (rs.next()){
                list.add(rs.getString("products.id_product") + "~" +
                        rs.getString("products.product_name") + "~" +
                        rs.getString("products.info") + "~" +
                        rs.getString("products.cost") + "~" +
                        rs.getString("products.number_now") + "~" +
                        rs.getString("products.number_max"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(String[] tmp, ObjectOutputStream soos) {
        try{
            insertStr = "DELETE FROM `categoryes` WHERE id_category = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            insertStr = "DELETE FROM `category_product` WHERE id_category = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void confimOrder(String[] tmp, ObjectOutputStream soos) {
        try{
            insertStr = "UPDATE `orders` SET `status`= '1' where `id_order`= " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewCategory(String[] tmp, ObjectOutputStream soos) {
        try {
            insertStr = "INSERT INTO `categoryes`(`category_name`) VALUES (" + quotate(tmp[1]) + ")";
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registration(String[] tmp, ObjectOutputStream soos) {
        try {
            String id = "";
            String answer = "";
            ResultSet rs = statement.executeQuery("select id_user from users where login = " + quotate(tmp[1]));

            while (rs.next()){
                id = rs.getString("id_user");
            }
            if(id.equals("")) {
                insertStr = "INSERT INTO `users`(`login`, `password`, `role`) values (" + quotate(tmp[1]) + "," + quotate(tmp[2]) + "," + quotate(tmp[3]) + ")";
                statement.executeUpdate(insertStr);
                answer = "good";
            }else {
                answer = "error";
            }
            soos.writeObject(answer);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllUsers(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT id_user, login, `password`,\n" +
                    "CASE WHEN role = '0' then 'Админ'\n" +
                    "WHEN role = '1' then 'Работник'\n" +
                    "when role = '2' then 'Курьер'\n" +
                    "when role = '3' THEN 'Клиент'\n" +
                    "When role = '11' THEN 'Менеджер' \n" +
                    "end as role_value, role\n" +
                    "FROM users");
            while (rs.next()){
                list.add(rs.getString("id_user") + "~"+
                        rs.getString("login") + "~" +
                        rs.getString("password") + "~" +
                        rs.getString("role_value") + "~" +
                        rs.getString("role"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void laodChekBoxRoles(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT " +
                    "CASE WHEN role = '0' then 'Админ'\n" +
                    "WHEN role = '1' then 'Работник'\n" +
                    "when role = '2' then 'Курьер'\n" +
                    "when role = '3' THEN 'Клиент'\n" +
                    "When role = '11' THEN 'Менеджер' \n" +
                    "end as role_value, role\n" +
                    "FROM users");
            while (rs.next()){
                list.add(rs.getString("role_value") + "~" +
                        rs.getString("role"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String[] tmp, ObjectOutputStream soos) {
        try{
            insertStr = "Delete from users where id_user = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOrderItems(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT products.product_name, order_product.number, order_product.total_cost\n" +
                    "FROM products JOIN order_product on products.id_product = order_product.id_product\n" +
                    "WHERE order_product.id_order = " + quotate(tmp[1]));
            while (rs.next()){
                list.add(rs.getString("products.product_name") + "~" +
                        rs.getString("order_product.number") + "~" +
                        rs.getString("order_product.total_cost"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addProdToCateg(String[] tmp, ObjectOutputStream soos) {
        try {
            String answer = "";
            String chek = "";
            ResultSet rs = statement.executeQuery("SELECT category_product.id_category" +
                    " FROM category_product " +
                    "WHERE category_product.id_category = " + quotate(tmp[1]) +
                    " and category_product.id_product = " + quotate(tmp[2]));
            while (rs.next()){
                chek = rs.getString("category_product.id_category");
            }
            rs.close();
            if(chek.equals("")){
                insertStr = "INSERT INTO `category_product`(`id_category`, `id_product`)" +
                        " VALUES (" + quotate(tmp[1]) + "," + quotate(tmp[2]) +")";
                statement.executeUpdate(insertStr);
                answer = "good";
            }else {
                answer = "error";
            }

            soos.writeObject(answer);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(String[] tmp, ObjectOutputStream soos) {
        try{
            insertStr = "Delete from products where id_product = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            insertStr = "Delete from category_product where id_product = " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(String[] tmp, ObjectOutputStream soos) {
        try{
            insertStr = "INSERT INTO `products`(`product_name`, `info`, `cost`, `number_now`, `number_max`) " +
                    "VALUES (" + quotate(tmp[1]) + "," + quotate(tmp[2]) + "," + quotate(tmp[3]) + "," +
                    quotate(tmp[4]) + "," + quotate(tmp[5]) + ")";
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteProdFromCateg(String[] tmp, ObjectOutputStream soos) {
        try{
            String answer = "";
            String check = "";
            ResultSet rs = statement.executeQuery("Select id_product from category_product where id_product = " + quotate(tmp[1]));
            while (rs.next()){
                check = rs.getString("id_product");
            }
            rs.close();
            if(check.equals("")){
                answer = "error";
            }else {
                insertStr = "Delete from category_product where id_product = " + quotate(tmp[1]);
                statement.executeUpdate(insertStr);
                answer = "good";
            }
            soos.writeObject(answer);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pieChartIncomeCategory(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT sum(order_product.total_cost) as c_c, categoryes.category_name FROM\n" +
                    "order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN products on order_product.id_product = products.id_product\n" +
                    "JOIN category_product on products.id_product = category_product.id_product\n" +
                    "JOIN categoryes on category_product.id_category = categoryes.id_category\n" +
                    "WHERE orders.status = '2' and orders.date_get BETWEEN\n" + quotate(tmp[1]) + " and " + quotate(tmp[2]) +
                    " GROUP BY categoryes.id_category");
            while (rs.next()){
                list.add(rs.getString("categoryes.category_name") + "~" + rs.getString("c_c"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addProdToSklad(String[] tmp, ObjectOutputStream soos) {
        try {
            insertStr = "UPDATE `products` SET `number_now`= " + quotate(tmp[2]) + " WHERE `id_product`= " + quotate(tmp[1]);
            statement.executeUpdate(insertStr);
            soos.writeObject("good");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pieChartProductSels(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT sum(order_product.number) as c_c, products.product_name FROM\n" +
                    "order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN products on order_product.id_product = products.id_product\n" +
                    "WHERE orders.status = '2' and orders.date_get between\n" +  quotate(tmp[1]) + " and " + quotate(tmp[2]) +
                    "GROUP BY products.id_product" +
                    " Order by c_c desc" +
                    " limit 5");
            while (rs.next()){
                list.add(rs.getString("products.product_name") + "~" + rs.getString("c_c"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pieChartClientProfit(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT sum(order_product.total_cost) as c_c, users.login\n" +
                    "FROM order_product JOIN orders on order_product.id_order = orders.id_order\n" +
                    "JOIN users on orders.id_user = users.id_user\n" +
                    "WHERE orders.status = '2' and orders.date_get between\n" +  quotate(tmp[1]) + " and " + quotate(tmp[2]) +
                    "GROUP BY users.id_user\n" +
                    "ORDER BY c_c DESC\n" +
                    "LIMIT 5");
            while (rs.next()){
                list.add(rs.getString("users.login") + "~" + rs.getString("c_c"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lineChartOrders(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT COUNT(orders.id_order) as c_c, orders.date_get," +
                    " DATE_FORMAT(date(orders.date_get),'%d - %m, %W') AS date_res\n" +
                    "FROM orders\n" +
                    "WHERE orders.status = '2' and orders.date_get BETWEEN\n" + quotate(tmp[1])  + " and " + quotate(tmp[2]) +
                    "GROUP by orders.date_get\n" +
                    "ORDER by orders.date_get ASC");
            while (rs.next()){
                list.add(rs.getString("c_c") + "~"  +rs.getString("date_res"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lineChartIncome(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT SUM(order_product.total_cost) as c_c, " +
                    "orders.date_get, DATE_FORMAT(date(orders.date_get),'%d - %m, %W') AS date_res\n" +
                    "FROM orders JOIN order_product on orders.id_order = order_product.id_order\n" +
                    "WHERE orders.status = '2' and orders.date_get BETWEEN\n" + quotate(tmp[1])  + " and " + quotate(tmp[2]) +
                    " GROUP by orders.date_get\n" +
                    " ORDER by orders.date_get ASC");
            while (rs.next()){
                list.add(rs.getString("c_c") + "~"  +rs.getString("date_res"));
            }
            rs.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNewOrderClientTable(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            String id_use = "";
            ResultSet rs = statement.executeQuery("Select id_user from users where login = " + quotate(tmp[1]));
            while (rs.next()){
                id_use = rs.getString("id_user");
            }
            rs.close();
            ResultSet rs1 = statement.executeQuery("SELECT orders.id_order, orders.type, 'На рассмотрении' as stat_ord, sum(order_product.total_cost) as c_c \n" +
                    "FROM orders JOIN order_product on orders.id_order = order_product.id_order\n" +
                    "WHERE orders.status = '0' and orders.id_user = \n" + quotate(id_use) +
                    " GROUP BY orders.id_order");
            while (rs1.next()){
                list.add(rs1.getString("orders.id_order") + "~" +
                        rs1.getString("orders.type") + "~" +
                        rs1.getString("stat_ord") + "~" +
                        rs1.getString("c_c"));
            }
            rs1.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInProcOrderClientTable(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            String id_use = "";
            ResultSet rs = statement.executeQuery("Select id_user from users where login = " + quotate(tmp[1]));
            while (rs.next()) {
                id_use = rs.getString("id_user");
            }
            rs.close();
            ResultSet tr = statement.executeQuery("SELECT orders.id_order, orders.type, 'В процессе' as stat_ord, sum(order_product.total_cost) as c_c \n" +
                    "FROM orders JOIN order_product on orders.id_order = order_product.id_order\n" +
                    "WHERE orders.status = '1' and orders.id_user = \n" + quotate(id_use) +
                    " GROUP BY orders.id_order");
            while (tr.next()) {
                list.add(tr.getString("orders.id_order") + "~" +
                        tr.getString("orders.type") + "~" +
                        tr.getString("stat_ord") + "~" +
                        tr.getString("c_c"));
            }
            tr.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCloseOrderClientTable(String[] tmp, ObjectOutputStream soos) {
        try {
            ArrayList<String> list = new ArrayList<>();
            String id_use = "";
            ResultSet rs = statement.executeQuery("Select id_user from users where login = " + quotate(tmp[1]));
            while (rs.next()) {
                id_use = rs.getString("id_user");
            }
            rs.close();
            ResultSet tr = statement.executeQuery("SELECT orders.id_order, orders.type, 'Оплачен' as stat_ord," +
                    " sum(order_product.total_cost) as c_c, orders.date_get \n" +
                    "FROM orders JOIN order_product on orders.id_order = order_product.id_order\n" +
                    "WHERE orders.status = '2' and orders.id_user = \n" + quotate(id_use) +
                    " and orders.date_get BETWEEN\n" + quotate(tmp[2]) + " and " + quotate(tmp[3]) +
                    " GROUP BY orders.id_order");
            while (tr.next()) {
                list.add(tr.getString("orders.id_order") + "~" +
                        tr.getString("orders.type") + "~" +
                        tr.getString("stat_ord") + "~" +
                        tr.getString("c_c") + "~" +
                        tr.getString("orders.date_get"));
            }
            tr.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void infoPeriodSumOrdersButton(String[] tmp, ObjectOutputStream soos) {
        try{
            String list = "";
            String id_use = "";
            ResultSet rs = statement.executeQuery("Select id_user from users where login = " + quotate(tmp[1]));
            while (rs.next()) {
                id_use = rs.getString("id_user");
            }
            rs.close();
            System.out.println("SELECT sum(order_product.total_cost) as c_c\n" +
                    "FROM order_product JOIN orders ON order_product.id_order = orders.id_order\n" +
                    "WHERE orders.id_user = " + quotate(id_use) +" and orders.date_get BETWEEN " + quotate(tmp[2]) + " and " + quotate(tmp[3]));
            ResultSet rt = statement.executeQuery("SELECT sum(order_product.total_cost) as ggg\n" +
                    "FROM order_product JOIN orders ON order_product.id_order = orders.id_order\n" +
                    "WHERE orders.status = '2' and " +
                    " orders.id_user = " + quotate(id_use) +" and orders.date_get BETWEEN " + quotate(tmp[2]) + " and " + quotate(tmp[3]));
            while (rt.next()){
                list = rt.getString("ggg");
            }
            rt.close();
            System.out.println(list);
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void maxProductClient(String[] tmp, ObjectOutputStream soos) {
        try{
            ArrayList<String> list = new ArrayList<>();
            String id_use = "";
            ResultSet rs = statement.executeQuery("Select id_user from users where login = " + quotate(tmp[1]));
            while (rs.next()) {
                id_use = rs.getString("id_user");
            }
            rs.close();
            ResultSet rt = statement.executeQuery("SELECT products.product_name, sum(order_product.number) as c_c FROM \n" +
                    "products JOIN order_product on products.id_product = order_product.id_product\n" +
                    "JOIN orders on order_product.id_order = orders.id_order\n" +
                    "WHERE orders.status = '2' and orders.id_user = \n" + quotate(id_use) +
                    " GROUP BY products.id_product\n" +
                    "ORDER BY c_c DESC\n" +
                    "LIMIT 1");
            while (rt.next()){
               list.add(rt.getString("c_c") + "~" + rt.getString("products.product_name"));
            }
            rt.close();
            soos.writeObject(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
