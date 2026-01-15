package pt.ipleiria.estg.dei.vetgestlink.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MarcacaoDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "marcacoes_db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "marcacoes";

    private static final String ID = "id";
    private static final String DATA = "data";
    private static final String HORA_INICIO = "horaInicio";
    private static final String HORA_FIM = "horaFim";
    private static final String ESTADO = "estado";
    private static final String DURACAO = "duracaoMinutos";
    private static final String DIAGNOSTICO = "diagnostico";
    private static final String SERVICO = "servicoNome";
    private static final String ANIMAL_NOME = "animalNome";
    private static final String ANIMAL_ESPECIE = "animalEspecie";

    private SQLiteDatabase db;

    public MarcacaoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY, " +
                DATA + " TEXT, " +
                HORA_INICIO + " TEXT, " +
                HORA_FIM + " TEXT, " +
                ESTADO + " TEXT, " +
                DURACAO + " INTEGER, " +
                DIAGNOSTICO + " TEXT, " +
                SERVICO + " TEXT, " +
                ANIMAL_NOME + " TEXT, " +
                ANIMAL_ESPECIE + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void adicionarMarcacaoBD(Marcacao m) {
        ContentValues values = new ContentValues();
        values.put(ID, m.getId());
        values.put(DATA, m.getData());
        values.put(HORA_INICIO, m.getHoraInicio());
        values.put(HORA_FIM, m.getHoraFim());
        values.put(ESTADO, m.getEstado());
        values.put(DURACAO, m.getDuracaoMinutos());
        values.put(DIAGNOSTICO, m.getDiagnostico());
        values.put(SERVICO, m.getServicoNome());
        values.put(ANIMAL_NOME, m.getAnimalNome());
        values.put(ANIMAL_ESPECIE, m.getAnimalEspecie());
        db.insert(TABLE_NAME, null, values);
    }

    public ArrayList<Marcacao> getAllMarcacoesBD() {
        ArrayList<Marcacao> lista = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(new Marcacao(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8),
                        cursor.getString(9)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void removerAllMarcacoesBD() {
        db.delete(TABLE_NAME, null, null);
    }
}