package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.activities.MainActivity;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.PerfilAnimalAdapter;

public class PerfilFragment extends Fragment {

    private RecyclerView recyclerAnimais;
    private PerfilAnimalAdapter perfilAdapter;
    private List<Animal> perfilAnimais = new ArrayList<>();
    private TextView tvAnimalCount;

    // TextViews do utilizador (nome, email, telemóvel, morada)
    private TextView tvNomeCompleto;
    private TextView tvEmail;
    private TextView tvTelefone;
    private TextView tvMorada;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public PerfilFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar RecyclerView, LayoutManager e Adapter
        recyclerAnimais = view.findViewById(R.id.rvAnimais);
        recyclerAnimais.setLayoutManager(new LinearLayoutManager(requireContext()));
        perfilAdapter = new PerfilAnimalAdapter(perfilAnimais);
        recyclerAnimais.setAdapter(perfilAdapter);

        // TextView para contar animais
        tvAnimalCount = view.findViewById(R.id.tvAnimalCount);
        updateAnimalCount();

        // Inicializar TextViews do utilizador (certificar-se que ids existem no layout)
        tvNomeCompleto = view.findViewById(R.id.tvNomeCompleto);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTelefone = view.findViewById(R.id.tvTelefone);
        tvMorada = view.findViewById(R.id.tvMorada);

        // Botão ver lembretes
        Button btnVerLembretes = view.findViewById(R.id.btnVerLembretes);
        btnVerLembretes.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).navegarParaLembretes();
        });

        // Carregar dados do perfil/animais
        carregarDados();

        return view;
    }

    private String getAccessToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    private void carregarDados() {
        String token = getAccessToken();
        if (token == null) {
            // sem token: limpa lista e UI do utilizador
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                perfilAnimais.clear();
                if (perfilAdapter != null) perfilAdapter.notifyDataSetChanged();
                updateAnimalCount();
                clearUserInfo();
            });
            return;
        }

        // Carrega animais (assíncrono)
        Singleton.getInstance(requireContext()).getAnimais(token, new Singleton.AnimaisCallback() {
            @Override
            public void onSuccess(List<Animal> animaisList) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    perfilAnimais.clear();
                    if (animaisList != null && !animaisList.isEmpty()) {
                        perfilAnimais.addAll(animaisList);
                    }
                    if (perfilAdapter == null) {
                        perfilAdapter = new PerfilAnimalAdapter(perfilAnimais);
                        recyclerAnimais.setAdapter(perfilAdapter);
                    } else {
                        perfilAdapter.notifyDataSetChanged();
                    }
                    updateAnimalCount();
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    perfilAnimais.clear();
                    if (perfilAdapter != null) perfilAdapter.notifyDataSetChanged();
                    updateAnimalCount();
                });
            }
        });

        // Carrega o perfil do utilizador (independente dos animais)
        loadUserProfile(token);
    }

    private void loadUserProfile(@Nullable String token) {
        if (tvNomeCompleto == null || tvEmail == null || tvTelefone == null || tvMorada == null) return;

        if (token == null) {
            clearUserInfo();
            return;
        }

        Singleton.getInstance(requireContext()).getProfile(token, new Singleton.ProfileCallback() {
            @Override
            public void onSuccess(String nome, String email, String telefone, String moradaCompleta) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    tvNomeCompleto.setText(nome != null ? nome : "");
                    tvEmail.setText(email != null ? email : "");
                    tvTelefone.setText(telefone != null ? telefone : "");
                    tvMorada.setText(moradaCompleta != null ? moradaCompleta : "");
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    // Em erro, limpar ou manter o que houver — aqui limpa para consistência
                    clearUserInfo();
                });
            }
        });
    }

    private void clearUserInfo() {
        if (tvNomeCompleto != null) tvNomeCompleto.setText("");
        if (tvEmail != null) tvEmail.setText("");
        if (tvTelefone != null) tvTelefone.setText("");
        if (tvMorada != null) tvMorada.setText("");
    }

    private void updateAnimalCount() {
        if (tvAnimalCount == null) return;
        int count = perfilAnimais != null ? perfilAnimais.size() : 0;
        tvAnimalCount.setText(String.valueOf(count) + " animais");
    }
}
