package com.example.fernandosouza.feedback_system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private Button botao;
    private RatingBar ratingBar1;
    private RatingBar ratingBar2;
    private RatingBar ratingBar3;
    private RatingBar ratingBar4;
    private RatingBar ratingBar5;
    private RatingBar ratingBar6;
    private RatingBar ratingBar7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        
        initRatingBars();

        botao = (Button) findViewById(R.id.button);
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stringDate = getStringDateToday();

                /*Enviar para o firebase*/
                popularDadosNessaData(stringDate, "1", ratingBar1);
                popularDadosNessaData(stringDate, "2", ratingBar2);
                popularDadosNessaData(stringDate, "3", ratingBar3);
                popularDadosNessaData(stringDate, "4", ratingBar4);
                popularDadosNessaData(stringDate, "5", ratingBar5);
                popularDadosNessaData(stringDate, "6", ratingBar6);
                popularDadosNessaData(stringDate, "7", ratingBar7);


                /*Toast*/
                Context contexto = getApplicationContext();
                String texto = "Obrigado por nos enviar sua opinião!";
                int duracao = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(contexto, texto,duracao);
                toast.show();


                /*refreshRatingBars();*/

            }
        });
    }

    public String getStringDateToday(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        month = month + 1;
        String monthString = Integer.toString(month);
        String dayString = Integer.toString(day);

        if(Integer.toString(month).length() == 1){
            monthString = "0" + Integer.toString(month);
        }
        if(Integer.toString(day).length() == 1){
            dayString = "0" + Integer.toString(day);
        }

        return dayString+monthString+Integer.toString(year);
    }

    public void popularDadosNessaData(final String date,String numeroPergunta, final RatingBar ratingBarr){
        if(ratingBarr.getRating() == 0.0f){
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference myRefValue = database.getReference("forte_ativo/pergunta_"+numeroPergunta+"/" + date);
        final DatabaseReference myRef = database.getReference("forte_ativo/pergunta_"+numeroPergunta+"/" + date +"/"+Integer.toString((int)ratingBarr.getRating()).replace("0", ""));
        myRefValue.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                if(ratingBarr.getRating() == 0){
                    return;
                }
                /*Caso não exista esse dia no bd*/
                if(dataSnapshot1.getValue() == null){
                    Map<String, Integer> firstData = new HashMap<>();
                    firstData.put("1", 0);
                    firstData.put("2", 0);
                    firstData.put("3", 0);
                    firstData.put("4", 0);
                    firstData.put("5", 0);
                    firstData.put(Integer.toString((int)ratingBarr.getRating()), 1);
                    myRefValue.setValue(firstData);
                }else{
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                myRef.setValue((Long)dataSnapshot.getValue() + 1);
                                ratingBarr.setRating(0f);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            System.out.println(error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

    public static void setTimeoutSync(Runnable runnable, int delay) {
        try {
            Thread.sleep(delay);
            runnable.run();
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    public void initRatingBars(){
        ratingBar1 = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        ratingBar4 = (RatingBar) findViewById(R.id.ratingBar4);
        ratingBar5 = (RatingBar) findViewById(R.id.ratingBar5);
        ratingBar6 = (RatingBar) findViewById(R.id.ratingBar6);
        ratingBar7 = (RatingBar) findViewById(R.id.ratingBar7);

        ratingBar1.setStepSize(1);
        ratingBar1.setRating(0f);

        ratingBar2.setStepSize(1);
        ratingBar2.setRating(0f);

        ratingBar3.setStepSize(1);
        ratingBar3.setRating(0f);

        ratingBar4.setStepSize(1);
        ratingBar4.setRating(0f);

        ratingBar5.setStepSize(1);
        ratingBar5.setRating(0f);

        ratingBar6.setStepSize(1);
        ratingBar6.setRating(0f);

        ratingBar7.setStepSize(1);
        ratingBar7.setRating(0f);
    }



    public void refreshRatingBars(){
        ratingBar1.setRating(0f);
        ratingBar2.setRating(0f);
        ratingBar3.setRating(0f);
        ratingBar4.setRating(0f);
        ratingBar5.setRating(0f);
        ratingBar6.setRating(0f);
        ratingBar7.setRating(0f);
    }
}
