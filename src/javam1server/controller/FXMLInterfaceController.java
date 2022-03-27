/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1server.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javam1server.MainApp;
import javam1server.model.AccepteClient;
import static javam1server.model.AccepteClient.sockets;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

/**
 *
 * @author Mauyz
 */

public class FXMLInterfaceController implements Initializable {
    @FXML
    private Accordion configPane;
    @FXML
    private TitledPane srvConfig;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField clientMax;    
    @FXML
    private Button applySrvBtn;
    @FXML
    private Button defaultSrvBtn;
    @FXML
    private TitledPane terConfig;
    @FXML
    private ColorPicker fontColor;
    @FXML
    private ChoiceBox<Integer> fontSizeChoice;
    @FXML
    private ComboBox<String> fontChoice;
    @FXML
    private Button applyTerBtn;
    @FXML
    private Button defaultTerBtn;
    @FXML
    private TitledPane dbConfig;
    @FXML
    private TextField dbServer;
    @FXML
    private TextField dbName;
    @FXML
    private TextField dbUser;
    @FXML
    private PasswordField dbPwd;
    @FXML
    private Button applyDbBtn;
    @FXML
    private Button defaultDbBtn;
    @FXML
    private Label etatLbl;
    @FXML
    private ListView<String> clientList;
    @FXML
    private Button startBtn;
    @FXML
    private TextField user;
    @FXML
    private PasswordField pwd;
    @FXML
    private ToolBar mainToolbar;
    @FXML
    private AnchorPane settingPane;
    @FXML
    private TextArea debugTxt;
    @FXML
    private ToolBar menuToolbar;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private AnchorPane authModal;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField loginTxt;
    @FXML
    private PasswordField pwdTxt;
    @FXML
    private Button quitBtn;
    @FXML
    private Button connectBtn;
    @FXML
    private Label errorLbl;
    @FXML
    private Label titleLbl;
    @FXML
    private Button stopBtn;
    @FXML
    private Button windowMin;
    @FXML
    private Button windowMax;
    @FXML
    private Button windowClose;
    @FXML
    private TextField fontTxt;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logs.addListener((ListChangeListener.Change<? extends String> c) -> {
            if(c.next() && c.wasAdded()){
                debugTxt.appendText(logs.get(logs.size()-1));
            }
        });
        
