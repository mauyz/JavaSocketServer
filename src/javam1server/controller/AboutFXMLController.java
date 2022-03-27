/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1server.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Mauyz
 */
public class AboutFXMLController implements Initializable {
    
    @FXML
    private Button closeBtn;
    @FXML
    private AnchorPane aboutPane;
    @FXML
    private Hyperlink fbLink;
    @FXML
    private Hyperlink googleLink;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    private void browse(String  url){
        if(Desktop.isDesktopSupported())
            try {
                Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
        }
    }
    @FXML
    private void closeBtnAction(ActionEvent event) {
        FXMLInterfaceController.stageAbout.close();
    }
    
    double x, y;
    @FXML
    private void dragAction(MouseEvent event) {
        FXMLInterfaceController.stageAbout.setX(event.getScreenX()-x);
        FXMLInterfaceController.stageAbout.setY(event.getScreenY()-y);
    }

    @FXML
    private void pressAction(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    private void fbLinkAction(ActionEvent event) {
       browse("https://www.facebook.com/pierremoise.tsiorinambinina");
    }

    @FXML
    private void googleLinkAction(ActionEvent event) {
        browse("https://mail.google.com");
    }
}
