package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Runnable {
    public static final  String TAG="MainActivity";

    Handler handler;

    float dollarRate = 0.15f;
    float euroRate = 0.13f;
    float wonRate = 172.12f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("myRate", Activity.MODE_PRIVATE);
        dollarRate = sp.getFloat("dollar_rate", 0.0f);
        euroRate = sp.getFloat("euro_rate", 0.0f);
        wonRate = sp.getFloat("won_rate", 0.0f);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-Rate");
                    euroRate = bdl.getFloat("euro-Rate");
                    wonRate = bdl.getFloat("won-Rate");

                    Log.i(TAG, "dollarRate:" + dollarRate);
                    Log.i(TAG, "euroRate:" + euroRate);
                    Log.i(TAG, "wonRate:" + wonRate);
                }
                super.handleMessage(msg);
            }
        };
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
            Intent ExRateList = new Intent(this,Main3Activity.class);
            startActivity(ExRateList);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        Log.i(TAG, "run:run()...");

        Bundle bundle = new Bundle();

        /*URL url = null;
        try {
            url = new URL("https://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputStream2String(in);
            Log.i(TAG, "run:html=" + html);
            Document doc = Jsoup.parse(html);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: "+doc.title());
            Elements tables = doc.getElementsByTag("table");
            Element table1 = tables.get(0);

            //获取TD中的数据
            Elements tds = table1.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                String str1 = td1.text();
                String val = td2.text();
                Log.i(TAG, "run: " + str1 + "==>" + val);

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-Rate", 100f/Float.parseFloat(val));
                }else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-Rate", 100f/Float.parseFloat(val));
                }else if ("韩元".equals(str1)) {
                    bundle.putFloat("won-Rate", 100f/Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        //msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}