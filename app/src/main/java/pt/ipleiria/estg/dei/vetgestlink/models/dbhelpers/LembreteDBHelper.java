package pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Lembrete;

public class LembreteDBHelper extends SQLiteOpenHelper {

    // Mudei para Lembretes.db para não conflitar com Notas.db
    private static final String DB_NAME = "Lembretes.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "lembretes";

    // Colunas da Tabela
    private static final String COL_ID = "id";
    private static final String COL_DESCRICAO = "descricao";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_UPDATED_AT = "updated_at";
    private static final String COL_USERPROFILES_ID = "userprofiles_id";

    public LembreteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_DESCRICAO + " TEXT, " +
                COL_CREATED_AT + " TEXT, " +
                COL_UPDATED_AT + " TEXT, " +
                COL_USERPROFILES_ID + " INTEGER" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- MÉTODOS CRUD ---

    public void adicionarLembreteBD(Lembrete lembrete) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_ID, lembrete.getId());
            values.put(COL_DESCRICAO, lembrete.getDescricao());
            values.put(COL_CREATED_AT, lembrete.getCreatedAt());
            values.put(COL_UPDATED_AT, lembrete.getUpdatedAt());
            values.put(COL_USERPROFILES_ID, lembrete.getUserprofilesId());

            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
    }

    public ArrayList<Lembrete> getAllLembretesBD() {
        ArrayList<Lembrete> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;

        try {
            c = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
                    String descricao = c.getString(c.getColumnIndexOrThrow(COL_DESCRICAO));
                    String createdAt = c.getString(c.getColumnIndexOrThrow(COL_CREATED_AT));
                    String updatedAt = c.getString(c.getColumnIndexOrThrow(COL_UPDATED_AT));
                    int userprofilesId = c.getInt(c.getColumnIndexOrThrow(COL_USERPROFILES_ID));

                    Lembrete lembrete = new Lembrete(id, descricao, createdAt, updatedAt, userprofilesId);
                    lista.add(lembrete);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
            db.close();
        }
        return lista;
    }

    public void removerAllLembretesBD() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    public void removerLembreteBD(int id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }
}
