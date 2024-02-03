package com.example.notesqlite;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    // Definisco delle variabili di istanza, la Lista di note e il DbHelper
    private final Context context;
    private final List<Note> noteList;
    private MyDBHelper myDBHelper;
    private OnNoteClickListener onNoteClickListener;

    // Costruttore della classe NoteAdapter

    public NoteAdapter(Context context, List<Note> noteList, MyDBHelper myDBHelper) {
        this.context = context;
        this.noteList = noteList;
        this.myDBHelper = myDBHelper;

    }

    // Interfaccia che definisce i due metodi per la gestione dei click sugli elementi della
    //RecyclerView
    public interface OnNoteClickListener {
        void onDeleteClick(Note note);

        void onEditClick(Note note);

    }
    // Metodo per impostare l'ascoltatore per la gestione dei click sugli elementi della RecyclerView
    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;

    }
    // Sovrascrivo il metodo OnCreateViewHolder per creare una nuova istanza
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
        return new NoteViewHolder(view);
    }
    // Sovrascrivo il metodo onBindViewHolder per associare i dati delle note alla vista di appartenenza di quella nota
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.tvTitolo.setText(note.getTitolo());
        holder.tvContenuto.setText(note.getContenuto());
        String tipo = note.getTipo();
        int textColor = Color.BLACK;
        //Imposto i colori del testo del tipo della nota in base al valore ALTA, MEDIA O BASSA
        if ("ALTA".equals(tipo)) {
            textColor = Color.RED;

        } else if ("MEDIO".equals(tipo)) {
            textColor = Color.YELLOW;


        } else if ("BASSA".equals(tipo)) {
            textColor = Color.GREEN;


        }
        holder.tvTipo.setText(tipo);
        holder.tvTipo.setTextColor(textColor);
        // Gestisco i click sugli elementi della RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNoteClickListener != null) {
                    onNoteClickListener.onEditClick(note);
                }
            }
        });
        //Gestisco i LongClick sugli elementi della RecyclerView per mostrare le opzioni successive
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOptionsDialog(note);
                return true;
            }
        });


    }
    @Override
    //Sovrascrivo il metodo GetItemCount per ottenere il numero degli elementi nella lista delle note
    public int getItemCount(){
        return noteList.size();

    }
    //Classe interna per definire la vista degli elementi della RecyclerView
    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitolo;
        TextView tvContenuto;
        TextView tvTipo;
        public NoteViewHolder(View itemView) {
            super(itemView);
            tvTitolo = itemView.findViewById(R.id.tvTitolo);
            tvContenuto = itemView.findViewById(R.id.tvContenuto);
            tvTipo = itemView.findViewById(R.id.tvTipo);
        }
        }
        // Metodo privato per mostrare il dialogo con le opzioni modifica ed elimina
        private void showOptionsDialog(Note note){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Opzioni spazio nota");
        builder.setItems(new CharSequence[]{
                "modifica", "elimina"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case 0:
                        if (onNoteClickListener != null){
                            onNoteClickListener.onEditClick(note);
                        }
                        break;
                    case 1:
                        if (onNoteClickListener != null){
                            onNoteClickListener.onDeleteClick(note);
                        }
                        break;
                }

            }
        });
        builder.show();

    }
}
