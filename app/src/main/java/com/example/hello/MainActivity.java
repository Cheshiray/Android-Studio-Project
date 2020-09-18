package com.example.hello;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private EditText edt;
    private TextView finalTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.textView5);
        tv.setText("Hello Android");

        btn = findViewById(R.id.button);
        edt = findViewById(R.id.editText);
        finalTv = findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double num= Double.valueOf(edt.getText().toString());
                num = num + 22;
                finalTv.setText(num.toString()+"F");
            }
        });
    }
}
