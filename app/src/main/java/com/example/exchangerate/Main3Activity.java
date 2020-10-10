package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.List;

public class Main3Activity extends AppCompatActivity implements Runnable {
    public static final  String TAG="MainActivity3";

    Handler handler;

    String[] data;
    String update;
    List<String> list3 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        SharedPreferences sp = getSharedPreferences("myList", Activity.MODE_PRIVATE);
        update = sp.getString("update_rate","0000.00.00:false");
        String getStr = sp.getString("list","");

        if (!getStr.equals("")) {
            Gson gson1 = new Gson();
            list3 = gson1.fromJson(getStr, new TypeToken<List<String>>() {
            }.getType());
        }

        data = list3.toArray(new String[list3.size()]);
        ListView listView1 = (ListView) findViewById(R.id.myList);
        ListAdapter adapter = new ArrayAdapter<String>(Main3Activity.this, android.R.layout.simple_list_item_1, data);
        listView1.setAdapter(adapter);

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String OSTime = df.format(new Date());

        if (!OSTime.equals(update.substring(0,10))) {
            update=OSTime+":false";
        }

        Log.i(TAG, "今日是否更新？"+update);

        if (update.substring(11).equals("false")) {
            Thread t = new Thread(this);
            t.start();
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    ListView listView2 = (ListView) findViewById(R.id.myList);
                    Bundle bdl = (Bundle) msg.obj;
                    data = bdl.getStringArray("Data");
                    ListAdapter adapter = new ArrayAdapter<String>(Main3Activity.this, android.R.layout.simple_list_item_1, data);
                    listView2.setAdapter(adapter);

                    SharedPreferences sp = getSharedPreferences("myList", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    List<String> list1 = Arrays.asList(data);
                    Gson gson2 = new Gson();
                    String str = gson2.toJson(list1);
                    editor.putString("update_rate",update.replace("false","true"));
                    editor.putString("list", str);
                    editor.commit();

                    Toast.makeText(Main3Activity.this, "今日汇率已更新", Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"今日已更新");
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        Bundle bundle = new Bundle();
        Document doc = null;

        try {
            doc = Jsoup.connect("https://www.usd-cny.com/bankofchina.htm").get();
            Elements tables = doc.getElementsByTag("table");
            Element table1 = tables.get(0);

            Elements tds = table1.getElementsByTag("td");
            String[] list2 = new String[27];
            int j = 0;
            float result;
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                result = 100f/Float.parseFloat(td2.text());
                String str = td1.text() + "==>" + result;
                list2[j] = str;
                j++;
            }
            bundle.putStringArray("Data", list2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = bundle;
        handler.sendMessage(msg);
    }
}