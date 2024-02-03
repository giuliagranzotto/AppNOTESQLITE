package com.example.notesqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    // Definizione delle costanti per il nome e la versione del DB
    private static final String DATABASE_NAME = "note.db";
    private static final int DATABASE_VERSION = 1;
    // Definizione delle costanti per il nome della Tabella delle colonne del DB
    public static final String TABLE_NAME = "note_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITOLO = "titolo";
    public static final String COLUMN_CONTENUTO = "contenuto";
    public static final String COLUMN_TIPO = "tipo";

    // Costruttore della classe DbHelper

    public MyDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    // Query per creare la tabella nel DataBase
    @Override
    public void onCreate(SQLiteDatabase db){
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITOLO + " TEXT, "
                + COLUMN_CONTENUTO + " TEXT, "
                +COLUMN_TIPO + " TEXT"
                + ");";
        // Esecuzione della Query per creare la tabella
        db.execSQL(createTableQuery);

    }
    //Metodo chiamato quando il DataBase viene aggiornato a una nuova versione
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Elimina la vecchia tabella se esiste e ricrea la tabella aggiornata
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
