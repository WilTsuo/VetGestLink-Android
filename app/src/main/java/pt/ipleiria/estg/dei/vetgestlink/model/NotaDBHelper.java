package pt.ipleiria.estg.dei.vetgestlink.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NotaDBHelper extends SQLiteOpenHelper {
    private static final String BD_NOME = "bdNotas";
    private static final String TABELA_NOME = "notas";

    private final String ID = "id";
    private final String NOTA = "nota";
    private final String CREATED_AT = "created_at";
    private final String UPDATED_AT = "updated_at";
    private final String ANIMALNOME = "animalNome";
    private final String AUTOR = "autor";

    private static final int BD_VERSION = 1;

    private final SQLiteDatabase bd;

    public NotaDBHelper(@Nullable Context context) {
        super(context, BD_NOME, null, BD_VERSION);
        this.bd = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTableNotas =
                "CREATE TABLE " + TABELA_NOME + " (" +
                        ID + " INTEGER PRIMARY KEY, " +
                        NOTA + " TEXT NOT NULL, " +
                        CREATED_AT + " TEXT, " +
                        UPDATED_AT + " TEXT, " +
                        ANIMALNOME + " TEXT, " +
                        AUTOR + " TEXT" +
                        ");";

        sqLiteDatabase.execSQL(createTableNotas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_NOME);
        onCreate(db);
    }

    //region - CRUD
    public ArrayList<Nota> getAllNotasBD() {

        ArrayList<Nota> lista = new ArrayList<>();

        Cursor cursor = this.bd.query(
                TABELA_NOME,
                new String[]{ID, NOTA, CREATED_AT, UPDATED_AT, ANIMALNOME, AUTOR},
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                Nota nota = new Nota(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        null
                );

                lista.add(nota);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }
    public Nota adicionarNotaBD(Nota n) {

        ContentValues values = new ContentValues();
        values.put(ID, n.getId());
        values.put(NOTA, n.getNota());
        values.put(CREATED_AT, n.getCreatedAt());
        values.put(UPDATED_AT, n.getUpdatedAt());
        values.put(ANIMALNOME, n.getAnimalNome());
        values.put(AUTOR, n.getUpdatedAt());

        long id = this.bd.insert(TABELA_NOME, null, values);

        return id > -1 ? n : null;
    }

    public boolean removerNotaBD(int id) {
        int linhas = this.bd.delete(TABELA_NOME, ID + "=?", new String[]{String.valueOf(id)});
        return linhas > 0;
    }

    public void removerAllNotasBD() {

        this.bd.delete(TABELA_NOME, null, null);
    }


    //endregion

}
