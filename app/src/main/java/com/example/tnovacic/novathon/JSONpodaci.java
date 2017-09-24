package com.example.tnovacic.novathon;


import android.os.AsyncTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zvjerka on 23.9.2017..
 */

public class JSONpodaci {

    public boolean pakiranje(String user, String password){
        salji1("email="+user+"&password="+password);
        return true;
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


    char s[] = new char[250];
    String fini = null;
    String data;
    boolean test = false;


    public boolean salji1(String s){
        this.data = s;
        new slanje1().execute();
        return true;
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
            pod.adresa = S.getString("user_address");
            pod.post = S.getInt("post_number");
            pod.mail = S.getString("email");
            pod.rodendan = S.getString("birthday");
            pod.phone = S.getString("phone");
            pod.income = S.getInt("income");
            System.out.println("Uspjesno");
            test = true;
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Greska pri pasiranjz");
        }

    }

    class slanje1 extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://10.20.0.89/web-api/test");
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(
                        httpCon.getOutputStream());
                out.write(data);
                out.flush();
                System.out.println(httpCon.getResponseCode());
                System.out.println(httpCon.getResponseMessage());
                out.close();
                InputStreamReader in = new InputStreamReader(httpCon.getInputStream());
                System.out.println(in.read(s,0, 250));
                //System.out.println(s);
                in.close();

            }catch (Exception e){System.out.println("greska pri primanju");}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pasiranje(String.valueOf(s));
        }
    }
}
