package models;

public class ProductModel {
    private String catygory_name;
    private String product_info;
    private String id_product;
    private String product_name;
    private double cost;
    private int number_now;
    private int number_max;
    private String category;
    private String id_category;



    public ProductModel(String id_category, String catygory_name, String product_name){
        this.id_category = id_category;
        this.catygory_name = catygory_name;
        this.product_name = product_name;
    }


    public ProductModel(String id_product, String product_name, String product_info, double cost, int number_now, int number_max){
        this.id_product = id_product;
        this.product_name = product_name;
        this.product_info = product_info;
        this.cost = cost;
        this.number_now = number_now;
        this.number_max = number_max;
    }


    public ProductModel(String id_category, String id_product, String product_name, double cost, int number_now){
        this.id_category= id_category;
        this.id_product= id_product;
        this.product_name = product_name;
        this.cost= cost;
        this.number_now = number_now;
    }

    public ProductModel(String id_category, String category_name) {
        this.id_category = id_category;
        this.catygory_name = category_name;
    }




    @Override
    public String toString() {
        return  catygory_name;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getNumber_max() {
        return number_max;
    }

    public void setNumber_max(int number_max) {
        this.number_max = number_max;
    }

    public int getNumber_now() {
        return number_now;
    }

    public void setNumber_now(int number_now) {
        this.number_now = number_now;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId_category() {
        return id_category;
    }

    public void setId_category(String id_category) {
        this.id_category = id_category;
    }


    public String getCatygory_name() {
        return catygory_name;
    }

    public void setCatygory_name(String catygory_name) {
        this.catygory_name = catygory_name;
    }

    public String getProduct_info() {
        return product_info;
    }

    public void setProduct_info(String product_info) {
        this.product_info = product_info;
    }
}
