package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IMC_Activity extends AppCompatActivity {

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

        Button Calculadora = findViewById(R.id.calculadora);
        Button calcular =  findViewById(R.id.calcular);

        Calculadora.setOnClickListener(view->{
            Intent i = new Intent(IMC_Activity.this,MainActivity.class);
            startActivity(i);
        });

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
}