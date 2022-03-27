/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1server.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javam1server.controller.FXMLInterfaceController;
import static javam1server.controller.FXMLInterfaceController.printLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author Mauyz
 */

public class DaoClient {
    
    public Statement st;
    
    public DaoClient( Statement stmnt){
        st = stmnt;
    }

    public JSONObject execute(JSONObject obj){
        
        JSONObject res = new JSONObject();
        String type = obj.get("Type").toString();
        switch(type){
            case "Insert":{
                boolean retour = false;
                String query = "INSERT INTO "+FXMLInterfaceController.DB_NAME+" (nom,adresse,solde) VALUES ('"+obj.get("nom").toString()+"','"
                    +obj.get("adresse").toString()+"','"+Integer.valueOf(obj.get("solde").toString())+"')";
            try {
                retour = st.execute(query);
            } catch (SQLException ex) {
                printLog(ex.getMessage());
                res.put("Retour",ex.getMessage());
            }
                if(!retour) res.put("Retour","Client added");
                else res.put("Retour","Client not added");    
                break;
            }
            
            case "Update":{
                int retour = 0 ;
                String query = "UPDATE "+FXMLInterfaceController.DB_NAME+" SET nom = '"+obj.get("nom").toString()+"',adresse = '"
                    +obj.get("adresse").toString()+"',solde = '"+Integer.valueOf(obj.get("solde").toString())+"' WHERE numero = "+Integer.valueOf(obj.get("numero").toString());
                
            try {
                retour = st.executeUpdate(query);
            } catch (SQLException ex) {
                printLog(ex.getMessage());
                res.put("Retour",ex.getMessage());
            }
                if(retour == 1) res.put("Retour","Client updated");
                else res.put("Retour","Client not updated");
                    
                break;
            }
            case "Delete":{
                int retour = 0 ;
                String query = "DELETE FROM "+FXMLInterfaceController.DB_NAME+" WHERE numero = "+Integer.valueOf(obj.get("numero").toString());
            try {
                retour = st.executeUpdate(query);
            } catch (SQLException ex) {
                printLog(ex.getMessage());
                res.put("Retour",ex.getMessage());
            }
                if(retour == 1) res.put("Retour","Client deleted");
                else res.put("Retour","Client not deleted");
                break;
            }
            case"Select":{
                JSONObject client = new JSONObject();
                JSONArray list =  new JSONArray();
                
                String select = "SELECT * FROM "+FXMLInterfaceController.DB_NAME;
                try{
                    ResultSet rs = st.executeQuery(select);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while(rs.next()){
                        for(int i = 1 ; i <= rsmd.getColumnCount(); i++){
                            client.put(rsmd.getColumnName(i),rs.getObject(i));
                        }
                        list.add(client);
                    }
                }catch (SQLException ex){
                    printLog(ex.getMessage());
                }
                res.put("Client list", list);
                break;
            }
            default:{
                res.put("Retour","Invalid query");
                break;
            }
        }
        
        return res;
    }
}
