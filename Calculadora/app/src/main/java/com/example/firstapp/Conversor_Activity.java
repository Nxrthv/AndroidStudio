package com.example.firstapp;

import static java.lang.String.format;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class Conversor_Activity extends AppCompatActivity {

    private boolean areFABsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversor_de_moneda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FloatingActionButton fabMain = findViewById(R.id.fab_main);
        FloatingActionButton canculadora = findViewById(R.id.calculadora);
        FloatingActionButton imc = findViewById(R.id.imc);

        fabMain.setOnClickListener(view -> {
            if (areFABsVisible) {
                canculadora.setVisibility(View.GONE);
                imc.setVisibility(View.GONE);
                areFABsVisible = false;
            } else {
                canculadora.setVisibility(View.VISIBLE);
                imc.setVisibility(View.VISIBLE);
                areFABsVisible = true;
            }
        });

        canculadora.setOnClickListener(view -> {
            Intent i = new Intent(Conversor_Activity.this,MainActivity.class);
            startActivity(i);
        });

        imc.setOnClickListener(view -> {
            Intent i = new Intent(Conversor_Activity.this,IMC_Activity.class);
            startActivity(i);
        });

        Spinner spinner =  findViewById(R.id.moneda);

        List<String> monedas = new ArrayList<>();
        monedas.add("Tipo de Cambio");
        monedas.add("Dolar");
        monedas.add("Euro");
        monedas.add("Yen");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, monedas);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Button convertir = findViewById(R.id.convertir);
                TextView resultado = findViewById(R.id.result);
                EditText cantidad = findViewById(R.id.cantidad);

                    convertir.setOnClickListener(v ->{
                        String cantidadTexto = cantidad.getText().toString();
                        if (cantidadTexto.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Por favor ingrese una cantidad", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Double Sol = Double.parseDouble(cantidadTexto);
                        switch (selected){
                            case "Dolar":
                                Double rest = Sol / 3.75;
                                resultado.setText("$"+format("%.2f", rest)+" Dolares");
                                break;
                            case "Euro":
                                rest = Sol / 4.19;
                                resultado.setText("€"+format("%.2f", rest)+" Euros");
                                break;
                            case "Yen":
                                rest = Sol * 38.43;
                                resultado.setText("¥"+format("%.2f", rest)+" Yenes");
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "Seleccione un tipo de Moneda", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
                };

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}