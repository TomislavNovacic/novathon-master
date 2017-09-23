package com.example.tnovacic.novathon;

import android.content.Intent;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText user, password;
    Intent Activity2;
    String proizvodsZarezom1;
    String proizvodsZarezom2;

    private static final String LICENSE_KEY = "QIFSS7YI-G2WAMKWX-LPZUXECB-2BLCYTRR-JDLBGALA-ZNZMCVRM-JYYUQ5VL-EQQDCSWM";

    private static final int REQ_CODE_OCR = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (EditText) findViewById(R.id.user_login);
        password = (EditText) findViewById(R.id.password_login);
        Activity2 = new Intent(this, Activity2.class);

        Button gumb = (Button) findViewById(R.id.btnStart);
        gumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanning(v);
            }
        });
    }

    public void logiranje(View v){
        JSONpodaci JS = new JSONpodaci();

        if( (user.getText().equals("")) && (password.getText().equals(""))){
            String zaslanje = JS.pakiranje(user.getText().toString(), password.getText().toString());
        }
        startActivity(Activity2);
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

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            String podaci = data.getExtras().getString("EXTRA_RES");
            String s[] = podaci.split("\n");
            ArrayList<String> listaProizvoda = new ArrayList<>();
            for (String proizvod: s) {
                if(proizvod.contains("VODA") || proizvod.contains("CIPS") || proizvod.contains("BOM") || proizvod.contains("KROASAN") || proizvod.contains("SLAD") || proizvod.contains("MIX")) {

                    listaProizvoda.add(proizvod);
                }
            }

            ArrayList<String> finalnaListaProizvoda = new ArrayList<>();
            for (String proizvod: listaProizvoda) {
                String nazivProizvoda = "";
                String proizvodsaSpaceom[] = proizvod.split(" +");
                int proizvodSize = proizvodsaSpaceom.length;
                for (int i = 0; i < proizvodSize; i++ ) {
                    if(!(proizvodsaSpaceom[i].equals(proizvodsaSpaceom[proizvodSize - 1])) && !(proizvodsaSpaceom[i].equals(proizvodsaSpaceom[proizvodSize - 2])) && !(proizvodsaSpaceom[i].equals(proizvodsaSpaceom[proizvodSize - 3]))) {
                        nazivProizvoda = nazivProizvoda +" " + proizvodsaSpaceom[i];
                    }
                }

                if(nazivProizvoda.contains("6")) {
                    int duljina = nazivProizvoda.length() - 1;
                    nazivProizvoda = nazivProizvoda.substring(0, duljina);
                    nazivProizvoda = nazivProizvoda + "G";
                }

                finalnaListaProizvoda.add(nazivProizvoda.trim());
                finalnaListaProizvoda.add(proizvodsaSpaceom[proizvodSize -3]);

                if(!(proizvodsaSpaceom[proizvodSize -2].contains(","))) {
                    proizvodsZarezom1 = "";
                    int duljina = proizvodsaSpaceom[proizvodSize -2].length() - 2;
                    proizvodsZarezom1 = proizvodsaSpaceom[proizvodSize -2].substring(0,duljina);
                    proizvodsZarezom1 = proizvodsZarezom1 + "," + proizvodsaSpaceom[proizvodSize -2].substring(duljina,duljina + 1);
                    if(!(proizvodsaSpaceom[proizvodSize -2].contains("O")) || proizvodsaSpaceom[proizvodSize -2].contains("o") || (proizvodsaSpaceom[proizvodSize -2].length() <= 4) && proizvodsaSpaceom[proizvodSize -2].contains(",") || (proizvodsaSpaceom[proizvodSize -2].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -2].contains(","))) {
                        proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2].substring(0,2);
                        proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2] + "00";
                    }
                    finalnaListaProizvoda.add(proizvodsZarezom1.trim());
                }
                else {
                    if(!(proizvodsaSpaceom[proizvodSize -2].contains("O")) || proizvodsaSpaceom[proizvodSize -2].contains("o") || (proizvodsaSpaceom[proizvodSize -2].length() <= 4) && proizvodsaSpaceom[proizvodSize -2].contains(",") || (proizvodsaSpaceom[proizvodSize -2].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -2].contains(",")) ) {
                        proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2].substring(0,2);
                        proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2] + "00";
                    }
                    finalnaListaProizvoda.add(proizvodsaSpaceom[proizvodSize -2]);
                }




                if(!(proizvodsaSpaceom[proizvodSize -1].contains(","))) {
                    proizvodsZarezom2 = "";
                    int duljina = proizvodsaSpaceom[proizvodSize -1].length() - 2;
                    proizvodsZarezom2 = proizvodsaSpaceom[proizvodSize -1].substring(0,duljina);
                    proizvodsZarezom2 = proizvodsZarezom2 + "," + proizvodsaSpaceom[proizvodSize -1].substring(duljina,duljina + 1);
                    if(!(proizvodsaSpaceom[proizvodSize -1].contains("O")) || proizvodsaSpaceom[proizvodSize -1].contains("o")|| (proizvodsaSpaceom[proizvodSize -1].length() <= 4) && proizvodsaSpaceom[proizvodSize -1].contains(",") || (proizvodsaSpaceom[proizvodSize -1].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -1].contains(","))) {
                        proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1].substring(0,2);
                        proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1] + "00";
                    }
                    finalnaListaProizvoda.add(proizvodsZarezom2.trim());
                }
                else {
                    if(!(proizvodsaSpaceom[proizvodSize -1].contains("O")) || proizvodsaSpaceom[proizvodSize -1].contains("o") || (proizvodsaSpaceom[proizvodSize -1].length() <= 4) && proizvodsaSpaceom[proizvodSize -1].contains(",") || (proizvodsaSpaceom[proizvodSize -1].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -1].contains(",")) ) {
                        proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1].substring(0,2);
                        proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1] + "00";
                    }
                    finalnaListaProizvoda.add(proizvodsaSpaceom[proizvodSize -1]);
                }
            }
            Log.w(this, "PROBA Result is: {}", finalnaListaProizvoda);
        }
}
