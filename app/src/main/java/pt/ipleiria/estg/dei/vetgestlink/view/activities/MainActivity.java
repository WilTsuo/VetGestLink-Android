package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.MainActivityAdapter;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.DefinicoesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.MarcacoesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.NotasFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.PagamentosFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.PerfilFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * // Configurar ListView
     *         ListView listView = findViewById(R.id.listView);
     *
     *         MainActivityAdapter adapter = new MainActivityAdapter(this, new MainActivityAdapter.OnNotaClickListener() {
     *             @Override
     *             public void onEditClick(Nota nota) {
     *                 Toast.makeText(MainActivity.this, "Editar: " + nota.getTitulo(), Toast.LENGTH_SHORT).show();
     *                 // Aqui pode abrir dialog_nota para editar
     *             }
     *
     *             @Override
     *             public void onDeleteClick(Nota nota) {
     *                 Toast.makeText(MainActivity.this, "Excluir: " + nota.getTitulo(), Toast.LENGTH_SHORT).show();
     *                 // Implementar chamada à API para deletar e atualizar adapter se necessário
     *             }
     *         });
     *
     *         listView.setAdapter(adapter);
     *
     *         // Inicializar serviço de notas e buscar (uso local)
     *         Singleton singleton = Singleton.getInstance(MainActivity.this);
     *
     *         singleton.getNotas(token, 1, new Singleton.NotasCallback() {
     *             @Override
     *             public void onSuccess(List<Nota> notas) {
     *                 runOnUiThread(() -> {
     *                     adapter.setNotas(notas);
     *                     if (notas == null || notas.isEmpty()) {
     *                         Toast.makeText(MainActivity.this, R.string.no_notes, Toast.LENGTH_SHORT).show();
     *                     }
     *                 });
     *             }
     *
     *             @Override
     *             public void onError(String error) {
     *                 runOnUiThread(() -> Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show());
     *             }
     *         });
     * **/
    private BottomNavigationView bottomNavigationView; // pode ser usado futuramente

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    // Metodo onCreate onde tudo começa e tudo é inicializado.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar o menu que fica em baixo "bottomNavigationView"
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // SharedPreferences (uso local)
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sessão não encontrada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        //tratamento dos fragments para o bottom navigaton view
        Fragment firstFragment = new MarcacoesFragment();
        Fragment secondFragment = new PagamentosFragment();
        Fragment thirdFragment = new NotasFragment();
        Fragment fourthfragment = new PerfilFragment();
        Fragment fithfragment = new DefinicoesFragment();


        setCurrentFragment(thirdFragment);


    }

    //define qual dos fragments vai ser mostrado
    private void setCurrentFragment(Fragment fragment) {
        //atualiza o bottomNavigation view para mostrra como selecionado o fragment correspondente


        //muda o fragment que está a ser mostrado
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
