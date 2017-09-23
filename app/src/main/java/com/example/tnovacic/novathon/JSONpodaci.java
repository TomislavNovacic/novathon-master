package com.example.tnovacic.novathon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zvjerka on 23.9.2017..
 */

public class JSONpodaci {

    public String pakiranje(String user, String password){
        JSONObject O = new JSONObject();
        try {
            O.put("user:", user);
            O.put("password:", password);
        } catch (JSONException e) {
            return "";
        }
        return O.toString();
    }

    String[][] elementi = {{"VODA","PICE"},
            {"CIPS","HRANA"},
            {"BOMONI","HRANA"},
            {"NESKAFE","HRANA"},
            {"SLADOLED","HRANA"},
            {"DIZEL","GORIVO"},
            {"KROASAN","HRANA"},
            {"STRUJA","RACUNI"},
            {"MAJICA","OSTALO"}};

    public void parsiranje(String str1, String str2, String str3, String str4){
        int i = 0;
        JSONObject obj = new JSONObject();
        double cifra = (Integer.getInteger(str4)/100)*Integer.getInteger(str2);
        if(str1.contains(elementi[i++][0])){
            try {
                obj.put("CIFRA:", cifra);
                obj.put("KATEGORIJA:", elementi[i][1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
