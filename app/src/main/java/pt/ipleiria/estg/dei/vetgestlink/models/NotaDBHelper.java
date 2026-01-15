package pt.ipleiria.estg.dei.vetgestlink.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NotaDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vetgestlink.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "notas";
    private static final String COL_ID = "id";
    private static final String COL_NOTA = "nota";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_UPDATED_AT = "updated_at";
    private static final String COL_USERPROFILE_ID = "userprofile_id";
    private static final String COL_ANIMAL_NOME = "animal_nome";
    private static final String COL_AUTOR = "autor";
    private static final String COL_TITULO = "titulo";

    public NotaDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_NOTA + " TEXT, " +
                COL_CREATED_AT + " TEXT, " +
                COL_UPDATED_AT + " TEXT, " +
                COL_USERPROFILE_ID + " INTEGER, " +
                COL_ANIMAL_NOME + " TEXT, " +
                COL_AUTOR + " TEXT, " +
                COL_TITULO + " TEXT" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simples estratégia: apagar e recriar. Ajustar conforme necessário.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Nota> getAllNotasBD() {
        ArrayList<Nota> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
                    String notaText = c.getString(c.getColumnIndexOrThrow(COL_NOTA));
                    String createdAt = c.getString(c.getColumnIndexOrThrow(COL_CREATED_AT));
                    String updatedAt = c.getString(c.getColumnIndexOrThrow(COL_UPDATED_AT));
                    int userprofileId = c.getInt(c.getColumnIndexOrThrow(COL_USERPROFILE_ID));
                    String animalNome = c.getString(c.getColumnIndexOrThrow(COL_ANIMAL_NOME));
                    String autor = c.getString(c.getColumnIndexOrThrow(COL_AUTOR));
                    String titulo = c.getString(c.getColumnIndexOrThrow(COL_TITULO));

                    Nota n = new Nota(id, notaText, createdAt, updatedAt, animalNome, autor, titulo, userprofileId);
                    lista.add(n);
                }
            } finally {
                c.close();
            }
        }
        db.close();
        return lista;
    }

    public void adicionarNotaBD(Nota nota) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, nota.getId());
        values.put(COL_NOTA, nota.getNota());
        values.put(COL_CREATED_AT, nota.getCreatedAt());
        values.put(COL_UPDATED_AT, nota.getUpdatedAt());
        values.put(COL_USERPROFILE_ID, nota.getUserprofileId());
        values.put(COL_ANIMAL_NOME, nota.getAnimalNome());
        values.put(COL_AUTOR, nota.getAutor());
        values.put(COL_TITULO, nota.getTitulo());

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void removerAllNotasBD() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void removerNotaBD(int idNota) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(idNota)});
        db.close();
    }
}
