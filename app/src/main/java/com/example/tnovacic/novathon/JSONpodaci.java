package com.example.tnovacic.novathon;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zvjerka on 23.9.2017..
 */

public class JSONpodaci {

    public String pakiranje(String user, String password){
        JSONObject O = new JSONObject();
        try {
            O.put("user", user);
            O.put("password", password);
        } catch (JSONException e) {
            return "";
        }
        return O.toString();
    }
}