        loadConfig();
        connectMysql();
        completeView();
    }
    
    private void loadConfig(){
        size = FXCollections.observableArrayList();
        int i = 4;
        while(i<41){
            i++;
            size.add(i);
        }
        fontSizeChoice.setItems(size);
        
        fonts = FXCollections.observableArrayList();
        Font.getFamilies().stream().forEach((font) -> {
            fonts.add(font);
        });
        autoCompFont = TextFields.bindAutoCompletion(fontTxt,fonts);
        fontChoice.setItems(fonts);
        fontChoice.addEventHandler(EventType.ROOT, (Event event) -> {
            fontTxt.setText(fontChoice.getSelectionModel().getSelectedItem());
            
        });
        checkConfig();
        repaintConsole();
    }
    
    private void checkConfig(){
        config = new File("socketserver.conf");
                if (config.exists()){
            try {
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "Port":{
                                serverPort.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            case "ClientMax":{
                                clientMax.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            case "User":{
                                user.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            case "Pwd":{
                                pwd.setText(line.substring(line.indexOf("=")+2).trim());
                                break;
                            }
                            case "Font":{
                                fontTxt.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            case "FontSize":{
                                fontSizeChoice.getSelectionModel().select(Integer.valueOf(line.substring(line.indexOf("=")+1).trim()));
                                break;
                            }
                            case "FontColor":{
                                fontColor.setValue(Color.valueOf(line.substring(line.indexOf("=")+1).trim()));
                                break;
                            }
                            
                            case "DBServer":{
                                dbServer.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            case "DBName":{
                                dbName.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            case "DBUser":{
                                dbUser.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            default:{
                                dbPwd.setText(line.substring(line.indexOf("=")+2));
                                break;
                            }
                        }
                    }
                }
            } catch (FileNotFoundException  ex) {
                printLog(ex.toString());
            } catch (IOException ex) {
                printLog(ex.toString());              
            }
        }
        else {
            fontTxt.setText("Consolas");
            fontSizeChoice.getSelectionModel().select(size.get(7));
            fontColor.setValue(Color.BLACK);
            try {
                try (FileWriter fw = new FileWriter(config)) {
                    String str = "Port = "+ serverPort.getText()+"\n"+
                            "ClientMax = "+clientMax.getText()+"\n"+
                            "User = "+user.getText()+"\n"+
                            "Pwd = "+pwd.getText()+"\n"+
                            "Font = "+fontTxt.getText().trim()+"\n"+
                            "FontSize = "+fontSizeChoice.getSelectionModel().getSelectedItem().toString()+"\n"+
                            "FontColor = "+fontColor.getValue().toString().substring(2,8)+"\n"+
                            "DBServer = "+dbServer.getText()+"\n"+
                            "DBName = "+dbName.getText()+"\n"+
                            "DBUser = "+dbUser.getText()+"\n"+
                            "DBPwd = "+dbPwd.getText();
                    fw.write(str);
                }
            } catch (IOException ex) {
                printLog(ex.toString());
            }
        }
        USER = user.getText();
        PASSWD = pwd.getText();
    }
    
    private void repaintConsole(){
        fontChoice.getSelectionModel().select(fontTxt.getText().trim());
        debugTxt.setStyle(""+"-fx-font-size:"+fontSizeChoice.getSelectionModel().getSelectedItem()+";"+
                "-fx-text-fill: #"+fontColor.getValue().toString().substring(2,8)+";"+
                "-fx-font-family:"+fontTxt.getText().trim()+";");
    }
    
    private void reloadSocket(){
        port = Integer.valueOf(serverPort.getText());
        client = Integer.valueOf(clientMax.getText());
        try {
            if( ss != null && !ss.isClosed()){
                ss.close();
                removeSocket();
            }
            ss = null;
            if (stmnt != null && con != null){
                ss = new ServerSocket(port, client);
                new Thread(new AccepteClient(ss,clients)).start();
            }
        }catch(IOException ex){
            printLog(ex.toString());
        }
        if(ss != null){
            printLog("Server is listening on the port : "+ss.getLocalPort());
            etatLbl.setText("Started");
        }
        else{
            printLog("Server is not properly started");
            etatLbl.setText("Stopped");
        }
    }
    
    public static void printLog(String log){
        logs.add("> "+new Date()+": "+log+"\n");
    }
    
    private void connectMysql(){
        DB_SERVER = dbServer.getText();
        DB_NAME = dbName.getText();
        DB_USER = dbUser.getText();
        DB_PWD = dbPwd.getText();
        con = null;
        stmnt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://"+DB_SERVER+":3306/"+DB_NAME,DB_USER,DB_PWD);
            stmnt = con.createStatement();
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException  ex){
            printLog(ex.toString().replace("\n", " "));      
        }
        if(con != null && stmnt != null){
            printLog("Connection on Mysql server succeeded: "+DB_SERVER+":3306/"+DB_NAME);
        }
        else {
            printLog("Connection on Mysql server not succeeded.. Server can't work without database..");
        }
    }
    
    private void completeView(){
        debugTxt.setWrapText(true);
        startBtn.setTooltip(new Tooltip("Start the server"));
        stopBtn.setTooltip(new Tooltip("Stop the server"));
        etatLbl.setTooltip(new Tooltip("Status of the server"));
        clientList.setTooltip(new Tooltip("Connected client list"));
        applySrvBtn.setTooltip(new Tooltip("Apply changes and restart the server"));
        defaultSrvBtn.setTooltip(new Tooltip("Reset default and restart the server"));        
        applyTerBtn.setTooltip(new Tooltip("Apply changes view"));
        defaultTerBtn.setTooltip(new Tooltip("Reset default view"));
        applyDbBtn.setTooltip(new Tooltip("Apply changes and reconnect Mysql"));
        defaultDbBtn.setTooltip(new Tooltip("Reset default and reconnect Mysql"));
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        mainToolbar.getItems().add(spacer1);
        ImageView logo = new ImageView(new Image(MainApp.class.getResourceAsStream("view/images/logo.png")));
        mainToolbar.getItems().add(logo);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        menuToolbar.getItems().add(spacer2);
        Button clear = new Button();
        clear.setId("clearBtn");
        clear.setGraphic(new ImageView(new Image(MainApp.class.getResourceAsStream("view/images/clear.png"))));
        clear.setTooltip(new Tooltip("Clear the terminal"));
        clear.setOnAction((ActionEvent event) -> {
            debugTxt.setText("");
        });
        menuToolbar.getItems().add(clear);
        Button settingBtn = new Button();
        settingBtn.setId("settingBtn");
        settingBtn.setTooltip(new Tooltip("Settings"));
        settingBtn.setGraphic(new ImageView(new Image(MainApp.class.getResourceAsStream("view/images/settings.png"))));
        settingBtn.setOnAction((ActionEvent event) -> {
            hideSetting();
        });
        try{
            aboutPane = FXMLLoader.load(MainApp.class.getResource("view/aboutFXML.fxml"));
            stageAbout.setScene(new Scene(aboutPane));
            stageAbout.initModality(Modality.APPLICATION_MODAL);
        }catch (IOException ex) {
            printLog(ex.toString());
        }
        menuToolbar.getItems().add(settingBtn);
        Button aboutBtn = new Button();
        aboutBtn.setId("aboutBtn");
        aboutBtn.setTooltip(new Tooltip("About"));
        aboutBtn.setGraphic(new ImageView(new Image(MainApp.class.getResourceAsStream("view/images/help.png"))));
        aboutBtn.setOnAction((ActionEvent event) -> {
            stageAbout.showAndWait();
        });
        menuToolbar.getItems().add(aboutBtn);
    }
    
    private void maximizeWindow() {        
        final Screen screen = Screen.getScreensForRectangle(MainApp.stage.getX(), MainApp.stage.getY(), 1, 1).get(0);
        if (maximized) {
            maximized = false;
            if (backupWindowBounds != null) {
                MainApp.stage.setX(backupWindowBounds.getMinX());
                MainApp.stage.setY(backupWindowBounds.getMinY());
                MainApp.stage.setWidth(backupWindowBounds.getWidth());
                MainApp.stage.setHeight(backupWindowBounds.getHeight());
            }
        } else {
            maximized = true;
            backupWindowBounds = new Rectangle2D(MainApp.stage.getX(), MainApp.stage.getY(), MainApp.stage.getWidth(), MainApp.stage.getHeight());
            MainApp.stage.setX(screen.getVisualBounds().getMinX());
            MainApp.stage.setY(screen.getVisualBounds().getMinY());
            MainApp.stage.setWidth(screen.getVisualBounds().getWidth());
            MainApp.stage.setHeight(screen.getVisualBounds().getHeight());
        }
    }
    
    public static void removeSocket() throws IOException{
        if(!sockets.isEmpty()){
            clients.clear();
            for(Socket s: sockets) {
                s.close();
            }
            sockets.clear();
        }
    }
    
    private void stopSocket(){
        if(etatLbl.getText().equals("Started")){
            try {
                ss.close();
                removeSocket();
            } catch (IOException ex) {
                printLog(ex.toString());
            }
            printLog("Server was stopped");
            etatLbl.setText("Stopped");  
            }
        else  printLog("Server has already stopped");
    }
    
    private void showNotifError(String error){
       Notifications.create().owner(settingPane).darkStyle().hideAfter(Duration.seconds(5)).position(Pos.CENTER_RIGHT).text(error).showError();
    }
    
    private void startSocket(){
        if(etatLbl.getText().equals("Stopped")){
            reloadSocket();
         }
        else  printLog("Server has already started");
    }
    
    private void applySocketConf(){
      if(serverPort.getText().matches("[\\d]+") && clientMax.getText().matches("[\\d]+") && !user.getText().isEmpty()){
        if( Integer.valueOf(serverPort.getText()) < 1024 || Integer.valueOf(clientMax.getText()) > 100 ||
                    Integer.valueOf(clientMax.getText()) < 0){
            showNotifError("Invalid entry !");
            }
        else {
            
            int portActuel = port;
            if(!config.exists())checkConfig();
            try {
                String newContent;
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    newContent = "";
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "Port":{
                                newContent += "Port = "+serverPort.getText().trim()+"\n";
                                break;
                            }
                            case "ClientMax":{
                                newContent += "ClientMax = "+clientMax.getText().trim()+"\n";
                                break;
                            }
                            case "User":{
                                newContent +="User = "+user.getText().trim()+"\n";
                                break;
                            }
                            case "Pwd":{
                                newContent +="Pwd = "+pwd.getText().trim()+"\n";
                                break;
                            }
                            default:{
                                newContent += line+"\n";
                                break;
                            }
                        }
                    }
                    in.close();
                }
               try (FileWriter fw = new FileWriter(config)) {
                    fw.write("");
                    fw.write(newContent);
                }
                if(Integer.valueOf(serverPort.getText()) != portActuel){
                    printLog("Server port has changed : " + serverPort.getText());
                }
                } catch (IOException ex) {
                   printLog(ex.toString());
                }
            reloadSocket();
        }
      }
      else showNotifError("Invalid entry !");
    }
    
    private void defaultSocketConf(){
        int portActuel = port;
        if(!config.exists())checkConfig();
            try {
                String newContent;
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    newContent = "";
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "Port":{
                                newContent += "Port = 4000\n";
                                break;
                            }
                            case "ClientMax":{
                                newContent += "ClientMax = 5\n";
                                break;
                            }
                            case "User":{
                                newContent +="User = mauyz\n";
                                break;
                            }
                            case "Pwd":{
                                newContent +="Pwd = pass\n";
                                break;
                            }
                            default:{
                                newContent += line+"\n";
                                break;
                            }
                        }
                    }
                    in.close();
                }
                try (FileWriter fw = new FileWriter(config)) {
                    fw.write("");
                    fw.write(newContent);
                }
                serverPort.setText("4000");
                clientMax.setText("10");
                user.setText("mauyz");
                pwd.setText("pass");
                if(portActuel != 4000){
                    printLog("Server port has changed : 4000");
                }
                } catch (IOException ex) {
                   printLog(ex.toString());
                }
            reloadSocket();
    }
    private void applyTerConf(){
        if(!config.exists())checkConfig();
        try {
           String newContent ;
           try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
               newContent = "";
               while(in.ready()){
                   String line = in.readLine();
                   switch(line.substring(0,line.indexOf("=")).trim()){
                       case "Font":{
                           newContent += "Font = "+fontChoice.getSelectionModel().getSelectedItem()+"\n";
                           break;
                       }
                       case "FontSize":{
                           newContent += "FontSize = "+fontSizeChoice.getSelectionModel().getSelectedItem()+"\n";
                           break;
                       }
                       case "FontColor":{
                           newContent +="FontColor = "+fontColor.getValue().toString().substring(2,8)+"\n";
                           break;
                       }
                       default:{
                           newContent += line+"\n";
                           break;
                       }
                   }
               }
               in.close();
           }
            try (FileWriter fw = new FileWriter(config)) {
                fw.write("");
                fw.write(newContent);
                fw.close();
            }
            repaintConsole();
         } catch (IOException ex) {
            printLog(ex.toString());
        }
    }
    private void defaultTerConf(){
        if(!config.exists())checkConfig();
        try {
            String newContent;
            try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                newContent = "";
                while(in.ready()){
                    String line = in.readLine();
                    switch(line.substring(0,line.indexOf("=")).trim()){
                        case "Font":{
                            newContent += "Font = Consolas\n";
                            break;
                        }
                        case "FontSize":{
                            newContent += "FontSize = 12\n";
                            break;
                        }
                        case "FontColor":{
                            newContent +="FontColor = 000000\n";
                            break;
                        }         
                        default:{
                            newContent += line+"\n";
                            break;
                        }
                    }
                }
                in.close();
            }
                try (FileWriter fw = new FileWriter(config)) {
                    fw.write("");
                    fw.write(newContent);
                    fw.close();
                }
            fontChoice.getSelectionModel().select("Consolas");
            fontSizeChoice.getSelectionModel().select(fontSizeChoice.getItems().get(7));
            fontColor.setValue(Color.BLACK);
            repaintConsole();
         } catch (IOException ex) {
            printLog(ex.toString());
        }        
    }
    
    private void applyDbConf(){
        if(dbServer.getText().isEmpty() || dbName.getText().isEmpty()){
               showNotifError("Invalid entry !");
            }
        else {
            if(!config.exists())checkConfig();
            try {
                if(stmnt != null)stmnt.close();
                String newContent;
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    newContent = "";
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "DBServer":{
                                newContent += "DBServer = "+dbServer.getText().trim()+"\n";
                                break;
                            }
                            case "DBName":{
                                newContent += "DBName = "+dbName.getText().trim()+"\n";
                                break;
                            }
                            case "DBUser":{
                                newContent +="DBUser = "+dbUser.getText().trim()+"\n";
                                break;
                            }
                            case "DBPwd":{
                                newContent +="DBPwd = "+dbPwd.getText()+"\n";
                                break;
                            }
                            default:{
                                newContent += line+"\n";
                                break;
                            }
                        }
                    }
                    in.close();
                }
                try (FileWriter fw = new FileWriter(config)) {
                    fw.write("");
                    fw.write(newContent);
                }
                } catch (IOException | SQLException ex) {
                   printLog(ex.toString());
                }
            connectMysql();
            reloadSocket(); 
        }
    }
    private void defaultDbConf(){
        if(!config.exists())checkConfig();
        try {
                if(stmnt != null)stmnt.close();
                String newContent;
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    newContent = "";
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "DBServer":{
                                newContent += "DBServer = 127.0.0.1\n";
                                break;
                            }
                            case "DBName":{
                                newContent += "DBName = client\n";
                                break;
                            }
                            case "DBUser":{
                                newContent +="DBUser = root\n";
                                break;
                            }
                            case "DBPwd":{
                                newContent +="DBPwd = \n";
                                break;
                            }
                            default:{
                                newContent += line+"\n";
                                break;
                            }
                        }
                    }
                    in.close();
                }
                try (FileWriter fw = new FileWriter(config)) {
                    fw.write("");
                    fw.write(newContent);
                }
                dbServer.setText("127.0.0.1");
                dbName.setText("client");
                dbUser.setText("root");
                dbPwd.setText("");
                } catch (IOException | SQLException ex) {
                   printLog(ex.toString());
                }
        connectMysql();
        reloadSocket();
    }
    
    public void hideLoginPane() {
        rootPane.getChildren().remove(authModal);
    }
    
    @FXML
    private void applySrvAction(ActionEvent event) {
        applySocketConf();
    }

    @FXML
    private void defaultSrvAction(ActionEvent event) {
        defaultSocketConf();
    }

    @FXML
    private void applyTerAction(ActionEvent event) {
        applyTerConf();
    }

    @FXML
    private void defaultTerAction(ActionEvent event) {
        defaultTerConf();
    }

    @FXML
    private void applyDbAction(ActionEvent event) {
        applyDbConf();
    }

    @FXML
    private void defaultDbAction(ActionEvent event) {
        defaultDbConf();
    }
    @FXML
    private void windowMinAction(ActionEvent event) {
        MainApp.stage.setIconified(true);
    }

    @FXML
    private void windowMaxAction(ActionEvent event) {
        maximizeWindow();
    }

    @FXML
    private void windowCloseAction(ActionEvent event) {
        MainApp.close();
    }
    
    
    @FXML
    private void mainToolbarDraggedAction(MouseEvent event) {
        if(!maximized) {
             MainApp.stage.setX(event.getScreenX()- x);
             MainApp.stage.setY(event.getScreenY()- y);
         }
    }

    @FXML
    private void mainToolbarClickedAction(MouseEvent event) {
        if(event.getClickCount() == 2)
            maximizeWindow();
    }

    @FXML
    private void mainToolbarPressedAction(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    
    private void hideSetting() {
        loadConfig();
        settingPane.setVisible(!settingPane.isVisible());
    }
    
    @FXML
    private void startBtnAction(ActionEvent event) {
        startSocket();
    }
    @FXML
    private void quitBtnAction(ActionEvent event) {
        MainApp.close();
    }
    private  boolean isValildLogin(String user, String pass){
        return user.equals(USER) && pass.equals(PASSWD);
    }
    @FXML
    private void connectBtnAction(ActionEvent event) {
        if(isValildLogin(loginTxt.getText(), pwdTxt.getText())) {
        hideLoginPane();
        reloadSocket();
        clientList.setItems(clients);
        clients.addListener((ListChangeListener.Change<? extends String> c) -> {
            if (c.next() && c.wasAdded()){
                printLog(clients.get(clients.size()-1)+" is just connected");
            }
            if(c.wasRemoved()){
                for(String users : c.getRemoved())
                    printLog(users+" is disconnected");
            }  
        });
        }
        else {
            loginTxt.setText("");
            pwdTxt.setText("");
            new Thread(() -> {
                Platform.runLater(() -> {
                    errorLbl.setText("Username or Password incorrect");
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
                Platform.runLater(() -> {
                    errorLbl.setText(null);
                });
            }).start();
        }
    }  

    @FXML
    private void stopBtnAction(ActionEvent event) {
        stopSocket();
    } 
    
    AutoCompletionBinding<String> autoCompFont;
    ObservableList<Integer> size;
    ObservableList<String> fonts;
    Connection con = null;
    
    double x, y;
    boolean maximized = false;
    private Rectangle2D backupWindowBounds = null;
    File config;
    public static int port , client;
    
    public static Stage stageAbout = new Stage(StageStyle.TRANSPARENT);
    AnchorPane aboutPane;
    
    public static ObservableList<String> clients = FXCollections.observableArrayList();
    public static String USER, PASSWD, DB_SERVER, DB_USER, DB_PWD;
    public static ObservableList<String> logs = FXCollections.observableArrayList();
    public static ServerSocket ss  = null;
    public static Statement stmnt = null;
    public static String DB_NAME;
    
}
