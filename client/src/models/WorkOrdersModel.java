package models;

public class WorkOrdersModel {
    private int id_order;
    private String login;
    private String type;
    private String status;
    private String date_get;
    private Double total_cost;


    public WorkOrdersModel(int id_order, String login, String type, String status, Double total_cost) {
        this.id_order = id_order;
        this.login = login;
        this.type = type;
        this.status = status;
        this.total_cost = total_cost;
    }

    public WorkOrdersModel(int id_order, String login, String type, String status, String date_get, Double total_cost) {
        this.id_order = id_order;
        this.login = login;
        this.type = type;
        this.status = status;
        this.date_get = date_get;
        this.total_cost = total_cost;
    }

    public WorkOrdersModel(int id_order, String type, String status, Double total_cost) {
        this.id_order = id_order;
        this.type = type;
        this.status = status;
        this.total_cost = total_cost;
    }

    public WorkOrdersModel(int id_order, String type, String status, Double total_cost, String date_get) {
        this.id_order = id_order;
        this.type = type;
        this.status = status;
        this.total_cost = total_cost;
        this.date_get = date_get;
    }

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_get() {
        return date_get;
    }

    public void setDate_get(String date_get) {
        this.date_get = date_get;
    }

    public Double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(Double total_cost) {
        this.total_cost = total_cost;
    }
}
