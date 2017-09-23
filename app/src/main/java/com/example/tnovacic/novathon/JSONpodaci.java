package com.example.tnovacic.novathon;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microblink.results.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zvjerka on 23.9.2017..
 */

public class JSONpodaci {

    public void pakiranje(String user, String password){
        JSONObject O = new JSONObject();
        try {
            O.put("user:", user);
            O.put("password:", password);
        } catch (JSONException e) {

        }
        //return salji(O.toString());
    }

    String[][] elementi = {{"VODA","PICE"},
            {"CIPS","HRANA"},
            {"SOK","PICE"},
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
        double cifra = ((double)Integer.getInteger(str4)/100)*Integer.getInteger(str2);
        if(str1.contains(elementi[i++][0])){
            try {
                obj.put("CIFRA:", cifra);
                obj.put("KATEGORIJA:", elementi[i][1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //final static String url = "http://10.20.0.89/web-api/test";
    String fini = null;
    String data;
    public String salji(String data) {
        this.data = data;
        new slanje().execute();
        return fini;
    }

    class slanje extends AsyncTask<Void ,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try{
                        URL url = new URL("http://10.20.0.89/web-api/test");
                        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                        httpCon.setDoOutput(true);
                        httpCon.setRequestMethod("POST");
                        //InputStreamReader in = new InputStreamReader(httpCon.getInputStream());
                        OutputStreamWriter out = new OutputStreamWriter(
                                httpCon.getOutputStream());
                        System.out.println(httpCon.getResponseCode());
                        System.out.println(httpCon.getResponseMessage());
                        out.close();
                        //fini = in.toString();
                        //in.close();


            }catch (Exception e){
                fini = "false";
            }
            return null;
        }
    }
}
