package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

public class MarcacoesFragment extends Fragment implements MarcacoesListener {

    private RecyclerView rvMarcacoes;
    private MarcacoesAdapter adapter;
    private Button btnFilterTodas, btnFilterPendente, btnFilterCancelada, btnFilterRealizada;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private MaterialAutoCompleteTextView autoCompleteAnimal;

    private String currentToken;
    private ArrayList<Marcacao> listaOriginal = new ArrayList<>();
    private String animalSelecionado = "Todos";
    private String estadoSelecionado = "Todas";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marcacoes, container, false);

        rvMarcacoes = view.findViewById(R.id.rvMarcacoes);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        autoCompleteAnimal = view.findViewById(R.id.autoCompleteAnimal);

        btnFilterTodas = view.findViewById(R.id.btnFilterTodas);
        btnFilterPendente = view.findViewById(R.id.btnFilterPendente);
        btnFilterCancelada = view.findViewById(R.id.btnFilterCancelada);
        btnFilterRealizada = view.findViewById(R.id.btnFilterRealizada);

        rvMarcacoes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MarcacoesAdapter(getContext(), new ArrayList<>());
        rvMarcacoes.setAdapter(adapter);

        SharedPreferences prefs = getContext().getSharedPreferences("VetGestLinkPrefs", Context.MODE_PRIVATE);
        currentToken = prefs.getString("access_token", "");

        configurarFiltrosEstado();
        configurarFiltroAnimal();

        Singleton.getInstance(getContext()).setMarcacoesListener(this);

        return view;
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

        // Resetar seleção se o animal anterior não existir na nova lista
        if (!nomesAnimais.contains(animalSelecionado)) {
            animalSelecionado = "Todos";
            autoCompleteAnimal.setText("Todos", false);
        }
    }

    private void configurarFiltrosEstado() {
        View.OnClickListener statusClickListener = v -> {
            if (v.getId() == R.id.btnFilterTodas) {
                estadoSelecionado = "Todas";
                atualizarBotoesUI(btnFilterTodas);
            } else if (v.getId() == R.id.btnFilterPendente) {
                estadoSelecionado = "pendente";
                atualizarBotoesUI(btnFilterPendente);
            } else if (v.getId() == R.id.btnFilterCancelada) {
                estadoSelecionado = "cancelada";
                atualizarBotoesUI(btnFilterCancelada);
            } else if (v.getId() == R.id.btnFilterRealizada) {
                estadoSelecionado = "realizada";
                atualizarBotoesUI(btnFilterRealizada);
            }
            aplicarFiltros();
        };

        btnFilterTodas.setOnClickListener(statusClickListener);
        btnFilterPendente.setOnClickListener(statusClickListener);
        btnFilterCancelada.setOnClickListener(statusClickListener);
        btnFilterRealizada.setOnClickListener(statusClickListener);
    }

    private void carregarDadosAPI() {
        if (currentToken.isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);

        Singleton.MarcacoesCallback callback = new Singleton.MarcacoesCallback() {
            @Override
            public void onSuccess(ArrayList<Marcacao> marcacoes) {
                progressBar.setVisibility(View.GONE);
                listaOriginal = marcacoes;
                atualizarDropdownAnimais();
                aplicarFiltros();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro ao carregar: " + error, Toast.LENGTH_SHORT).show();
            }
        };

        Singleton.getInstance(getContext()).getMarcacoesAPI(currentToken, callback);
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
        Button[] botoes = {btnFilterTodas, btnFilterPendente, btnFilterCancelada, btnFilterRealizada};
        for (Button btn : botoes) {
            btn.setBackgroundResource(R.drawable.filter_chip_unselected);
            btn.setTextColor(Color.parseColor("#364153"));
        }
        botaoSelecionado.setBackgroundResource(R.drawable.filter_chip_selected);
        botaoSelecionado.setTextColor(Color.WHITE);
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarDadosAPI();
    }

    @Override
    public void onRefreshListaMarcacoes(ArrayList<Marcacao> listaMarcacoes) {
        this.listaOriginal = listaMarcacoes;
        atualizarDropdownAnimais();
        aplicarFiltros();
    }
}