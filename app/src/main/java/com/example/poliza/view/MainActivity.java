package com.example.poliza.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent; // Asegúrate de tener esta importación

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.poliza.R;
import com.example.poliza.controller.PolizaController;
import com.example.poliza.database.DatabaseHelper;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //declarar objetos

    EditText txtNombre, txtValor, txtAccidentes;
    TextView lblCostoPoliza;
    Button btnCalcularCosto, btnLimpiar, btnSalir, btnListar;

    Spinner cboModelo, cboEdad;

    DatabaseHelper dbHelper;

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

        txtNombre = findViewById(R.id.et_nombre);
        txtValor = findViewById(R.id.et_valor_auto);
        txtAccidentes = findViewById(R.id.et_valor_accidentes);

        lblCostoPoliza = findViewById(R.id.tv_costo_poliza);

        btnCalcularCosto = findViewById(R.id.btn_calcular_costo);
        btnLimpiar = findViewById(R.id.btn_limpiar);
        btnSalir = findViewById(R.id.btn_salir);
        btnListar = findViewById(R.id.btn_listar);

        cboEdad = findViewById(R.id.sp_edad);
        cboModelo = findViewById(R.id.sp_modelo);

        dbHelper = new DatabaseHelper(this);

        cargarDatos();

        //eventos de los botones
        btnCalcularCosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cacularPoliza();
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarCampos();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListarDatosActivity.class);
                startActivity(intent);
            }
        });
    }

    //metodo para cargar datos
    private void cargarDatos() {
        //spinner cargar datos de los modelos
        List<String> modelos = Arrays.asList("Modelo A", "Modelo B", "Modelo C");
        ArrayAdapter<String> modeloAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelos);
        modeloAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        cboModelo.setAdapter(modeloAdapter);

        //spinner cargar Datos edad-
        List<String> edades = Arrays.asList("18-23", "24-55", "Mayor de 35");
        ArrayAdapter<String> edadesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, edades);
        edadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        cboEdad.setAdapter(edadesAdapter);
    }

    //metodo para calcular la poliza
    private void cacularPoliza() {

        if (txtNombre.getText().toString().isEmpty() || txtValor.getText().toString().isEmpty() || txtAccidentes.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            return;
        }

        //validacion de los spinners
        if (cboModelo.getSelectedItem() == null || cboEdad.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione un modelo o una edad", Toast.LENGTH_LONG).show();
            return;
        }

        //declarar variables

        String nombre = txtNombre.getText().toString();
        double valorAuto = Double.parseDouble(txtValor.getText().toString());
        int accidentes = Integer.parseInt(txtAccidentes.getText().toString());
        String modelo = cboModelo.getSelectedItem().toString();
        String edad = cboEdad.getSelectedItem().toString();

        //calculo de la poliza
        double costoTotal = Double.parseDouble(PolizaController.calcularPoliza(nombre, valorAuto, accidentes, modelo, edad));
        lblCostoPoliza.setText(String.valueOf(costoTotal));

        //guardar en la base de datos
        boolean insertado = dbHelper.insertarPoliza(nombre, valorAuto, accidentes, modelo, edad, costoTotal);
        if (insertado) {
            Toast.makeText(this, "Poliza insertada de forma correcta", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error al insertar", Toast.LENGTH_LONG).show();
        }

    }

    private void limpiarCampos(){
        txtNombre.setText("");
        txtValor.setText("");
        txtAccidentes.setText("");

        lblCostoPoliza.setText("");

        cboEdad.setSelection(0);
        cboModelo.setSelection(0);

        Toast.makeText(this, "Campos Limpios", Toast.LENGTH_LONG).show();


    }

}