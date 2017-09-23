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
        salji(O.toString());
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


    char s[] = new char[100];
    String fini = null;
    String data;

    public void salji(String data) {
        this.data = data;
        new slanje().execute();
    }

    class podaci{
        int id;
        String ime, prezime, adresa;
        int post;
        String mail, phone, rodendan;
        int income;
    }

    public void pasiranje(String s){
        try {
            JSONObject S = new JSONObject(s);
            podaci pod = new podaci();
            pod.id = S.getInt("id");
            pod.ime = S.getString("first_name");
            pod.prezime = S.getString("last_name");
            pod.adresa = S.getString("user_adress");
            pod.post = S.getInt("post_number");
            pod.mail = S.getString("email");
            pod.rodendan = S.getString("birthday");
            pod.phone = S.getString("phone");
            pod.income = S.getInt("income");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class slanje extends AsyncTask<Void ,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try{
                        URL url = new URL("http://10.20.0.89/web-api/test");
                        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                        httpCon.setDoOutput(true);
                        httpCon.setRequestMethod("POST");

                        OutputStreamWriter out = new OutputStreamWriter(
                                httpCon.getOutputStream());
                        System.out.println(httpCon.getResponseCode());
                        System.out.println(httpCon.getResponseMessage());
                        out.close();
                        InputStreamReader in = new InputStreamReader(httpCon.getInputStream());
                        System.out.println(in.read(s,0, 100));
                        in.close();
            }catch (Exception e){
                fini = "false";
            }
            fini = String.copyValueOf(s);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            pasiranje(fini);
        }
    }
}
