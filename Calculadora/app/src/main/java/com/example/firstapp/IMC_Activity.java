package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class IMC_Activity extends AppCompatActivity {

    //Variable Identificar la activacion del FloatingButton
    private boolean areFABsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_imc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Lamando a los elementos del FloatingButton
        FloatingActionButton fabMain = findViewById(R.id.fab_main);
        FloatingActionButton calculadora = findViewById(R.id.calculadora);
        FloatingActionButton conversor = findViewById(R.id.conversor);

        //Definiendo los Elementos vistos al estar Activado o no
        fabMain.setOnClickListener(view -> {
            if (areFABsVisible) {
                calculadora.setVisibility(View.GONE);
                conversor.setVisibility(View.GONE);
                areFABsVisible = false;
            } else {
                conversor.setVisibility(View.VISIBLE);
                calculadora.setVisibility(View.VISIBLE);
                areFABsVisible = true;
            }
        });

        calculadora.setOnClickListener(view->{
            Intent i = new Intent(IMC_Activity.this,MainActivity.class);
            startActivity(i);
        });

        conversor.setOnClickListener(view ->{
           Intent i = new Intent(IMC_Activity.this,conversor_de_moneda.class);
           startActivity(i);
        });

        Button calcular =  findViewById(R.id.calcular);

        calcular.setOnClickListener(view ->{
            EditText peso = findViewById(R.id.peso);
            EditText altura = findViewById(R.id.altura);

            TextView resultado = findViewById(R.id.resultado);

            double kg = Double.parseDouble(peso.getText().toString());
            double cm = Double.parseDouble(altura.getText().toString());
            cm = cm / 100;
            double result = kg / (cm * cm);
            resultado.setText(String.format("%.2f", result));

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}