package com.example.notesqlite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {
    private MyDBHelper dbHelper;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Inizializza il database lo chiamiamo DbHelper
        dbHelper = new MyDBHelper(this);
        // Setup del RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, noteList, dbHelper);
        noteAdapter.setOnNoteClickListener(this);
        recyclerView.setAdapter(noteAdapter);
        //Carica e mostra i dati
        displayData();
        // è un pulsante sospeso,consente di aggiungere le note
        FloatingActionButton fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInsertDialog();

            }
        });
    }
    // Mostra la finestra "dialogo" per l'inserimento di una nuova nota
    private void showInsertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crea Nota");
        //Utilizza LayoutInflater per personalizzare il Dialog
        View dialodView = LayoutInflater.from(this).inflate(R.layout.edit_note_dialog, null);
        builder.setView(dialodView);
        EditText etTitolo = dialodView.findViewById(R.id.etTitolo);
        EditText etContenuto = dialodView.findViewById(R.id.etContenuto);
        Spinner spinnerTipo = dialodView.findViewById(R.id.spinnerTipo);
        // Imposto l'ArrayAdapter con i valori dello spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"ALTA", "MEDIO", "BASSA"});
        spinnerTipo.setAdapter(adapter);
        // Imposto un pulsante per salvare gli elementi (Titolo, contenuto, ecc)
        builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whitch) {
                String titolo = etTitolo.getText().toString();
                String contenuto = etContenuto.getText().toString();
                String tipo = (String) spinnerTipo.getSelectedItem();


                insertData(titolo, contenuto, tipo);
            }
        });
        //Imposto un pulsante per cancellare
        builder.setNegativeButton("Cancella", null);
        builder.show();
    }
        // Inserisco i dati nel DataBase
    private void insertData(String titolo, String contenuto, String tipo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_TITOLO, titolo);
        values.put(MyDBHelper.COLUMN_CONTENUTO, contenuto);
        values.put(MyDBHelper.COLUMN_TIPO, tipo);

        long newRowId = db.insert(MyDBHelper.TABLE_NAME, null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Nota Inserita con Successo! ", Toast.LENGTH_SHORT).show();
            displayData();
        }
    }
    // Carico e mostro i dati dal DataBase
    private void displayData() {
        noteList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MyDBHelper.COLUMN_ID,
                MyDBHelper.COLUMN_TITOLO,
                MyDBHelper.COLUMN_CONTENUTO,
                MyDBHelper.COLUMN_TIPO
        };
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_ID));
            String titolo = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_TITOLO));
            String contenuto = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_CONTENUTO));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_TIPO));

            noteList.add(new Note(id, titolo, contenuto, tipo));

        }
        cursor.close();
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public void onDeleteClick(Note note) {
        showDeleteDialog(note);

    }

    @Override
    public void onEditClick(Note note) {
        showEditDialog(note);

    }
        // Mostra il dialogo per l'eliminazione di una nota
    private void showDeleteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elimina Nota");
        builder.setMessage("Sei sicuro di voler eliminare questa nota?");
        builder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData(note.getId());
            }
        });
        builder.setNegativeButton("Cancella", null);
        builder.show();
    }
        // Elimino i dati dal DB
    private void deleteData(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(MyDBHelper.TABLE_NAME, MyDBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        if (rowsAffected > 0) {
            Toast.makeText(this, "Nota cancellata con successo", Toast.LENGTH_SHORT).show();
            displayData();
        } else {
            Toast.makeText(this, "Errore durante l'eliminazione", Toast.LENGTH_SHORT).show();
        }
    }
        // Mostro un dialogo per la modifica di una nota
    private void showEditDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifica Nota");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.edit_note_dialog, null);
        builder.setView(dialogView);
        EditText etTitolo = dialogView.findViewById(R.id.etTitolo);
        EditText etContenuto = dialogView.findViewById(R.id.etContenuto);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);

        etTitolo.setText(note.getTitolo());
        etContenuto.setText(note.getContenuto());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"ALTA", "MEDIO", "BASSA"});
        spinnerTipo.setAdapter(adapter);

        String tipo = note.getTipo();
        if (tipo != null) {
            int position = adapter.getPosition(tipo);
            spinnerTipo.setSelection(position);
        }
        builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String updatedTitolo = etTitolo.getText().toString();
                String updatedContenuto = etContenuto.getText().toString();
                String updatedTipo = (String) spinnerTipo.getSelectedItem();
                updateData(note.getId(), updatedTitolo, updatedContenuto, updatedTipo);

            }
        });
        builder.setNegativeButton("Cancella", null);
        builder.show();
    }

    // Aggiorno i dati del DB

    private void updateData(int id, String updatedTitolo, String updatedContenuto, String updatedTipo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_TITOLO, updatedTitolo);
        values.put(MyDBHelper.COLUMN_CONTENUTO, updatedContenuto);
        values.put(MyDBHelper.COLUMN_TIPO, updatedTipo);
        int rowsAffected = db.update(MyDBHelper.TABLE_NAME, values,
                MyDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Nota modificata con successo!", Toast.LENGTH_SHORT).show();
            displayData();
        } else {
            Toast.makeText(this, "Errore durante la modifica nota", Toast.LENGTH_SHORT).show();
        }
    }
}