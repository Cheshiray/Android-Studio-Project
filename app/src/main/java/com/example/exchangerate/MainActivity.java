package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final  String TAG="MainActivity";

    float dollarRate = 0.15f;
    float euroRate = 0.13f;
    float wonRate = 172.12f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 || resultCode==2) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);

            Log.i(TAG, "onActivityResult: dollarRate=" + dollarRate);
            Log.i(TAG, "onActivityResult: euroRate=" + euroRate);
            Log.i(TAG, "onActivityResult: wonRate=" + wonRate);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void dollar(View btn) {
        Log.i(TAG, "btn: onClicked");

        EditText et = findViewById(R.id.editText);
        TextView tv = findViewById(R.id.textView);

        if(et.length() == 0) {
            Toast.makeText(this, "please input RMB", Toast.LENGTH_SHORT).show();
            return;
        }else {
            float et1 = Float.parseFloat(et.getText().toString());

            if (btn.getId() == R.id.button_dollar) {
                float result = et1 * (dollarRate*100)/100;
                tv.setText(String.valueOf(result)+" dollar");
            }else if (btn.getId() == R.id.button_euro) {
                float result = et1 * (euroRate*100)/100;
                tv.setText(String.valueOf(result)+" euro");
            }else {
                float result = et1 * wonRate;
                tv.setText(String.valueOf(result)+" won");
            }
        }
    }

    public void openOne(View v) {
        Intent config = new Intent(this,Main2Activity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);
        startActivityForResult(config, 1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_1) {
            Intent config = new Intent(this,Main2Activity.class);
            config.putExtra("dollar_rate_key",dollarRate);
            config.putExtra("euro_rate_key",euroRate);
            config.putExtra("won_rate_key",wonRate);
            startActivity(config);
        }
        return super.onOptionsItemSelected(item);
    }
}