package com.example.tnovacic.novathon;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microblink.recognizers.blinkinput.BlinkInputRecognizerSettings;
import com.microblink.recognizers.blinkocr.engine.BlinkOCREngineOptions;
import com.microblink.recognizers.blinkocr.parser.generic.RawParserSettings;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class Activity2 extends AppCompatActivity {

    private static final String LICENSE_KEY = "QIFSS7YI-G2WAMKWX-LPZUXECB-2BLCYTRR-JDLBGALA-ZNZMCVRM-JYYUQ5VL-EQQDCSWM";

    private static final int REQ_CODE_OCR = 234;

    LinearLayout[] bars = new LinearLayout[5];
    FloatingActionButton float_but;
    String proizvodsZarezom1;
    String proizvodsZarezom2;
    TextView inputFood, user_txt;
    TextView inputDrinks;
    TextView inputBills;
    TextView input1Fuel;
    TextView inputOther;
    TextView inputRemaining;
    podaci pod = new podaci();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        JSONpodaci npodaci = new JSONpodaci();
        bars[0] = (LinearLayout) findViewById(R.id.food_bar);
        bars[1] = (LinearLayout) findViewById(R.id.drinks_bar);
        bars[2] = (LinearLayout) findViewById(R.id.bills_bar);
        bars[3] = (LinearLayout) findViewById(R.id.fuel_bar);
        bars[4] = (LinearLayout) findViewById(R.id.other_bar);
        user_txt = (TextView) findViewById(R.id.user_txt);
        user_txt.setText(pod.ime+ " " + pod.prezime+"' stats");
       // bars[5] = (LinearLayout) findViewById(R.id.other_bar);


        bars[0].setBackgroundColor(Color.parseColor("#3F51B5"));
        bars[1].setBackgroundColor(Color.parseColor("#4CAF50"));
        bars[2].setBackgroundColor(Color.parseColor("#795548"));
        bars[3].setBackgroundColor(Color.parseColor("#795548"));
        bars[4].setBackgroundColor(Color.parseColor("#607D8B"));
       // bars[5].setBackgroundColor(Color.parseColor("#BDBDBD"));

        inputFood = (TextView) findViewById(R.id.food_num);
        inputDrinks = (TextView) findViewById(R.id.textView3);
        inputBills = (TextView) findViewById(R.id.textView5);
        input1Fuel = (TextView) findViewById(R.id.textView7);
        inputOther = (TextView) findViewById(R.id.textView8);
        inputRemaining = (TextView) findViewById(R.id.textView12);

        float_but = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        float_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanning(v);
            }
        });

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

        ArrayList<Proizvod> finalnaListaProizvoda = new ArrayList<>();
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

            Proizvod proizvodObjekt = new Proizvod();
            proizvodObjekt.setNazivProizvoda(nazivProizvoda.trim());
            proizvodObjekt.setKolicina(proizvodsaSpaceom[proizvodSize -3]);

          //  finalnaListaProizvoda.add(nazivProizvoda.trim());
          //  finalnaListaProizvoda.add(proizvodsaSpaceom[proizvodSize -3]);
                proizvodsZarezom1 = "";
            if(!(proizvodsaSpaceom[proizvodSize -2].contains(","))) {
                proizvodsZarezom1 = "";
                int duljina = proizvodsaSpaceom[proizvodSize -2].length() - 2;
                proizvodsZarezom1 = proizvodsaSpaceom[proizvodSize -2].substring(0,duljina);
                proizvodsZarezom1 = proizvodsZarezom1 + "," + proizvodsaSpaceom[proizvodSize -2].substring(duljina,duljina + 1);
                if(!(proizvodsaSpaceom[proizvodSize -2].contains("O")) || proizvodsaSpaceom[proizvodSize -2].contains("o") || (proizvodsaSpaceom[proizvodSize -2].length() <= 4) && proizvodsaSpaceom[proizvodSize -2].contains(",") || (proizvodsaSpaceom[proizvodSize -2].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -2].contains(","))) {
                    proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2].substring(0,2);
                    proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2] + "00";
                }
                proizvodObjekt.setCijena(proizvodsZarezom1.trim());
                //finalnaListaProizvoda.add(proizvodsZarezom1.trim());
            }
            else {
                if(!(proizvodsaSpaceom[proizvodSize -2].contains("O")) || proizvodsaSpaceom[proizvodSize -2].contains("o") || (proizvodsaSpaceom[proizvodSize -2].length() <= 4) && proizvodsaSpaceom[proizvodSize -2].contains(",") || (proizvodsaSpaceom[proizvodSize -2].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -2].contains(",")) ) {
                    proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2].substring(0,2);
                    proizvodsaSpaceom[proizvodSize -2] = proizvodsaSpaceom[proizvodSize -2] + "00";
                }
                proizvodObjekt.setCijena(proizvodsZarezom1.trim());
                //finalnaListaProizvoda.add(proizvodsaSpaceom[proizvodSize -2]);
            }

            proizvodsZarezom2 = "";
            if(!(proizvodsaSpaceom[proizvodSize -1].contains(","))) {
                proizvodsZarezom2 = "";
                int duljina = proizvodsaSpaceom[proizvodSize -1].length() - 2;
                proizvodsZarezom2 = proizvodsaSpaceom[proizvodSize -1].substring(0,duljina);
                proizvodsZarezom2 = proizvodsZarezom2 + "," + proizvodsaSpaceom[proizvodSize -1].substring(duljina,duljina + 1);
                if(!(proizvodsaSpaceom[proizvodSize -1].contains("O")) || proizvodsaSpaceom[proizvodSize -1].contains("o")|| (proizvodsaSpaceom[proizvodSize -1].length() <= 4) && proizvodsaSpaceom[proizvodSize -1].contains(",") || (proizvodsaSpaceom[proizvodSize -1].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -1].contains(","))) {
                    proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1].substring(0,2);
                    proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1] + "00";
                }
                proizvodObjekt.setIznos(proizvodsZarezom2.trim());
                //finalnaListaProizvoda.add(proizvodsZarezom2.trim());
            }
            else {
                if(!(proizvodsaSpaceom[proizvodSize -1].contains("O")) || proizvodsaSpaceom[proizvodSize -1].contains("o") || (proizvodsaSpaceom[proizvodSize -1].length() <= 4) && proizvodsaSpaceom[proizvodSize -1].contains(",") || (proizvodsaSpaceom[proizvodSize -1].length() <= 3) && !(proizvodsaSpaceom[proizvodSize -1].contains(",")) ) {
                    proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1].substring(0,2);
                    proizvodsaSpaceom[proizvodSize -1] = proizvodsaSpaceom[proizvodSize -1] + "00";
                }
                proizvodObjekt.setIznos(proizvodsZarezom2.trim());
                //finalnaListaProizvoda.add(proizvodsaSpaceom[proizvodSize -1]);
            }
            finalnaListaProizvoda.add(proizvodObjekt);
        }
        JSONpodaci.parsiranje(finalnaListaProizvoda);
        Log.w(this, "PROBA Result is: {}", finalnaListaProizvoda);
    }

    public void hendlanjePodataka(String food, String drinks, String bills, String fuel, String other, String remaining) {
        String valuta = " HRK";
        inputFood.setText(food + valuta);
        inputDrinks.setText(drinks + valuta);
        inputBills.setText(bills + valuta);
        input1Fuel.setText(fuel + valuta);
        inputOther.setText(other + valuta);
        inputRemaining.setText(remaining + valuta);
    }
}
