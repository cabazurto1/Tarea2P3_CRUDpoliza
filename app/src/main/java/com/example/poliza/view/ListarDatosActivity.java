package com.example.poliza.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poliza.R;
import com.example.poliza.controller.PolizaController;
import com.example.poliza.database.DatabaseHelper;

import java.util.List;

public class ListarDatosActivity extends AppCompatActivity {

    private ListView listViewPolizas;
    private Button btnCerrar;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;  // Mantenemos en variable de clase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_datos);

        listViewPolizas = findViewById(R.id.listViewPolizas);
        btnCerrar = findViewById(R.id.btnCerrar);
        dbHelper = new DatabaseHelper(this);

        cargarDatos();

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Cierra la actividad y vuelve a la anterior
            }
        });

        // Escuchar clic en cada item de la lista (click corto)
        listViewPolizas.setOnItemClickListener((parent, view, position, id) -> {
            // Podrías mostrar un diálogo con opciones "Editar" o "Eliminar"
            mostrarDialogOpciones(position);
        });

        // Opcional: Escuchar "long click" si prefieres un menú al mantener pulsado
        // listViewPolizas.setOnItemLongClickListener((parent, view, position, id) -> {
        //     mostrarDialogOpciones(position);
        //     return true;
        // });
    }

    // Método para cargar y mostrar las pólizas en el ListView
    private void cargarDatos() {
        List<String> listaPolizas = dbHelper.obtenerPolizas();

        if (listaPolizas.isEmpty()) {
            Toast.makeText(this, "No hay pólizas registradas", Toast.LENGTH_LONG).show();
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listaPolizas);
        listViewPolizas.setAdapter(adapter);
    }

    /**
     * Muestra un AlertDialog con las opciones de Editar o Eliminar la póliza seleccionada
     */
    private void mostrarDialogOpciones(int position) {
        // Obtenemos el string del item (ej: "ID: 3, Nombre: Juan, ...")
        String polizaString = adapter.getItem(position);
        if (polizaString == null) return;

        // Extraer ID de la póliza
        int polizaId = extraerIdDesdeCadena(polizaString);
        if (polizaId == -1) {
            Toast.makeText(this, "No se pudo extraer el ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un diálogo de opciones
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione una acción")
                .setItems(new CharSequence[]{"Editar", "Eliminar"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Editar
                                mostrarDialogEditar(polizaId);
                                break;
                            case 1: // Eliminar
                                eliminarPoliza(polizaId);
                                break;
                        }
                    }
                })
                .show();
    }

    /**
     * Elimina la póliza con el ID dado y recarga la lista
     */
    private void eliminarPoliza(int id) {
        boolean exito = dbHelper.eliminarPoliza(id);
        if (exito) {
            Toast.makeText(this, "Póliza eliminada correctamente", Toast.LENGTH_SHORT).show();
            recargarLista();
        } else {
            Toast.makeText(this, "No se pudo eliminar la póliza", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Muestra un cuadro de diálogo para editar la póliza (formulario básico).
     * Aquí mismo puedes obtener los datos y llamar a dbHelper.actualizarPoliza(...)
     */
    private void mostrarDialogEditar(int idPoliza) {
        // Recuperamos la póliza actual desde la BD
        String polizaStr = dbHelper.buscarPolizaPorID(idPoliza);
        if (polizaStr == null) {
            Toast.makeText(this, "No se encontraron datos de la póliza", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extraer los valores (Nombre, Valor, Accidentes, Modelo, Edad, Costo)
        String[] partes = polizaStr.split(",");
        if (partes.length < 7) {
            Toast.makeText(this, "Error en el formato de la póliza", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreActual = partes[1].split(":")[1].trim();
        double valorActual = Double.parseDouble(partes[2].split(":")[1].trim());
        int accidentesActual = Integer.parseInt(partes[3].split(":")[1].trim());
        String modeloActual = partes[4].split(":")[1].trim();
        String edadActual = partes[5].split(":")[1].trim();

        // Crear un layout dinámico para el AlertDialog
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        // Campos de entrada prellenados
        final EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre");
        etNombre.setText(nombreActual);
        layout.addView(etNombre);

        final EditText etValor = new EditText(this);
        etValor.setHint("Valor del Auto");
        etValor.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etValor.setText(String.valueOf(valorActual));
        layout.addView(etValor);

        final EditText etAccidentes = new EditText(this);
        etAccidentes.setHint("Número de Accidentes");
        etAccidentes.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etAccidentes.setText(String.valueOf(accidentesActual));
        layout.addView(etAccidentes);

        // Spinner para Modelo (seleccionado automáticamente)
        final Spinner spModelo = new Spinner(this);
        ArrayAdapter<String> adapterModelo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Modelo A", "Modelo B", "Modelo C"});
        adapterModelo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModelo.setAdapter(adapterModelo);
        spModelo.setSelection(adapterModelo.getPosition(modeloActual)); // Poner modelo actual
        layout.addView(spModelo);

        // Spinner para Edad (seleccionado automáticamente)
        final Spinner spEdad = new Spinner(this);
        ArrayAdapter<String> adapterEdad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"18-23", "24-55", "Mayor de 35"});
        adapterEdad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEdad.setAdapter(adapterEdad);
        spEdad.setSelection(adapterEdad.getPosition(edadActual)); // Poner edad actual
        layout.addView(spEdad);

        // Crear el AlertDialog con los datos precargados
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Póliza")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        // Obtener los valores ingresados por el usuario
                        String nuevoNombre = etNombre.getText().toString().trim();
                        double nuevoValor = Double.parseDouble(etValor.getText().toString().trim());
                        int nuevosAccidentes = Integer.parseInt(etAccidentes.getText().toString().trim());
                        String nuevoModelo = spModelo.getSelectedItem().toString();
                        String nuevaEdad = spEdad.getSelectedItem().toString();

                        // Calcular nuevamente el costo total
                        double nuevoCosto = Double.parseDouble(PolizaController.calcularPoliza(nuevoNombre, nuevoValor, nuevosAccidentes, nuevoModelo, nuevaEdad));

                        // Llamar al método de actualización
                        boolean exito = dbHelper.actualizarPoliza(idPoliza, nuevoNombre, nuevoValor, nuevosAccidentes, nuevoModelo, nuevaEdad, nuevoCosto);

                        if (exito) {
                            Toast.makeText(this, "Póliza actualizada correctamente", Toast.LENGTH_SHORT).show();
                            recargarLista();
                        } else {
                            Toast.makeText(this, "Error al actualizar la póliza", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Datos inválidos, por favor verifique", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Fuerza a recargar la lista desde la BD
     */
    private void recargarLista() {
        List<String> nuevaLista = dbHelper.obtenerPolizas();
        adapter.clear();
        adapter.addAll(nuevaLista);
        adapter.notifyDataSetChanged();
    }

    /**
     * Extrae el ID de la cadena que contiene la póliza, asumiendo formato "ID: X, Nombre: ..."
     */
    private int extraerIdDesdeCadena(String polizaString) {
        // polizaString = "ID: 3, Nombre: Juan, Valor: 4000.0, ..."

        // Buscamos "ID: " y luego leemos hasta la coma o el final
        // Por simplicidad, podemos hacer un split:
        // [0] = "ID: 3"
        // [1] = " Nombre: Juan"
        // ...
        try {
            String[] partes = polizaString.split(",");
            // partes[0] debería ser "ID: 3"
            String parteId = partes[0]; // "ID: 3"
            // Quitamos "ID: " usando replace o substring
            String idStr = parteId.replace("ID:", "").trim(); // "3"
            return Integer.parseInt(idStr);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
