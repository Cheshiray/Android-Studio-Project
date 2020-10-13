package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.List;

public class Main3Activity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {
    public static final  String TAG="MainActivity3";

    Handler handler;

    String[] data1;
    String[] data2;
    String update;
    List<String> list3 = new ArrayList<>();
    List<String> list6 = new ArrayList<>();
    ArrayList<HashMap<String, String>> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        SharedPreferences sp = getSharedPreferences("myList", Activity.MODE_PRIVATE);
        update = sp.getString("update_rate","0000.00.00:false");
        String getStr = sp.getString("list","");
        String getStr2 = sp.getString("list2","");

        if (!getStr.equals("")) {
            Gson gson1 = new Gson();
            Gson gson3 = new Gson();
            list3 = gson1.fromJson(getStr, new TypeToken<List<String>>() {
            }.getType());
            list6 = gson3.fromJson(getStr2, new TypeToken<List<String>>() {
            }.getType());
        }

        data1 = list3.toArray(new String[list3.size()]);
        data2 = list6.toArray(new String[list6.size()]);

        ListView listView = (ListView) findViewById(R.id.myList);
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 27; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", data1[i]);
            map.put("ItemDetail", data2[i]);
            listItems.add(map);
        }

        MyAdapter myAdapter = new MyAdapter(this, R.layout.mylist, listItems);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(Main3Activity.this);

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
                    Bundle bdl = (Bundle) msg.obj;
                    data1 = bdl.getStringArray("Data1");
                    data2 = bdl.getStringArray("Data2");

                    ListView listView2 = (ListView) findViewById(R.id.myList);
                    listItems = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < 27; i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("ItemTitle", data1[i]);
                        map.put("ItemDetail", data2[i]);
                        listItems.add(map);
                    }

                    MyAdapter myAdapter2 = new MyAdapter(Main3Activity.this, R.layout.mylist, listItems);
                    listView2.setAdapter(myAdapter2);
                    listView2.setOnItemClickListener(Main3Activity.this);

                    SharedPreferences sp = getSharedPreferences("myList", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    List<String> list1 = Arrays.asList(data1);
                    List<String> list5 = Arrays.asList(data2);
                    Gson gson2 = new Gson();
                    Gson gson4 = new Gson();
                    String str = gson2.toJson(list1);
                    String str3 = gson4.toJson(list5);
                    editor.putString("update_rate",update.replace("false","true"));
                    editor.putString("list", str);
                    editor.putString("list2", str3);
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
            String[] list4 = new String[27];
            int j = 0;
            float result;
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                result = 100f/Float.parseFloat(td2.text());
                String str1 = td1.text();
                String str2 = String.valueOf(result);
                list2[j] = str1;
                list4[j] = str2;
                j++;
            }
            bundle.putStringArray("Data1", list2);
            bundle.putStringArray("Data2", list4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView title = (TextView) view.findViewById(R.id.textView5);
        TextView detail = (TextView) view.findViewById(R.id.textView6);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());

        Intent calculation = new Intent(view.getContext(), Main4Activity.class);
        calculation.putExtra("title", title2);
        calculation.putExtra("detail", Float.parseFloat(detail2));
        view.getContext().startActivity(calculation);
    }
}