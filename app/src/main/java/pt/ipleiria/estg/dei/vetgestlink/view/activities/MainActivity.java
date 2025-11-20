// app/src/main/java/pt/ipleiria/estg/dei/vetgestlink/view/activities/MainActivity.java
package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.api.NotasApiService;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.MainActivityAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView; // pode ser usado futuramente

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar Toolbar (uso local)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remover content insets que podem deslocar o conteúdo (garante logo à esquerda)
        toolbar.setContentInsetsRelative(0, 0);
        // Garantir que não exista navigation icon a empurrar o logo
        toolbar.setNavigationIcon(null);

        // Programmatically ensure logo and title behavior so the logo is visible
        toolbar.setLogo(R.drawable.logo_vetgestlink);
        toolbar.setTitle("");

        // Ensure toolbar logo is shown
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            // Hide default title so the custom TextView inside the Toolbar is used
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        // SharedPreferences (uso local)
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        if (token == null || token.isEmpty()) {
            // Token ausente: voltar para Login
            Toast.makeText(this, "Sessão não encontrada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Configurar ListView
        ListView listView = findViewById(R.id.listView);

        MainActivityAdapter adapter = new MainActivityAdapter(this, new MainActivityAdapter.OnNotaClickListener() {
            @Override
            public void onEditClick(Nota nota) {
                Toast.makeText(MainActivity.this, "Editar: " + nota.getTitulo(), Toast.LENGTH_SHORT).show();
                // Aqui pode abrir dialog_nota para editar
            }

            @Override
            public void onDeleteClick(Nota nota) {
                Toast.makeText(MainActivity.this, "Excluir: " + nota.getTitulo(), Toast.LENGTH_SHORT).show();
                // Implementar chamada à API para deletar e atualizar adapter se necessário
            }
        });

        listView.setAdapter(adapter);

        // Inicializar serviço de notas e buscar (uso local)
        NotasApiService notasApiService = new NotasApiService(this);
        notasApiService.setNotasCallback(new NotasApiService.NotasCallback() {
            @Override
            public void onSuccess(List<Nota> notas) {
                runOnUiThread(() -> {
                    adapter.setNotas(notas);
                    if (notas == null || notas.isEmpty()) {
                        Toast.makeText(MainActivity.this, R.string.no_notes, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show());
            }
        });

        notasApiService.getNotas(token, null);
    }
}
