/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Mauyz
 */
public class AccepteClient implements Runnable{
    
    private ServerSocket socketserver = null;
    private ObservableList<String> clients = FXCollections.observableArrayList();
    
    public static ObservableList<Socket> sockets = FXCollections.observableArrayList();
    
    public AccepteClient(ServerSocket s,ObservableList<String> listSocket){
        socketserver = s;
        clients = listSocket;
       
    }
 
    @Override
    public void run() {
          while(!socketserver.isClosed()){
            try {
                  sockets.add(socketserver.accept());
                  new Thread(new ReceiveReplyRequest(sockets.get(sockets.size()-1))).start();
              } catch (IOException ex) {
                  break;
              }
          }
    }
}
