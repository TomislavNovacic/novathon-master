package com.example.tnovacic.novathon;

import android.content.Intent;
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
            user.setText(JS.salji("Hello"));
            //JS.salji("hello");
            //user.setText(JS.pakiranje(user.getText().toString(), password.getText().toString()));
            startActivity(Activity2);
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
}
