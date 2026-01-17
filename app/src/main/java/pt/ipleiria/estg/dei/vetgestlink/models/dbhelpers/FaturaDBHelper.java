package pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;

public class FaturaDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "vetgestlink_faturas.db";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "faturas";

    // Colunas da Tabela
    private static final String COL_ID = "id";
    private static final String COL_DATA = "data";
    private static final String COL_TOTAL = "total";
    private static final String COL_ESTADO = "estado";
    private static final String COL_ELIMINADO = "eliminado";
    private static final String COL_METODO_PAGAMENTO = "metodo_pagamento";
    private static final String COL_NUMERO_ITENS = "numero_itens";
    private static final String COL_CLIENTE_NOME = "cliente_nome";

    public FaturaDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_DATA + " TEXT, " +
                COL_TOTAL + " REAL, " +
                COL_ESTADO + " INTEGER, " +            // 0 ou 1
                COL_ELIMINADO + " INTEGER, " +         // 0 ou 1
                COL_METODO_PAGAMENTO + " TEXT, " +
                COL_NUMERO_ITENS + " INTEGER, " +
                COL_CLIENTE_NOME + " TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- MÉTODOS CRUD ---

    public void adicionarFaturaBD(Fatura fatura) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_ID, fatura.getId());
            values.put(COL_DATA, fatura.getCreatedAt());
            values.put(COL_TOTAL, fatura.getTotal());
            values.put(COL_ESTADO, fatura.isEstado() ? 1 : 0);
            values.put(COL_ELIMINADO, fatura.isEliminado() ? 1 : 0);
            values.put(COL_METODO_PAGAMENTO, fatura.getMetodoPagamento());
            values.put(COL_NUMERO_ITENS, fatura.getNumeroItens());
            values.put(COL_CLIENTE_NOME, fatura.getClienteNome() != null ? fatura.getClienteNome() : "N/A");

            // Substitui se o ID já existir, insere se não existir
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
    }

    public ArrayList<Fatura> getAllFaturasBD() {
        ArrayList<Fatura> faturas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Fatura fatura = cursorToFatura(cursor);
                    faturas.add(fatura);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return faturas;
    }

    public void removerAllFaturasBD() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    // Método auxiliar para converter Cursor em Objeto
    private Fatura cursorToFatura(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
        float total = (float) cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL));
        String data = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA));
        boolean estado = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ESTADO)) == 1;
        boolean eliminado = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ELIMINADO)) == 1;
        String metodoPagamento = cursor.getString(cursor.getColumnIndexOrThrow(COL_METODO_PAGAMENTO));
        int numeroItens = cursor.getInt(cursor.getColumnIndexOrThrow(COL_NUMERO_ITENS));
        String clienteNome = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOME));

        Fatura fatura = new Fatura(id, total, data, estado, eliminado, metodoPagamento, numeroItens);
        fatura.setClienteNome(clienteNome);
        return fatura;
    }
}
