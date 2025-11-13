package pt.ipleiria.estg.dei.vetgestlink.view.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.api.NotasApiService;
import pt.ipleiria.estg.dei.vetgestlink.api.UserProfileApiService;
import pt.ipleiria.estg.dei.vetgestlink.model.Animal;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.model.UserProfile;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.NotasAdapter;

/**
 * MainActivity - Tela de Notas dos Animais
 * CRUD completo de notas usando API
 */
public class MainActivity extends AppCompatActivity implements NotasAdapter.OnNotaClickListener {

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private BottomNavigationView bottomNavigation;
    private Spinner spinnerAnimais;
    private Button btnNovaAnotacao;
    private RecyclerView recyclerNotas;
    private SwipeRefreshLayout swipeRefresh;
    private NotasAdapter notasAdapter;

    private String accessToken;
    private String username;
    private List<Animal> animais;
    private Animal animalSelecionado;

    // Serviços de API
    private NotasApiService notasApiService;
    private UserProfileApiService userProfileApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obter username e token
        username = getIntent().getStringExtra("user_username");
        loadAccessToken();

        // Inicializar serviços de API
        notasApiService = new NotasApiService(this);
        userProfileApiService = new UserProfileApiService(this);

        // Inicializar views
        initViews();

        // Configurar RecyclerView
        setupRecyclerView();

        // Carregar dados da API
        loadUserProfileAndAnimals();

