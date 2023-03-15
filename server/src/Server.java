import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class Server extends Thread {
    private DatabaseHandler myDataBase;
    private Socket clientSocket;
    private ObjectInputStream sois;
    private ObjectOutputStream soos;
    private String[] tmp;
    private String action;



    public Server(Socket socket) throws IOException {
        clientSocket = socket;
        sois = new ObjectInputStream(clientSocket.getInputStream());
        soos = new ObjectOutputStream(clientSocket.getOutputStream());

        start();
    }

    public void run() {
        myDataBase = new DatabaseHandler();
        try {
            System.out.println("Соединение установлено..");

            while (true)    {
                action = (String) sois.readObject();
                tmp = action.split("~");

                switch (tmp[0]) {
                    case "authorization" :
                        myDataBase.authorization(tmp,soos);
                        break;
                    case "checkRole":
                        myDataBase.checkRole(tmp,soos);
                        break;
                    case "loadCategoryBox":
                        myDataBase.loadCategoryBox(tmp,soos);
                        break;
                    case "loadProductTableFromCategory":
                        myDataBase.loadProductTableFromCategory(tmp,soos);
                        break;
                    case "createOrder":
                        myDataBase.createOrder(tmp,soos);
                        break;
                    case "crateOrderProduct":
                        myDataBase.crateOrderProduct(tmp,soos);
                        break;
                    case "loadNewOrdersWorkTable":
                        myDataBase.loadNewOrdersWorkTable(tmp,soos);
                        break;
                    case "loadPreProcOrdersWorkTable":
                        myDataBase.loadPreProcOrdersWorkTable(tmp,soos);
                        break;
                    case "loadCloseOrdersWorkTable":
                        myDataBase.loadCloseOrdersWorkTable(tmp,soos);
                        break;
                    case "payOrder":
                        myDataBase.payOrder(tmp,soos);
                        break;
                    case "confimOrder":
                        myDataBase.confimOrder(tmp,soos);
                        break;
                    case "declineOrder":
                        myDataBase.declineOrder(tmp,soos);
                        break;
                    case "declinePaymentOrder":
                        myDataBase.declinePaymentOrder(tmp,soos);
                        break;
                    case "addNewCategory":
                        myDataBase.addNewCategory(tmp,soos);
                        break;
                    case "loadCategoryProdTable":
                        myDataBase.loadCategoryProdTable(tmp,soos);
                        break;
                    case "loadCategWithoutProd":
                        myDataBase.loadCategWithoutProd(tmp,soos);
                        break;
                    case "loadAllProducts":
                        myDataBase.loadAllProducts(tmp,soos);
                        break;
                    case "loadProdWithoutCateg":
                        myDataBase.loadProdWithoutCateg(tmp,soos);
                        break;
                    case "deleteCategory":
                        myDataBase.deleteCategory(tmp,soos);
                        break;
                    case "registration":
                        myDataBase.registration(tmp,soos);
                        break;
                    case "loadAllUsers":
                        myDataBase.loadAllUsers(tmp,soos);
                        break;
                    case "laodChekBoxRoles":
                        myDataBase.laodChekBoxRoles(tmp,soos);
                        break;
                    case "deleteUser":
                        myDataBase.deleteUser(tmp,soos);
                        break;
                    case "getOrderItems":
                        myDataBase.getOrderItems(tmp,soos);
                        break;
                    case "addProdToCateg":
                        myDataBase.addProdToCateg(tmp,soos);
                        break;
                    case "deleteProduct":
                        myDataBase.deleteProduct(tmp,soos);
                        break;
                    case "addProduct":
                        myDataBase.addProduct(tmp,soos);
                        break;
                    case "deleteProdFromCateg":
                        myDataBase.deleteProdFromCateg(tmp,soos);
                        break;
                    case "pieChartIncomeCategory":
                        myDataBase.pieChartIncomeCategory(tmp,soos);
                        break;
                    case "addProdToSklad":
                        myDataBase.addProdToSklad(tmp,soos);
                        break;
                    case "pieChartProductSels":
                        myDataBase.pieChartProductSels(tmp,soos);
                        break;
                    case "pieChartClientProfit":
                        myDataBase.pieChartClientProfit(tmp,soos);
                        break;
                    case "lineChartOrders":
                        myDataBase.lineChartOrders(tmp,soos);
                        break;
                    case "lineChartIncome":
                        myDataBase.lineChartIncome(tmp,soos);
                        break;
                    case "loadNewOrderClientTable":
                        myDataBase.loadNewOrderClientTable(tmp,soos);
                        break;
                    case "loadInProcOrderClientTable":
                        myDataBase.loadInProcOrderClientTable(tmp,soos);
                        break;
                    case "loadCloseOrderClientTable" :
                        myDataBase.loadCloseOrderClientTable(tmp,soos);
                        break;
                    case "infoPeriodSumOrdersButton":
                        myDataBase.infoPeriodSumOrdersButton(tmp,soos);
                        break;
                    case "maxProductClient":
                        myDataBase.maxProductClient(tmp,soos);
                        break;
                }

            }

        } catch (Exception e) {
            System.err.println("Клиент отключился.");
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Сокет не закрыт!!!");
            }
        }

    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(2522);
        System.out.println("Сервер запущен...");

        try {
            while (true) {
                Socket newSocket = serverSocket.accept();
                System.out.println("Новый клиент подключился");

                try {
                    new Server(newSocket);
                } catch (Exception e) {
                    newSocket.close();
                }
            }

        } finally {
            serverSocket.close();
        }
    }
}


