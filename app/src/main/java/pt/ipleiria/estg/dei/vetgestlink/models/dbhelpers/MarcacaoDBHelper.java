package pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;

public class MarcacaoDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Marcacoes.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "marcacoes";

    // Colunas da Tabela
    private static final String COL_ID = "id";
    private static final String COL_DATA = "data";
    private static final String COL_HORA_INICIO = "horaInicio";
    private static final String COL_HORA_FIM = "horaFim";
    private static final String COL_ESTADO = "estado";
    private static final String COL_DURACAO = "duracaoMinutos";
    private static final String COL_DIAGNOSTICO = "diagnostico";
    private static final String COL_SERVICO = "servicoNome";
    private static final String COL_ANIMAL_NOME = "animalNome";
    private static final String COL_ANIMAL_ESPECIE = "animalEspecie";

    public MarcacaoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_DATA + " TEXT, " +
                COL_HORA_INICIO + " TEXT, " +
                COL_HORA_FIM + " TEXT, " +
                COL_ESTADO + " TEXT, " +
                COL_DURACAO + " INTEGER, " +
                COL_DIAGNOSTICO + " TEXT, " +
                COL_SERVICO + " TEXT, " +
                COL_ANIMAL_NOME + " TEXT, " +
                COL_ANIMAL_ESPECIE + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- MÉTODOS CRUD ---

    public void adicionarMarcacaoBD(Marcacao m) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_ID, m.getId());
            values.put(COL_DATA, m.getData());
            values.put(COL_HORA_INICIO, m.getHoraInicio());
            values.put(COL_HORA_FIM, m.getHoraFim());
            values.put(COL_ESTADO, m.getEstado());
            values.put(COL_DURACAO, m.getDuracaoMinutos());
            values.put(COL_DIAGNOSTICO, m.getDiagnostico());
            values.put(COL_SERVICO, m.getServicoNome());
            values.put(COL_ANIMAL_NOME, m.getAnimalNome());
            values.put(COL_ANIMAL_ESPECIE, m.getAnimalEspecie());

            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
    }

    public ArrayList<Marcacao> getAllMarcacoesBD() {
        ArrayList<Marcacao> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(new Marcacao(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_HORA_INICIO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_HORA_FIM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ESTADO)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COL_DURACAO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_DIAGNOSTICO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ANIMAL_NOME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ANIMAL_ESPECIE))
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return lista;
    }

    public void removerAllMarcacoesBD() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    public void removerMarcacaoBD(int id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }
}
