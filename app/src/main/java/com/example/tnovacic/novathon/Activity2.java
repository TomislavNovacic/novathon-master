package com.example.tnovacic.novathon;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.microblink.recognizers.blinkinput.BlinkInputRecognizerSettings;
import com.microblink.recognizers.blinkocr.engine.BlinkOCREngineOptions;
import com.microblink.recognizers.blinkocr.parser.generic.RawParserSettings;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;

public class Activity2 extends AppCompatActivity {

    private static final String LICENSE_KEY = "QIFSS7YI-G2WAMKWX-LPZUXECB-2BLCYTRR-JDLBGALA-ZNZMCVRM-JYYUQ5VL-EQQDCSWM";

    private static final int REQ_CODE_OCR = 234;

    LinearLayout[] bars = new LinearLayout[5];
    FloatingActionButton float_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        bars[0] = (LinearLayout) findViewById(R.id.food_bar);
        bars[1] = (LinearLayout) findViewById(R.id.drinks_bar);
        bars[2] = (LinearLayout) findViewById(R.id.bills_bar);
        bars[3] = (LinearLayout) findViewById(R.id.fuel_bar);
        bars[4] = (LinearLayout) findViewById(R.id.other_bar);


        bars[0].setBackgroundColor(Color.parseColor("#3F51B5"));
        bars[1].setBackgroundColor(Color.parseColor("#64B5F6"));
        bars[2].setBackgroundColor(Color.parseColor("#F4511E"));
        bars[3].setBackgroundColor(Color.parseColor("#CDDC39"));
        bars[4].setBackgroundColor(Color.parseColor("#8D6E63"));

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
}
