package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.listeners.MarcacoesListener;
import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.MarcacoesAdapter;

public class MarcacoesFragment extends Fragment implements MarcacoesListener, Singleton.ApiStateChangeListener {

    private RecyclerView rvMarcacoes;
    private MarcacoesAdapter adapter;
    private Button btnFilterPendente, btnFilterCancelada, btnFilterRealizada;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private MaterialAutoCompleteTextView autoCompleteAnimal;

    private String currentToken;
    private ArrayList<Marcacao> listaOriginal = new ArrayList<>();
    private String animalSelecionado = "Todos";
    private String estadoSelecionado = "Todas";

    private ConnectivityManager.NetworkCallback networkCallback;
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marcacoes, container, false);

        // Inicialização das Views
        rvMarcacoes = view.findViewById(R.id.rvMarcacoes);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEstadoVazio);
        autoCompleteAnimal = view.findViewById(R.id.autoCompleteAnimal);

        btnFilterPendente = view.findViewById(R.id.btnFiltrarPendente);
        btnFilterCancelada = view.findViewById(R.id.btnFiltrarCancelada);
        btnFilterRealizada = view.findViewById(R.id.btnFiltrarRealizada);

        // Configuração da RecyclerView
        rvMarcacoes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MarcacoesAdapter(getContext(), new ArrayList<>());

        // Configurar o clique na lista
        adapter.setOnItemClickListener(marcacao -> {
            // Chama o Singleton para buscar os detalhes completos da API
            Singleton.getInstance(requireContext()).getMarcacaoDetalhesAPI(marcacao.getId(), requireContext());
        });

        rvMarcacoes.setAdapter(adapter);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentToken = prefs.getString(KEY_ACCESS_TOKEN, "");

        configurarFiltrosEstado();
        configurarFiltroAnimal();

        // Registrar listeners
        Singleton.getInstance(getContext()).setMarcacoesListener(this);
        Singleton.getInstance(requireContext()).addApiStateChangeListener(this);

        // Setup NetworkCallback para reagir a mudanças de rede
        setupNetworkCallback();

        // Carregar dados iniciais
        loadMarcacoes();

        return view;
    }

    // --- IMPLEMENTAÇÃO DO DIÁLOGO DE DETALHES ---

    @Override
    public void onMarcacaoDetalhesLoaded(Marcacao marcacaoDetalhada) {
        // Este método é chamado pelo Singleton quando os dados chegam da API
        if (getContext() != null && isAdded()) {
            mostrarDialogDetalhes(marcacaoDetalhada);
        }
    }

    private void mostrarDialogDetalhes(Marcacao m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_marcacao_detalhes, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Referências UI do XML dialog_marcacao_detalhes.xml
        TextView tvDataHora = dialogView.findViewById(R.id.tvDialogDataHora);
        TextView tvEstado = dialogView.findViewById(R.id.tvDialogEstado);
        TextView tvServico = dialogView.findViewById(R.id.tvDialogServico);

        // ALTERAÇÃO: Referências para os novos campos separados do animal
        TextView tvAnimalNome = dialogView.findViewById(R.id.tvDialogAnimalNome);
        TextView tvAnimalEspecie = dialogView.findViewById(R.id.tvDialogAnimalEspecie);
        TextView tvAnimalRaca = dialogView.findViewById(R.id.tvDialogAnimalRaca);

        TextView tvDiagnostico = dialogView.findViewById(R.id.tvDialogDiagnostico);
        Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        // Preencher Dados
        tvDataHora.setText(m.getData() + " | " + m.getHoraInicio());

        // Estado e Cor
        tvEstado.setText(m.getEstado());
        if ("realizada".equalsIgnoreCase(m.getEstado())) {
            tvEstado.setTextColor(Color.parseColor("#2E7D32")); // Verde
        } else if ("cancelada".equalsIgnoreCase(m.getEstado())) {
            tvEstado.setTextColor(Color.RED);
        } else {
            tvEstado.setTextColor(Color.parseColor("#F59E0B")); // Laranja/Amarelo para pendente
        }

        // Serviço e Duração
        String servicoTxt = m.getServicoNome();
        if (m.getDuracaoMinutos() > 0) {
            servicoTxt += " (" + m.getDuracaoMinutos() + " min)";
        }
        tvServico.setText(servicoTxt);

        // ALTERAÇÃO: Preenchimento dos campos separados
        tvAnimalNome.setText(m.getAnimalNome() != null ? m.getAnimalNome() : "-");
        tvAnimalEspecie.setText(m.getAnimalEspecie() != null ? m.getAnimalEspecie() : "-");

        // Assumindo que o modelo Marcacao possui o método getAnimalEspecie()
        tvAnimalRaca.setText(m.getAnimalEspecie() != null ? m.getAnimalEspecie() : "-");

        // Diagnóstico
        String diag = m.getDiagnostico();
        if (diag == null || diag.isEmpty() || diag.equals("null")) {
            tvDiagnostico.setText("Sem notas registadas.");
        } else {
            tvDiagnostico.setText(diag);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // --- FIM DA IMPLEMENTAÇÃO DO DIÁLOGO ---

    private void loadMarcacoes() {
        if (currentToken == null || currentToken.isEmpty()) {
            carregarCache();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Singleton.getInstance(requireContext()).getMarcacoes(currentToken, new Singleton.MarcacoesCallback() {
            @Override
            public void onSuccess(ArrayList<Marcacao> marcacoes) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    listaOriginal = marcacoes;
                    atualizarDropdownAnimais();
                    aplicarFiltros();
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    // Se falhar a API, carrega do cache
                    carregarCache();
                    Toast.makeText(getContext(), "Modo Offline: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void carregarCache() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            ArrayList<Marcacao> cachedMarcacoes = Singleton.getInstance(requireContext()).getMarcacoesBD();

            listaOriginal.clear();
            if (cachedMarcacoes != null) {
                listaOriginal.addAll(cachedMarcacoes);
            }

            atualizarDropdownAnimais();
            aplicarFiltros();
            progressBar.setVisibility(View.GONE);

            Log.d("VetGestLink-Marcacoes", "Carregou dados do cache local");
        });
    }

    private void setupNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // Quando a rede ficar disponível, verificar a API e recarregar dados
                Singleton.getInstance(requireContext()).quickCheckApiState(requireContext(), responding -> {
                    if (isAdded() && responding) {
                        requireActivity().runOnUiThread(() -> loadMarcacoes());
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                // Quando perder a rede, garantir que mostramos o cache
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> carregarCache());
                }
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        cm.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void configurarFiltroAnimal() {
        autoCompleteAnimal.setOnItemClickListener((parent, view, position, id) -> {
            animalSelecionado = (String) parent.getItemAtPosition(position);
            aplicarFiltros();
        });
    }

    private void atualizarDropdownAnimais() {
        Set<String> nomesAnimais = new HashSet<>();
        nomesAnimais.add("Todos");
        for (Marcacao m : listaOriginal) {
            if (m.getAnimalNome() != null) {
                nomesAnimais.add(m.getAnimalNome());
            }
        }
        ArrayAdapter<String> adapterAnimais = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(nomesAnimais));
        autoCompleteAnimal.setAdapter(adapterAnimais);

        // Se o animal selecionado anteriormente não existir mais na lista, volta para "Todos"
        if (!nomesAnimais.contains(animalSelecionado)) {
            animalSelecionado = "Todos";
        }

        // Define o texto visível no campo para garantir que "Todos" (ou o atual) apareça selecionado
        autoCompleteAnimal.setText(animalSelecionado, false);
    }

    private void configurarFiltrosEstado() {
        View.OnClickListener statusClickListener = v -> {
            // Lógica de Toggle: Se clicar no botão já selecionado, volta para "Todas"
            if (v.getId() == R.id.btnFiltrarPendente) {
                if (estadoSelecionado.equals("pendente")) {
                    estadoSelecionado = "Todas";
                    atualizarBotoesUI(null);
                } else {
                    estadoSelecionado = "pendente";
                    atualizarBotoesUI(btnFilterPendente);
                }
            } else if (v.getId() == R.id.btnFiltrarCancelada) {
                if (estadoSelecionado.equals("cancelada")) {
                    estadoSelecionado = "Todas";
                    atualizarBotoesUI(null);
                } else {
                    estadoSelecionado = "cancelada";
                    atualizarBotoesUI(btnFilterCancelada);
                }
            } else if (v.getId() == R.id.btnFiltrarRealizada) {
                if (estadoSelecionado.equals("realizada")) {
                    estadoSelecionado = "Todas";
                    atualizarBotoesUI(null);
                } else {
                    estadoSelecionado = "realizada";
                    atualizarBotoesUI(btnFilterRealizada);
                }
            }
            aplicarFiltros();
        };

        btnFilterPendente.setOnClickListener(statusClickListener);
        btnFilterCancelada.setOnClickListener(statusClickListener);
        btnFilterRealizada.setOnClickListener(statusClickListener);
    }

    private void aplicarFiltros() {
        ArrayList<Marcacao> listaFiltrada = new ArrayList<>();

        for (Marcacao m : listaOriginal) {
            boolean bateAnimal = animalSelecionado.equals("Todos") ||
                    (m.getAnimalNome() != null && m.getAnimalNome().equalsIgnoreCase(animalSelecionado));

            boolean bateEstado = estadoSelecionado.equals("Todas") ||
                    (m.getEstado() != null && m.getEstado().equalsIgnoreCase(estadoSelecionado));

            if (bateAnimal && bateEstado) {
                listaFiltrada.add(m);
            }
        }

        adapter.updateList(listaFiltrada);
        verificarEstadoVazio(listaFiltrada.size());
    }

    private void verificarEstadoVazio(int tamanhoLista) {
        if (tamanhoLista > 0) {
            rvMarcacoes.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            rvMarcacoes.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        }
    }

    private void atualizarBotoesUI(Button botaoSelecionado) {
        Button[] botoes = {btnFilterPendente, btnFilterCancelada, btnFilterRealizada};

        int corVerde = Color.parseColor("#2E7D32");
        int corBranca = Color.WHITE;

        for (Button btn : botoes) {
            if (btn == botaoSelecionado) {
                // Botão Ativo: Fundo Verde, Texto Branco
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(corVerde));
                btn.setTextColor(corBranca);
            } else {
                // Botão Inativo: Fundo Branco, Texto Verde
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(corBranca));
                btn.setTextColor(corVerde);
            }
        }
    }

    @Override
    public void onRefreshListaMarcacoes(ArrayList<Marcacao> listaMarcacoes) {
        // Atualização vinda do Singleton (ex: via socket ou outra trigger)
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                this.listaOriginal = listaMarcacoes;
                atualizarDropdownAnimais();
                aplicarFiltros();
            });
        }
    }

    @Override
    public void onApiStateChanged(boolean available) {
        // Se a API ficar disponível e a lista estiver vazia ou desatualizada, podemos recarregar
        if (available && isAdded()) {
            requireActivity().runOnUiThread(this::loadMarcacoes);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Singleton.getInstance(requireContext()).removeApiStateChangeListener(this);

        if (networkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                cm.unregisterNetworkCallback(networkCallback);
            }
        }
    }
}
