package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main4Activity extends AppCompatActivity {

    Float detail = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        String title = getIntent().getStringExtra("title");
        detail = getIntent().getFloatExtra("detail", 0f);
        ((TextView)findViewById(R.id.textView7)).setText(title);

        EditText cal = findViewById(R.id.editText5);
        cal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView show = findViewById(R.id.textView8);
                Pattern p = Pattern.compile("^[+-]?(0|([1-9]\\d*))(\\.\\d+)?$");
                Matcher m = p.matcher(s);
                if (s.length()>0) {
                    if (m.matches()) {
                        float val = Float.parseFloat(s.toString());
                        show.setText(val + "RMB=>" + detail*val);
                    } else {
                        show.setText("");
                        Toast.makeText(Main4Activity.this, "请输入数字", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    show.setText("");
                }
            }
        });
    }
}