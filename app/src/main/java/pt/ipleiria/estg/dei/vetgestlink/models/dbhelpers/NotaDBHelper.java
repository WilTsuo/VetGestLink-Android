package pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Nota;

public class NotaDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Notas.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "notas";

    // Colunas da Tabela
    private static final String COL_ID = "id";
    private static final String COL_NOTA = "nota";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_UPDATED_AT = "updated_at";
    private static final String COL_USERPROFILE_ID = "userprofile_id";
    private static final String COL_ANIMAL_NOME = "animal_nome";
    private static final String COL_AUTOR = "autor";
    private static final String COL_TITULO = "titulo";

    public NotaDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- MÉTODOS CRUD ---

    public void adicionarNotaBD(Nota nota) {
        SQLiteDatabase db = getWritableDatabase();
        try {
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
        } finally {
            db.close();
        }
    }

    public ArrayList<Nota> getAllNotasBD() {
        ArrayList<Nota> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;

        try {
            c = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    lista.add(cursorToNota(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
            db.close();
        }
        return lista;
    }

    public void removerAllNotasBD() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    public void removerNotaBD(int idNota) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(idNota)});
        } finally {
            db.close();
        }
    }

    // Método específico para filtrar notas por animal
    public ArrayList<Nota> getNotasByAnimalNome(String animalNome) {
        ArrayList<Nota> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;

        try {
            c = db.query(
                    TABLE_NAME,
                    null,
                    COL_ANIMAL_NOME + " = ?",
                    new String[]{animalNome},
                    null, null,
                    COL_CREATED_AT + " DESC"
            );

            if (c != null && c.moveToFirst()) {
                do {
                    lista.add(cursorToNota(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
            db.close();
        }
        return lista;
    }

    // Método auxiliar para evitar repetição de código
    private Nota cursorToNota(Cursor c) {
        return new Nota(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NOTA)),
                c.getString(c.getColumnIndexOrThrow(COL_CREATED_AT)),
                c.getString(c.getColumnIndexOrThrow(COL_UPDATED_AT)),
                c.getString(c.getColumnIndexOrThrow(COL_ANIMAL_NOME)),
                c.getString(c.getColumnIndexOrThrow(COL_AUTOR)),
                c.getString(c.getColumnIndexOrThrow(COL_TITULO)),
                c.getInt(c.getColumnIndexOrThrow(COL_USERPROFILE_ID))
        );
    }
}
