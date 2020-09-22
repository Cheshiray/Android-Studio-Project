package com.example.score_indicator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private TextView tv1;
    private TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        tv1 = findViewById(R.id.textView);
        btn5 = findViewById(R.id.button8);
        btn6 = findViewById(R.id.button7);
        btn7 = findViewById(R.id.button5);
        tv2 = findViewById(R.id.textView4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer a = Integer.valueOf(tv1.getText().toString())+3;
                tv1.setText(a.toString());
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer b = Integer.valueOf(tv1.getText().toString())+2;
                tv1.setText(b.toString());
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer c = Integer.valueOf(tv1.getText().toString())+1;
                tv1.setText(c.toString());
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer a = Integer.valueOf(tv2.getText().toString())+3;
                tv2.setText(a.toString());
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer b = Integer.valueOf(tv2.getText().toString())+2;
                tv2.setText(b.toString());
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer c = Integer.valueOf(tv2.getText().toString())+1;
                tv2.setText(c.toString());
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1.setText("0");
                tv2.setText("0");
            }
        });

    }
}
