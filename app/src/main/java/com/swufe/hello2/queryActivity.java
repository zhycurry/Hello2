package com.swufe.hello2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class queryActivity extends AppCompatActivity implements Runnable {
    private final String TAG = "key words";
    private String updateDate = "";


    EditText input;
    ListView result;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        input = (EditText)findViewById(R.id.input);
        final ListView result=(ListView)this.findViewById(R.id.result);

        List<String> List2 = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            List2.add("item" + 1);
        }

        ListAdapter adapter = new ArrayAdapter<String>(queryActivity.this, android.R.layout.simple_list_item_1, List2);
        result.setAdapter(adapter);




        SharedPreferences sharedPreferences = getSharedPreferences("key words", Activity.MODE_PRIVATE);
        updateDate = sharedPreferences.getString("update_date","");


        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG,"onCreate: sp updateDate" + updateDate);


        if(!todayStr.equals(updateDate)){
            Log.i(TAG,"onCreate: 需要更新");
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG,"onCreate: 不需要更新");
        }


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==5){
                    List<String> list2= (List<String>)msg.obj;

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("key words", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    editor.commit();
                    editor.apply();

                    Toast.makeText(queryActivity.this,"通知已经更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);

            }
        };



    }

    public void run() {
        List<String> retList = new ArrayList<String>();
        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG,"run:"+doc.title());
            Elements titles = doc.getElementsByTag("title");
            Element title1 = titles.get(6);
            Elements showtitles = title1.getElementsByTag("article-showTitle");
            for(int i=0;i<showtitles.size();i+=1){
                Element td1 = showtitles.get(i);

                Log.i(TAG,"run:"+td1.text());
                String str1 = td1.text();
                retList.add(str1);

                Log.i(TAG,"run:"+str1);


            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(5);
        msg.obj = retList;
        handler.sendMessage(msg);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();

        try (Reader in = new InputStreamReader(inputStream, "UTF-8")) {
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);


            }
            return out.toString();
        }
    }



}
