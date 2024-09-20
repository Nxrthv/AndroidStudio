package com.example.firstapp;

import static java.lang.String.format;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class conversor_de_moneda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversor_de_moneda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner =  findViewById(R.id.moneda);

        List<String> monedas = new ArrayList<>();
        monedas.add("Selecciona");
        monedas.add("Dolar");
        monedas.add("Euro");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Button convertir = findViewById(R.id.convertir);
                TextView resultado = findViewById(R.id.result);
                EditText cantidad = findViewById(R.id.cantidad);

                    String cantidadTexto = cantidad.getText().toString();
                    if (cantidadTexto.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Por favor ingrese una cantidad", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    convertir.setOnClickListener(v ->{
                        Double Sol = Double.parseDouble(cantidadTexto);
                        if (selected.equals("Dolar")) {
                            Toast.makeText(getApplicationContext(), "Seleccionaste: " + selected, Toast.LENGTH_SHORT).show();
                            Double rest = Sol / 3.75;
                            resultado.setText(format("%.2f", rest));
                        } else if (selected.equals("Euro")) {
                            Toast.makeText(getApplicationContext(), "Seleccionaste: " + selected, Toast.LENGTH_SHORT).show();
                            Double rest = Sol / 4.19;
                            resultado.setText(format("%.2f", rest));
                        }else{
                            resultado.setText("Error");
                        }
                    });
                };

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }
}