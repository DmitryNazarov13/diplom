package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class Main extends Application {

    static private ObjectOutputStream coos;
    static private ObjectInputStream cois;
    static private Socket clientSocket;

    public static String activeUser = "";

    public void setActiveUser(String temp){
        activeUser = temp;
    }


//    public static String setActiveUser(String temp) {
//         String activeUser = temp;
//        return activeUser;
//    }

    public static String checkRole() {
        System.out.println("checkRole" + "~" + activeUser);
        String activeRole = (String) Main.getObjectFromServer("checkRole" + "~" + activeUser);
        return activeRole;
    }




    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Authorization.fxml")));
        primaryStage.setResizable(true);
        primaryStage.setTitle("Вход");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        try {
            System.out.println("Соединение с сервером..");
            clientSocket = new Socket("127.0.0.1", 2522);
            System.out.println("Соединение установлено....");
            coos = new ObjectOutputStream(clientSocket.getOutputStream());
            cois = new ObjectInputStream(clientSocket.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
        launch(args);
    }

    public static void autoResizeColumns(TableView<?> table){
        //Set the right policy
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().stream().forEach( (column) ->
        {
            //Minimal width = columnheader
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ )
            {
                //cell must not be empty
                if ( column.getCellData( i ) != null )
                {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();
                    //remember new max-width
                    if ( calcwidth > max )
                    {
                        max = calcwidth;
                    }
                }
            }
            //set the new max-widht with some extra space
            column.setPrefWidth( max + 10.0d );
        } );
    }

    public void allertError( String titleTextAlert, String headerTextAlert, String contentTextAlert){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titleTextAlert);
        alert.setHeaderText(headerTextAlert);
        alert.setContentText(contentTextAlert);
        alert.showAndWait();
    }

    public void allertInfo (String titleTextAlert, String headerTextAlert){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle((titleTextAlert));
        alert.setHeaderText(headerTextAlert);
        alert.showAndWait();
    }

    public void openNewWindow(Stage stage,boolean isCurrentClose,String path,String title,boolean isModal){
        try {
            if(isCurrentClose) stage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Parent root1 = (Parent) fxmlLoader.load();
            stage = new Stage();
            if(isModal) stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setTitle(title);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String sendToServerString(String sendMessages) {
        String receive = "";
        try{
            coos.writeObject(sendMessages);
            receive = (String)cois.readObject();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

        return receive;
    }

    public static Object getObjectFromServer(String sendMessage)  {
        Object obj = null;
        try{
            coos.writeObject(sendMessage);
            obj = cois.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return obj;
    }
}
