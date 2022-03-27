/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;
import javam1server.controller.FXMLInterfaceController;
import static javam1server.controller.FXMLInterfaceController.*;
import static javam1server.model.AccepteClient.sockets;
import net.sf.json.JSONObject;

/**
 *
 * @author Mauyz
 */

public class ReceiveReplyRequest implements Runnable{
    
    private BufferedReader in;
    private JSONObject come = new JSONObject(), send = new JSONObject();
    private Socket socket = null;
    private PrintWriter out;
    
    public ReceiveReplyRequest(Socket sock){
        socket = sock;
    }
    
    String line;
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new  PrintWriter(socket.getOutputStream());
            out.println("JavaM1");
            out.flush();
            Platform.runLater(() -> {
                clients.add(sockets.get(sockets.size()-1).getInetAddress().getHostName());
            });
            
        }catch(IOException e){}
        
        while(!socket.isClosed()){
            try{
                line = in.readLine();
                if(line != null){
                    Object obj = line;
                    printLog("From "+socket.getInetAddress().getHostAddress()+": "+line);
                    if(obj.toString().length() > 5 ){
                        if(obj.toString().substring(0,6).equals("{\"Type")){
                            come = JSONObject.fromObject(obj);
                            send = new DaoClient(FXMLInterfaceController.stmnt).execute(come);
                            out.println(send);
                            out.flush();
                        }
                        else{
                            send.put("Retour", "Invalid query");
                            out.println(send);
                            out.flush();
                            printLog("To "+socket.getInetAddress().getHostAddress()+": "+send);
                        }
                    }
                    else{
                            send.put("Retour", "Invalid query");
                            out.println(send);
                            out.flush();
                            printLog("To "+socket.getInetAddress().getHostAddress()+": "+send);
                    }
                }
                else break;
            } catch (IOException ex) {}
        }
        if(!socket.isClosed())
        {
            Platform.runLater(() -> {
                clients.remove(sockets.indexOf(socket));
                try{
                socket.close();
                sockets.remove(sockets.indexOf(socket));
                }catch(IOException e){}
            });
        }
    }
}

