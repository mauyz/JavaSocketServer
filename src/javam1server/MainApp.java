/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1server;

import java.awt.Dimension;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javam1server.controller.FXMLInterfaceController;
import static javam1server.controller.FXMLInterfaceController.removeSocket;
import static javam1server.controller.FXMLInterfaceController.ss;
import static javam1server.controller.FXMLInterfaceController.stmnt;

/**
 *
 * @author Mauyz
 */
public class MainApp extends Application {
    
    public static Stage stage;
    public static Scene scene;
    public static double width, height;
    
    @Override
    public void start(Stage fen) throws Exception {
        AnchorPane root = FXMLLoader.load(MainApp.class.getResource("view/InterfaceFXML.fxml"));
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(screen.width - 500,screen.height-200);
        width = size.width ; 
        height = size.height;
        fen.initStyle(StageStyle.UNDECORATED);
        root.setPrefSize(width, height);
        scene = new Scene(root,width,height);
        fen.setScene(scene);
        fen.setOnCloseRequest((WindowEvent event) -> {
            close();
        });
        stage = fen;
        fen.show();
    }
    
    public static  void close(){
        try {
            removeSocket();
            if(FXMLInterfaceController.ss != null)ss.close();
            if(FXMLInterfaceController.stmnt != null)stmnt.close();               
         } catch (IOException | SQLException ex) {   
         }
        Platform.exit();
    }
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        launch(args);
    }
}
