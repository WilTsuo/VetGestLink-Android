package pt.ipleiria.estg.dei.vetgestlink.models.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Animal;

public class AnimalDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Animais.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "animal";

    // Colunas da Tabela
    private static final String COL_ID = "id";
    private static final String COL_NOME = "nome";
    private static final String COL_ESPECIE = "especie";
    private static final String COL_RACA = "raca";
    private static final String COL_IDADE = "idade";
    private static final String COL_PESO = "peso";
    private static final String COL_SEXO = "sexo";
    private static final String COL_DATANASCIMENTO = "datanascimento";
    private static final String COL_MICROCHIP = "microchip";
    private static final String COL_FOTO_URL = "foto_url";
    private static final String COL_USERPROFILES_ID = "userprofiles_id";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_UPDATED_AT = "updated_at";

    public AnimalDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_NOME + " TEXT, " +
                COL_ESPECIE + " TEXT, " +
                COL_RACA + " TEXT, " +
                COL_IDADE + " INTEGER, " +
                COL_PESO + " REAL, " +
                COL_SEXO + " TEXT, " +
                COL_DATANASCIMENTO + " TEXT, " +
                COL_MICROCHIP + " INTEGER, " +
                COL_FOTO_URL + " TEXT, " +
                COL_USERPROFILES_ID + " INTEGER, " +
                COL_CREATED_AT + " TEXT, " +
                COL_UPDATED_AT + " TEXT" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- MÉTODOS CRUD ---

    public void adicionarAnimalBD(Animal animal) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_ID, animal.getId());
            values.put(COL_NOME, animal.getNome());
            values.put(COL_ESPECIE, animal.getEspecie());
            values.put(COL_RACA, animal.getRaca());
            values.put(COL_IDADE, animal.getIdade());
            values.put(COL_PESO, animal.getPeso());
            values.put(COL_SEXO, animal.getSexo());
            values.put(COL_DATANASCIMENTO, animal.getDtanascimento());
            values.put(COL_MICROCHIP, animal.getMicrochip());
            values.put(COL_FOTO_URL, animal.getFotoUrl());
            values.put(COL_USERPROFILES_ID, animal.getUserprofilesId());
            values.put(COL_CREATED_AT, animal.getCreatedAt());
            values.put(COL_UPDATED_AT, animal.getUpdatedAt());

            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
    }

    public ArrayList<Animal> getAllAnimaisBD() {
        ArrayList<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;

        try {
            c = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    Animal animal = new Animal(
                            c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                            c.getString(c.getColumnIndexOrThrow(COL_NOME)),
                            c.getString(c.getColumnIndexOrThrow(COL_ESPECIE)),
                            c.getString(c.getColumnIndexOrThrow(COL_RACA)),
                            c.getString(c.getColumnIndexOrThrow(COL_IDADE)),
                            c.getDouble(c.getColumnIndexOrThrow(COL_PESO)),
                            c.getString(c.getColumnIndexOrThrow(COL_SEXO)),
                            c.getInt(c.getColumnIndexOrThrow(COL_MICROCHIP)),
                            c.getString(c.getColumnIndexOrThrow(COL_FOTO_URL)),
                            c.getString(c.getColumnIndexOrThrow(COL_DATANASCIMENTO)),
                            c.getInt(c.getColumnIndexOrThrow(COL_USERPROFILES_ID)),
                            c.getString(c.getColumnIndexOrThrow(COL_CREATED_AT)),
                            c.getString(c.getColumnIndexOrThrow(COL_UPDATED_AT))
                    );
                    lista.add(animal);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
            db.close();
        }
        return lista;
    }

    public void removerAllAnimaisBD() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    public void removerAnimalBD(int idAnimal) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(idAnimal)});
        } finally {
            db.close();
        }
    }
}
