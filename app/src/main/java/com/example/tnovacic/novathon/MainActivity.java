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

public class MainActivity extends AppCompatActivity {

    EditText user, password;
    Intent Activity2;



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


}
