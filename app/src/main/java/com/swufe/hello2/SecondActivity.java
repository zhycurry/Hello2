package com.swufe.hello2;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.swufe.hello2.R;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "show";
    TextView score;
    TextView scoreb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        score = findViewById(R.id.score);
        scoreb = findViewById(R.id.scoreb);


    }
    public void btnAdd1(View btn){
        if(btn.getId()==R.id.btn_1) {
            showScore(1);
        }else{
            showScoreb(1);
        }
    }

    public void btnAdd2(View btn){
        if(btn.getId()==R.id.btn_2) {
            showScore(2);
        }else{
            showScoreb(2);
        }
    }

    public void btnAdd3(View btn){
        if(btn.getId()==R.id.btn_3) {
            showScore(3);
        }else{
            showScoreb(3);
        }
    }

    public void btnReset(View btn){
        score.setText("0");
        scoreb.setText("0");
    }

    private void showScore(int inc){
        Log.i(TAG, "showScore: "+inc);
        String oldScore = (String) score.getText();
        int newScore = Integer.parseInt(oldScore) + inc;
        score.setText(""+newScore);
    }

    private void showScoreb(int inc){
        Log.i(TAG, "showScore: "+inc);
        String oldScoreb = (String) scoreb.getText();
        int newScoreb = Integer.parseInt(oldScoreb) + inc;
        scoreb.setText(""+newScoreb);
    }
}
