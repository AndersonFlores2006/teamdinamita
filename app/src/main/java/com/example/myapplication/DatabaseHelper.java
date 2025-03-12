package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "residuosApp.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Usuarios
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT)";

    // Tabla Residuos
    private static final String TABLE_RESIDUOS = "residuos";
    private static final String COLUMN_RESIDUO_ID = "id";
    private static final String COLUMN_TIPO = "tipo";
    private static final String COLUMN_CANTIDAD = "cantidad";

    private static final String CREATE_TABLE_RESIDUOS =
            "CREATE TABLE " + TABLE_RESIDUOS + " (" +
                    COLUMN_RESIDUO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIPO + " TEXT, " +
                    COLUMN_CANTIDAD + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);  // Crear tabla Usuarios
        db.execSQL(CREATE_TABLE_RESIDUOS); // Crear tabla Residuos
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESIDUOS);
        onCreate(db);
    }

    // Método para registrar usuarios
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Método para validar usuario
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE username = ? AND password = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Método para insertar residuos
    public boolean insertResiduos(String tipo, int cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIPO, tipo);
        values.put(COLUMN_CANTIDAD, cantidad);
        long result = db.insert(TABLE_RESIDUOS, null, values);
        return result != -1;
    }

    // Método para obtener todos los residuos registrados
    public ArrayList<String> getAllResiduos() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> listaResiduos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESIDUOS, null);
        if (cursor.moveToFirst()) {
            do {
                // Formato: "ID: 1 | Tipo: Vidrio | Cantidad: 5"
                String residuo = "ID: " + cursor.getInt(0) +
                        " | Tipo: " + cursor.getString(1) +
                        " | Cantidad: " + cursor.getInt(2);
                listaResiduos.add(residuo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaResiduos;
    }

    // Método para eliminar un residuo por ID
    public boolean deleteResiduo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_RESIDUOS, COLUMN_RESIDUO_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Método para actualizar un residuo
    public boolean updateResiduo(int id, String tipo, int cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIPO, tipo);
        values.put(COLUMN_CANTIDAD, cantidad);
        int result = db.update(TABLE_RESIDUOS, values, COLUMN_RESIDUO_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Método para obtener residuos para PDF (si lo necesitas)
    public ArrayList<String[]> getResiduosForPDF() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String[]> listaResiduos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESIDUOS, null);
        if (cursor.moveToFirst()) {
            do {
                String[] residuo = {
                        String.valueOf(cursor.getInt(0)), // ID
                        cursor.getString(1),               // Tipo
                        String.valueOf(cursor.getInt(2))   // Cantidad
                };
                listaResiduos.add(residuo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaResiduos;
    }
}