        // Configurar listeners
        setupListeners();
    }

    /**
     * Carrega o token de acesso do SharedPreferences
     */
    private void loadAccessToken() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        accessToken = prefs.getString(KEY_ACCESS_TOKEN, "");

        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Token de acesso não encontrado. Faça login novamente.", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        spinnerAnimais = findViewById(R.id.spinner_animais);
        btnNovaAnotacao = findViewById(R.id.btn_nova_anotacao);
        recyclerNotas = findViewById(R.id.recycler_notas);

        // SwipeRefreshLayout - se existir no layout
        swipeRefresh = findViewById(R.id.swipeRefresh);
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            );
        }
    }

    private void setupRecyclerView() {
        notasAdapter = new NotasAdapter(this);
        recyclerNotas.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotas.setAdapter(notasAdapter);
    }

    private void setupListeners() {
        // Bottom Navigation
        bottomNavigation.setOnItemSelectedListener(item -> handleNavigationItemSelected(item));

        // Selecionar item Notas por padrão
        bottomNavigation.setSelectedItemId(R.id.nav_notes);

        // Spinner de Animais
        spinnerAnimais.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (animais != null && !animais.isEmpty()) {
                    animalSelecionado = animais.get(position);
                    btnNovaAnotacao.setText("+ Nova Anotação para " + animalSelecionado.getNome());
                    carregarNotasDoAnimal(animalSelecionado.getId());
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Botão Nova Anotação
        btnNovaAnotacao.setOnClickListener(v -> mostrarDialogNovaAnotacao());

        // SwipeRefreshLayout
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                if (animalSelecionado != null) {
                    carregarNotasDoAnimal(animalSelecionado.getId());
                } else {
                    swipeRefresh.setRefreshing(false);
                }
            });
        }
    }

    /**
     * Carregar perfil do utilizador e lista de animais da API
     */
    private void loadUserProfileAndAnimals() {
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        userProfileApiService.getUserProfile(accessToken, new UserProfileApiService.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile userProfile, List<Animal> animaisList) {
                animais = animaisList;

                if (animais.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Nenhum animal encontrado", Toast.LENGTH_SHORT).show();
                    btnNovaAnotacao.setEnabled(false);
                    return;
                }

                // Configurar Spinner
                ArrayAdapter<Animal> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_spinner_item, animais);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAnimais.setAdapter(adapter);

                // Selecionar primeiro animal
                animalSelecionado = animais.get(0);
                btnNovaAnotacao.setText("+ Nova Anotação para " + animalSelecionado.getNome());
                carregarNotasDoAnimal(animalSelecionado.getId());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Erro: " + error, Toast.LENGTH_LONG).show();

                // Fallback para dados mockados
                loadMockDataFallback();
            }
        });
    }

    /**
     * Carregar dados mockados como fallback (caso API falhe)
     */
    private void loadMockDataFallback() {
        animais = new ArrayList<>();
        animais.add(new Animal(1, "Rex", "Cachorro", "Labrador Retriever"));
        animais.add(new Animal(2, "Miau", "Gato", "Persa"));

        ArrayAdapter<Animal> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, animais);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnimais.setAdapter(adapter);

        if (!animais.isEmpty()) {
            animalSelecionado = animais.get(0);
            carregarNotasDoAnimalMock(animalSelecionado.getId());
        }
    }

    /**
     * Carregar notas do animal da API
     */
    private void carregarNotasDoAnimal(int animalId) {
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        notasApiService.setNotasCallback(new NotasApiService.NotasCallback() {
            @Override
            public void onSuccess(List<Nota> notas) {
                notasAdapter.setNotas(notas);

                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }

                if (notas.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Nenhuma nota encontrada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Erro ao carregar notas: " + error, Toast.LENGTH_LONG).show();

                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }

                // Fallback para dados mockados
                carregarNotasDoAnimalMock(animalId);
            }
        });

        notasApiService.getNotas(accessToken, animalId);
    }

    /**
     * Carregar notas mockadas (fallback)
     */
    private void carregarNotasDoAnimalMock(int animalId) {
        List<Nota> notas = new ArrayList<>();

        Nota nota1 = new Nota();
        nota1.setId(1);
        nota1.setTitulo("Comportamento Estranho");
        nota1.setNota("O Rex tem agido menos ativo nos últimos 2 dias. Comeu menos ração no jantar de ontem.");
        nota1.setCreatedAt("2025-11-18 14:30:00");
        nota1.setUpdatedAt("2025-11-18 14:30:00");
        nota1.setAnimaisId(animalId);

        notas.add(nota1);
        notasAdapter.setNotas(notas);
    }

    /**
     * Mostrar dialog para criar nova anotação
     */
    private void mostrarDialogNovaAnotacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nota, null);

        EditText etTitulo = dialogView.findViewById(R.id.et_titulo);
        EditText etDescricao = dialogView.findViewById(R.id.et_descricao);

        builder.setView(dialogView)
            .setTitle("Nova Anotação - " + animalSelecionado.getNome())
            .setPositiveButton("Salvar", (dialog, which) -> {
                String titulo = etTitulo.getText().toString().trim();
                String descricao = etDescricao.getText().toString().trim();

                if (titulo.isEmpty() || descricao.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                criarNota(titulo, descricao);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    /**
     * Criar nova nota via API
     */
    private void criarNota(String titulo, String descricao) {
        if (accessToken.isEmpty() || animalSelecionado == null) {
            Toast.makeText(this, "Erro: dados inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combinar título e descrição
        String notaCompleta = titulo + "\n" + descricao;

        notasApiService.criarNota(accessToken, animalSelecionado.getId(), notaCompleta,
            new NotasApiService.NotaOperationCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    // Recarregar lista de notas
                    carregarNotasDoAnimal(animalSelecionado.getId());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, "Erro: " + error, Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Editar nota existente via API
     */
    @Override
    public void onEditClick(Nota nota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nota, null);

        EditText etTitulo = dialogView.findViewById(R.id.et_titulo);
        EditText etDescricao = dialogView.findViewById(R.id.et_descricao);

        // Preencher com dados existentes
        etTitulo.setText(nota.getTitulo());
        etDescricao.setText(nota.getDescricao());

        builder.setView(dialogView)
            .setTitle("Editar Anotação")
            .setPositiveButton("Salvar", (dialog, which) -> {
                String novoTitulo = etTitulo.getText().toString().trim();
                String novaDescricao = etDescricao.getText().toString().trim();

                if (novoTitulo.isEmpty() || novaDescricao.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Combinar título e descrição
                String notaCompleta = novoTitulo + "\n" + novaDescricao;

                notasApiService.atualizarNota(accessToken, nota.getId(), notaCompleta,
                    new NotasApiService.NotaOperationCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            // Recarregar lista
                            carregarNotasDoAnimal(animalSelecionado.getId());
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(MainActivity.this, "Erro: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    /**
     * Excluir nota via API
     */
    @Override
    public void onDeleteClick(Nota nota) {
        new AlertDialog.Builder(this)
            .setTitle("Excluir Nota")
            .setMessage("Deseja realmente excluir esta nota?")
            .setPositiveButton("Excluir", (dialog, which) -> {
                notasApiService.deletarNota(accessToken, nota.getId(),
                    new NotasApiService.NotaOperationCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            // Recarregar lista
                            carregarNotasDoAnimal(animalSelecionado.getId());
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(MainActivity.this, "Erro: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    /**
     * Trata a seleção de itens na Bottom Navigation
     */
    private boolean handleNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_appointments) {
            Toast.makeText(this, "Consultas em desenvolvimento", Toast.LENGTH_SHORT).show();
            return false;
        } else if (itemId == R.id.nav_payments) {
            Toast.makeText(this, "Pagamentos em desenvolvimento", Toast.LENGTH_SHORT).show();
            return false;
        } else if (itemId == R.id.nav_notes) {
            return true; // Já estamos na tela de notas
        } else if (itemId == R.id.nav_profile) {
            Toast.makeText(this, "Perfil em desenvolvimento", Toast.LENGTH_SHORT).show();
            return false;
        } else if (itemId == R.id.nav_settings) {
            Toast.makeText(this, "Configurações em desenvolvimento", Toast.LENGTH_SHORT).show();
            return false;
        }

        return false;
    }
}

