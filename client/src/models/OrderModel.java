package models;

public class OrderModel {
    private String id_category;
    private String id_product;
    private String category_name;
    private String product_name_order;
    private int number_order;
    private double cost_order;
    private String type_order;

    public OrderModel(String id_category, String id_product, String category_name, String product_name_order, int number_order, double cost_order) {
        this.id_category = id_category;
        this.id_product = id_product;
        this.category_name = category_name;
        this.product_name_order = product_name_order;
        this.number_order = number_order;
        this.cost_order = cost_order;
    }

    public String getId_category() {
        return id_category;
    }

    public void setId_category(String id_category) {
        this.id_category = id_category;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getProduct_name_order() {
        return product_name_order;
    }

    public void setProduct_name_order(String product_name_order) {
        this.product_name_order = product_name_order;
    }

    public int getNumber_order() {
        return number_order;
    }

    public void setNumber_order(int number_order) {
        this.number_order = number_order;
    }

    public double getCost_order() {
        return cost_order;
    }

    public void setCost_order(double cost_order) {
        this.cost_order = cost_order;
    }

    public String getType_order() {
        return type_order;
    }

    public void setType_order(String type_order) {
        this.type_order = type_order;
    }
}
