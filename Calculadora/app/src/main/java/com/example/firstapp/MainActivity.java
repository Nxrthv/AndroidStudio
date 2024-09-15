package com.example.firstapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import static java.lang.Math.*;

public class MainActivity extends AppCompatActivity {

    double firstNum;
    String operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button num1 = findViewById(R.id.uno);
        Button num2 = findViewById(R.id.dos);
        Button num3 = findViewById(R.id.tres);
        Button num4 = findViewById(R.id.cuatro);
        Button num5 = findViewById(R.id.cinco);
        Button num6 = findViewById(R.id.seis);
        Button num7 = findViewById(R.id.siete);
        Button num8 = findViewById(R.id.ocho);
        Button num9 = findViewById(R.id.nueve);
        Button num0 = findViewById(R.id.cero);

        Button suma = findViewById(R.id.sumar);
        Button resta = findViewById(R.id.restar);
        Button multiplicacion = findViewById(R.id.multiplicar);
        Button division = findViewById(R.id.dividir);
        Button potencia = findViewById(R.id.potencia);
        Button porcentaje = findViewById(R.id.porcentaje);
        Button raiz = findViewById(R.id.raiz);
        Button point = findViewById(R.id.punto);
        Button clear = findViewById(R.id.clear);
        Button delete = findViewById(R.id.delete);
        Button equal = findViewById(R.id.igual);

        TextView screen = findViewById(R.id.textView);
        TextView resultado = findViewById(R.id.resultado);

        ArrayList<Button> nums = new ArrayList<>();
        nums.add(num0);
        nums.add(num1);
        nums.add(num2);
        nums.add(num3);
        nums.add(num4);
        nums.add(num5);
        nums.add(num6);
        nums.add(num7);
        nums.add(num8);
        nums.add(num9);

        for (Button b : nums) {
            b.setOnClickListener(view -> {
                String currentText = screen.getText().toString();
                String buttonText = ((Button) view).getText().toString();

                if (!currentText.equals("0")) {
                    screen.setText(new StringBuilder(currentText).append(buttonText));
                } else {
                    screen.setText(buttonText);
                }
            });
        }

        ArrayList<Button> opers = new ArrayList<>();
        opers.add(suma);
        opers.add(resta);
        opers.add(multiplicacion);
        opers.add(division);
        opers.add(potencia);
        opers.add(porcentaje);
        opers.add(raiz);

        final boolean[] operacionSeleccionada = {false,true};

        for (Button b : opers) {
            b.setOnClickListener(view -> {
                String operacionActual = b.getText().toString();

                if (operacionActual.equals("√")) {
                    firstNum = Double.parseDouble(screen.getText().toString());

                    if (firstNum >= 0) {
                        double resultadoRaiz = Math.sqrt(firstNum);
                        screen.setText(String.valueOf(resultadoRaiz));
                        resultado.setText("√" + firstNum + " = " + resultadoRaiz);

                        operacionSeleccionada[0] = false;
                    } else {
                        screen.setText("Error: Número negativo");
                        return;
                    }
                } else if (!operacionSeleccionada[0]) {
                    firstNum = Double.parseDouble(screen.getText().toString());
                    operation = operacionActual;
                    operacionSeleccionada[0] = true;

                    resultado.setText(firstNum + " " + operation);
                    screen.setText("");
                } else {
                    screen.setText("0");
                    return;
                }
            });
        }

        delete.setOnClickListener(view ->{
            String num = screen.getText().toString();
            if(num.length()>1){
                screen.setText(num.substring(0,num.length()-1));
            }if(num.length() == 1 && !num.equals("0")){
                screen.setText("0");
            }
        });

        point.setOnClickListener(view ->{
            if(!screen.getText().toString().contains(".")){
                screen.setText(screen.getText().toString() + ".");
            }
        });

        equal.setOnClickListener(view -> {
            try {
                double secondNum = Double.parseDouble(screen.getText().toString());
                Double result = 0.0;

                switch (operation) {
                    case "-":
                        result = firstNum - secondNum;
                        break;
                    case "x":
                        result = firstNum * secondNum;
                        break;
                    case "÷":
                        if (secondNum != 0) {
                            result = firstNum / secondNum;
                        } else {
                            screen.setText("Error");
                            return;
                        }
                        break;
                    case "^":
                        result = Math.pow(firstNum, secondNum);
                        break;
                    case "%":
                        result = firstNum * secondNum / 100;
                        break;
                    case "√":
                        if (firstNum >= 0) {
                            result = Math.sqrt(firstNum);
                            screen.setText(String.valueOf(result));
                            firstNum = result;
                        } else {
                            screen.setText("Error: negativo");
                        }
                        break;
                    case "+":
                    default:
                        result = firstNum + secondNum;
                }

                if (operacionSeleccionada[0]) {
                    screen.setText(String.valueOf(result));
                    firstNum = result;
                    resultado.setText(String.valueOf(firstNum));

                    operacionSeleccionada[0] = false;
                    operation = "";
                }
            } catch (NumberFormatException e) {
                screen.setText("Error: inválido");
            }
        });

        clear.setOnClickListener(view ->{
            firstNum = 0;
            screen.setText("0");
            resultado.setText("");
            operacionSeleccionada[0] = false;
        });
    }
}
