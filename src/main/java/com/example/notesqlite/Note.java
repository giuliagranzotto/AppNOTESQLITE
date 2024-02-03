package com.example.notesqlite;

public class Note {
    //Definizione delle variabili di istanza per rappresentare la nota
    private int id;
    private String titolo;
    private String contenuto;
    private String tipo;

    // Costruttore della classe Note
    public Note(int id, String titolo, String contenuto, String tipo){
        this.id = id;
        this.titolo = titolo;
        this.contenuto = contenuto;
        this.tipo = tipo;

    }
    // Creo i metodi Getter per accedere alle variabili di istanza della nota
    public int getId(){
        return id;

    }

    public String getTitolo() {
        return titolo;
    }

    public String getContenuto() {
        return contenuto;
    }

    public String getTipo() {
        return tipo;
    }

}
