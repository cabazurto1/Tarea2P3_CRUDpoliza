package com.example.poliza.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //definir el nombre de la base de datos y versión
    private static final String DATABASE_NAME="poliza.db";
    private static final int DATABASE_VERSION=1;

    //definir el nombre de la tabla en columnas
    private static final String TABLE_NAME = "poliza";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_VALOR = "valor_auto";
    private static final String COLUMN_ACCIDENTES = "accidentes";
    private static final String COLUMN_MODELO = "modelo";
    private static final String COLUMN_EDAD = "edad";
    private static final String COLUMN_COSTO = "costo_poliza";

    //crear la tabla
    private static final String  TABLE_CREATE =
            " CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_VALOR + " REAL NOT NULL, " +
                    COLUMN_ACCIDENTES+ " INTEGER NOT NULL, " +
                    COLUMN_MODELO +  " TEXT NOT NULL, " +
                    COLUMN_EDAD + " TEXT NOT NULL, " +
                    COLUMN_COSTO + " REAL NOT NULL); ";


    //Constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //insertar datos en la base de datos
    public boolean insertarPoliza(String nombre, double valorAuto, int accidentes, String modelo, String edad, double costo_Poliza){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_VALOR, valorAuto);
        values.put(COLUMN_ACCIDENTES, accidentes);
        values.put(COLUMN_MODELO, modelo);
        values.put(COLUMN_EDAD, edad);
        values.put(COLUMN_COSTO, costo_Poliza);

        long resultado = sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();

        return resultado != -1;
    }

    // ✅ Read (Obtener todas las pólizas)
    public List<String> obtenerPolizas() {
        List<String> listaPolizas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String poliza = "ID: " + cursor.getInt(0) + ", Nombre: " + cursor.getString(1) +
                        ", Valor: " + cursor.getDouble(2) + ", Accidentes: " + cursor.getInt(3) +
                        ", Modelo: " + cursor.getString(4) + ", Edad: " + cursor.getString(5) +
                        ", Costo: " + cursor.getDouble(6);
                listaPolizas.add(poliza);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaPolizas;
    }

    // ✅ Update (Actualizar una póliza por ID)
    public boolean actualizarPoliza(int id, String nombre, double valorAuto, int accidentes, String modelo, String edad, double costo_Poliza) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_VALOR, valorAuto);
        values.put(COLUMN_ACCIDENTES, accidentes);
        values.put(COLUMN_MODELO, modelo);
        values.put(COLUMN_EDAD, edad);
        values.put(COLUMN_COSTO, costo_Poliza);

        int filasAfectadas = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();

        return filasAfectadas > 0;
    }

    // ✅ Delete (Eliminar una póliza por ID)
    public boolean eliminarPoliza(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasAfectadas = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();

        return filasAfectadas > 0;
    }

    // ✅ Buscar una póliza por ID
    public String buscarPolizaPorID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        String poliza = null;
        if (cursor.moveToFirst()) {
            poliza = "ID: " + cursor.getInt(0) + ", Nombre: " + cursor.getString(1) +
                    ", Valor: " + cursor.getDouble(2) + ", Accidentes: " + cursor.getInt(3) +
                    ", Modelo: " + cursor.getString(4) + ", Edad: " + cursor.getString(5) +
                    ", Costo: " + cursor.getDouble(6);
        }

        cursor.close();
        db.close();
        return poliza;
    }


}
