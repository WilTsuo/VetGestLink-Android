package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.DefinicoesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.LembretesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.MarcacoesFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.NotasFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.PagamentosFragment;
import pt.ipleiria.estg.dei.vetgestlink.view.fragments.PerfilFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CardView warningBar;
    private Handler handler;
    private Runnable apiCheckRunnable;
    private ConnectivityManager.NetworkCallback networkCallback;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final int API_CHECK_INTERVAL = 10000; // 10 segundos

    // Metodo onCreate onde tudo começa e tudo é inicializado.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar o menu que fica em baixo "bottomNavigationView"
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Inicializar a barra de aviso
        warningBar = findViewById(R.id.warning_bar);

        // Inicializar Handler para verificações periódicas
        handler = new Handler(Looper.getMainLooper());

        // SharedPreferences (uso local)
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sessão não encontrada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Configurar monitoramento de conectividade
        setupNetworkCallback();

        // Verificar o estado da API e atualizar a barra de aviso
        updateApiWarningBar();

        // Iniciar verificações periódicas
        startPeriodicApiCheck();

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

    @Override
    protected void onResume() {
        super.onResume();
        // Atualizar o estado da barra sempre que a activity voltar ao primeiro plano
        updateApiWarningBar();
        // Reiniciar verificações periódicas
        startPeriodicApiCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Parar verificações periódicas quando a activity não está visível
        stopPeriodicApiCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remover NetworkCallback
        if (networkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm != null) {
                cm.unregisterNetworkCallback(networkCallback);
            }
        }
        // Parar verificações periódicas
        stopPeriodicApiCheck();
    }

    private void setupNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // Quando a rede ficar disponível, verificar a API
                updateApiWarningBar();
            }

            @Override
            public void onLost(@NonNull Network network) {
                // Quando perder a rede, mostrar a barra imediatamente
                runOnUiThread(() -> {
                    if (warningBar != null) {
                        warningBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        cm.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void startPeriodicApiCheck() {
        // Parar qualquer verificação anterior
        stopPeriodicApiCheck();

        // Criar Runnable para verificação periódica
        apiCheckRunnable = new Runnable() {
            @Override
            public void run() {
                updateApiWarningBar();
                // Agendar próxima verificação
                handler.postDelayed(this, API_CHECK_INTERVAL);
            }
        };

        // Iniciar verificações periódicas
        handler.postDelayed(apiCheckRunnable, API_CHECK_INTERVAL);
    }

    private void stopPeriodicApiCheck() {
        if (handler != null && apiCheckRunnable != null) {
            handler.removeCallbacks(apiCheckRunnable);
        }
    }

    private void updateApiWarningBar() {
        // Verificar o estado da API com o Singleton (verificação rápida com cache)
        Singleton.getInstance(this).quickCheckApiState(this, responding -> {
            runOnUiThread(() -> {
                if (warningBar != null) {
                    if (responding) {
                        // API disponível - esconder a barra
                        warningBar.setVisibility(View.GONE);
                    } else {
                        // API indisponível - mostrar a barra
                        warningBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        });
    }

}
