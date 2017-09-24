package com.example.tnovacic.novathon;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microblink.recognizers.blinkinput.BlinkInputRecognizerSettings;
import com.microblink.recognizers.blinkocr.engine.BlinkOCREngineOptions;
import com.microblink.recognizers.blinkocr.parser.generic.RawParserSettings;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText user, password;
    Intent Activity2;

    private static final String LICENSE_KEY = "QIFSS7YI-G2WAMKWX-LPZUXECB-2BLCYTRR-JDLBGALA-ZNZMCVRM-JYYUQ5VL-EQQDCSWM";

    private static final int REQ_CODE_OCR = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (EditText) findViewById(R.id.user_login);
        password = (EditText) findViewById(R.id.password_login);
        Activity2 = new Intent(this, Activity2.class);

        Button btn = (Button) findViewById(R.id.login_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logiranje(v);
            }
        });
    }

    public void logiranje(View v){
        JSONpodaci JS = new JSONpodaci();

        if( !(user.getText().equals("")) && !(password.getText().equals(""))){
            pakiranje(user.getText().toString(), password.getText().toString());
        }

    }

    public void startScanning(View view) {
        Intent fullScreenIntent = new Intent(this, FullScreenOCR.class);
        fullScreenIntent.putExtra(IntentConstants.EXTRAS_LICENSE_KEY, LICENSE_KEY);
        BlinkInputRecognizerSettings ocrSett = new BlinkInputRecognizerSettings();
        RawParserSettings rawSett = new RawParserSettings();

        BlinkOCREngineOptions engineOptions = new BlinkOCREngineOptions();

        rawSett.setOcrEngineOptions(engineOptions);

        ocrSett.addParser("Raw", rawSett);

        RecognitionSettings settings = new RecognitionSettings();
        settings.setRecognizerSettingsArray(new RecognizerSettings[]{ocrSett});
        fullScreenIntent.putExtra(IntentConstants.EXTRAS_RECOGNITION_SETTINGS, settings);
        try {
            startActivityForResult(fullScreenIntent, REQ_CODE_OCR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class slanje1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://10.20.0.89/web-api/login");
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

    podaci pod;
    char s[] = new char[250];
    String fini = null;
    String data;

    public void salji1(String s){
        this.data = s;
        new slanje1().execute();
    }

    public boolean pakiranje(String user, String password){
        salji1("email="+user+"&password="+password);
        return true;
    }

    public void pasiranje(String s){

        int id,post, income;
        String ime, prezime, adresa,mail,
        rodendan, phone;
        try {
            JSONObject S = new JSONObject(s);

            if(S.getString("result") == "success")
            id = S.getInt("id");
            ime = S.getString("first_name");
            prezime = S.getString("last_name");
            adresa = S.getString("user_address");
            post = S.getInt("post_number");
            mail = S.getString("email");
            rodendan = S.getString("birthday");
            phone = S.getString("phone");
            income = S.getInt("income");

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
            startActivity(Activity2);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Greska pri pasiranjz");
        }
    }
}
