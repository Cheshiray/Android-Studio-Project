package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG="MainActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        float won2 = intent.getFloatExtra("won_rate_key",0.0f);

        EditText et1 = findViewById(R.id.editText2);
        EditText et2 = findViewById(R.id.editText3);
        EditText et3 = findViewById(R.id.editText4);
        et1.setText(String.valueOf(dollar2));
        et2.setText(String.valueOf(euro2));
        et3.setText(String.valueOf(won2));

        Log.i(TAG, "onCreate: dollar2=" + dollar2);
        Log.i(TAG, "onCreate: euro2=" + euro2);
        Log.i(TAG, "onCreate: won2=" + won2);
    }

    public void save(View v) {
        Log.i(TAG, "v: onClicked");

        Intent intent = getIntent();
        EditText et2 = findViewById(R.id.editText2);
        EditText et3 = findViewById(R.id.editText3);
        EditText et4 = findViewById(R.id.editText4);
        float newDollar = Float.parseFloat(et2.getText().toString());
        float newEuro = Float.parseFloat(et3.getText().toString());
        float newWon = Float.parseFloat(et4.getText().toString());

        Bundle bdl = new Bundle();
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);

        finish();
    }
}