package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.DefinicoesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.LembretesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.MarcacoesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.NotasFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.PagamentosFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.PerfilFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
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
        Fragment fragment_marcacoes = new MarcacoesFragment();
        Fragment fragment_pagamentos = new PagamentosFragment();
        Fragment fragment_notas = new NotasFragment();
        Fragment fragment_perfil = new PerfilFragment();
        Fragment fragment_definicoes = new DefinicoesFragment();

        bottomNavigationView.setSelectedItemId(R.id.notas);
        setCurrentFragment(fragment_notas, "Notas");

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.consultas) {
                setCurrentFragment(fragment_marcacoes, "Marcação de Consultas");
            } else if (id == R.id.pagamentos) {
                setCurrentFragment(fragment_pagamentos, "Historico de Pagamentos");
            } else if (id == R.id.notas) {
                setCurrentFragment(fragment_notas, "Notas e Lembretes");
            } else if (id == R.id.perfil) {
                setCurrentFragment(fragment_perfil, "Perfil do Utilizador");
            } else if (id == R.id.definicoes) {
                setCurrentFragment(fragment_definicoes, "Definições");
            }
            return true;
        });
    }

    //define qual dos fragments vai ser mostrado
    private void setCurrentFragment(Fragment fragment, String title) {
        //muda o fragment que está a ser mostrado
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        TextView toolbar_subtitle = findViewById(R.id.toolbar_subtitle);
        toolbar_subtitle.setText(title);
    }

    public void navegarParaLembretes() {
        Fragment fragment = new LembretesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        TextView toolbarSubtitle = findViewById(R.id.toolbar_subtitle);
        toolbarSubtitle.setText("Lembretes");
    }

}
