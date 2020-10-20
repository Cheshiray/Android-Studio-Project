package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main3Activity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static final  String TAG="MainActivity3";

    Handler handler;
    RateManager manager;

    String[] data1;
    String[] data2;
    ArrayList<HashMap<String, String>> listItems;

    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY, "");
        Log.i("List","lastRateDateStr=" + logDate);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    Bundle bdl = (Bundle) msg.obj;
                    data1 = bdl.getStringArray("Data1");
                    data2 = bdl.getStringArray("Data2");

                    GridView listView = (GridView) findViewById(R.id.myList);
                    listItems = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < 27; i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("ItemTitle", data1[i]);
                        map.put("ItemDetail", data2[i]);
                        listItems.add(map);
                    }

                    MyAdapter myAdapter = new MyAdapter(Main3Activity.this, R.layout.mylist, listItems);
                    listView.setAdapter(myAdapter);
                    listView.setOnItemClickListener(Main3Activity.this);
                    listView.setOnItemLongClickListener(Main3Activity.this);
                    listView.setEmptyView(findViewById(R.id.nodata));
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        Bundle bundle = new Bundle();
        manager = new RateManager(this);

        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i("run","curDateStr:" + curDateStr + " logDate:" + logDate);

        if (curDateStr.equals(logDate)) {
            Log.i("run", "日期相等，从数据库中获取数据");

            String[] name_1 = new String[27];
            String[] value_1 = new String[27];

            for (int i = 1; i < 28; i++) {
                name_1[i-1] = manager.findById(i).getCurName();
                value_1[i-1] = manager.findById(i).getCurRate();
            }

            bundle.putStringArray("Data1", name_1);
            bundle.putStringArray("Data2", value_1);
        } else {
            Log.i("run","日期不相等，从网络中获取在线数据");
            manager.deleteAll();
            Log.i("db","删除所有记录");

            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.usd-cny.com/bankofchina.htm").get();
                Elements tables = doc.getElementsByTag("table");
                Element table1 = tables.get(0);

                Elements tds = table1.getElementsByTag("td");
                String[] name_2 = new String[27];
                String[] value_2 = new String[27];
                int j = 0;
                float result;
                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    result = 100f / Float.parseFloat(td2.text());
                    String str1 = td1.text();
                    String str2 = String.valueOf(result);
                    name_2[j] = str1;
                    value_2[j] = str2;
                    j++;
                    manager.add(new RateItem(str1,str2));
                }
                bundle.putStringArray("Data1", name_2);
                bundle.putStringArray("Data2", value_2);

//                List<RateItem> testList = manager.listAll();
//                for(RateItem i : testList) {
//                    Log.i(TAG, "取出数据：[id="+i.getId()+"] Name="+i.getCurName()+" Rate="+i.getCurRate());
//                }
                Log.i("db","添加新记录集");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //更新记录日期
            SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(DATE_SP_KEY, curDateStr);
            edit.commit();
            Log.i("run","更新日期结束：" + curDateStr);
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

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("请确认是否删除当前数据")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                        adapter.remove(parent.getItemAtPosition(position));
                    }
                }).setNegativeButton("否", null);
        builder.create().show();
        return true;
    }
}