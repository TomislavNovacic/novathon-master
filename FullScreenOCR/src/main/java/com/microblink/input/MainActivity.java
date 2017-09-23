package com.microblink.input;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.microblink.recognizers.blinkinput.BlinkInputRecognizerSettings;
import com.microblink.recognizers.blinkocr.BlinkOCRRecognizerSettings;
import com.microblink.recognizers.blinkocr.engine.AbstractOCREngineOptions;
import com.microblink.recognizers.blinkocr.engine.BlinkOCREngineOptions;
import com.microblink.recognizers.blinkocr.parser.generic.RawParserSettings;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;

public class MainActivity extends AppCompatActivity {

    private static final String LICENSE_KEY = "GZLX6RM4-KUOPKVFO-F27ZHP23-GKFVGELE-GXCYIOHW-DNT6JOYT-RNJRDRDR-CTHZ4N3O";

    private static final int REQ_CODE_OCR = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        startActivityForResult(fullScreenIntent, REQ_CODE_OCR);
    }
}
