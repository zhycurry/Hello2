package com.swufe.hello2;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RateListActivity extends ListActivity implements Runnable {
    String date[] = {"one", "two", "three"};
    Handler handler;
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);


        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY,"");
        Log.i("list","lastRtaeDateStr="+logDate);

        List<String> list1 = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            list1.add("item" + 1);
        }

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list1);
        setListAdapter(adapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    List<String> list2 = (List<String>)msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this, android.R.layout.simple_list_item_1, list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }

        };


    }

    @Override
    public void run() {
        //获取网络数据，放入list
        List<String> retList = new ArrayList<String>();
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i("run","curDateStr:"+curDateStr + "logDate" + logDate);

        if(curDateStr.equals(logDate)){
            Log.i("run:","日期相等，从数据库中获取数据");
            RateManager manager = new RateManager(this);
            for(RateItem item : manager.listAll()){
                retList.add(item.getCurName() + "-->" + item.getCurRate());
            }
        }
        else{
            Log.i("run:","日期不相等，从网络中获取数据");
            Document doc = null;
            try {
                Thread.sleep(300);
                doc = Jsoup.connect("https://www.bankofchina.com/sourcedb/whpj/?keywords=%E5%A4%96%E6%B1%87%E7%89%8C%E4%BB%B7").get();
                Log.i(TAG,"run:"+doc.title());
                Elements tables = doc.getElementsByTag("table");


                Element table1 = tables.get(1);

                Elements tds = table1.getElementsByTag("td");
                List<RateItem> rateList = new ArrayList<RateItem>();
                for(int i=0;i<tds.size();i+=8){
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i+5);
                    Log.i(TAG,"run:"+td1.text() + "==>" + td2.text());
                    String str1 = td1.text();
                    String val = td2.text();

                    retList.add(str1 + "==>" + val);
                    rateList.add(new RateItem(str1,val));


                }
                RateManager manager = new RateManager(this);
                manager.deleteAll();
                manager.addAll(rateList);

                SharedPreferences sp = getSharedPreferences("myrate",Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(DATE_SP_KEY,curDateStr);
                edit.commit();
                Log.i("run:","更新日期结束" + curDateStr);



            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



        Message msg=handler.obtainMessage(5);
        msg.obj = retList;
        handler.sendMessage(msg);
    }

}