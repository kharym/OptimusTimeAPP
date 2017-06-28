package com.example.karim.optimustime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Evento del botón ir a vista tiempo
    public void irTiempo(View view){
        Intent i = new Intent(this, Tiempo.class);
        startActivity(i);
    }
}
