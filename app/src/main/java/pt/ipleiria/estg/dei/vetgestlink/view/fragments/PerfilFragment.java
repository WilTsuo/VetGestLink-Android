// java
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.PerfilAnimalAdapter;

public class PerfilFragment extends Fragment {

    private RecyclerView recyclerAnimais;
    private PerfilAnimalAdapter perfilAdapter;
    private List<Animal> perfilAnimais = new ArrayList<>();
    private TextView tvAnimalCount;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public PerfilFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar RecyclerView, LayoutManager e Adapter
        recyclerAnimais = root.findViewById(R.id.rvAnimais);
        recyclerAnimais.setLayoutManager(new LinearLayoutManager(requireContext()));
        perfilAdapter = new PerfilAnimalAdapter(perfilAnimais);
        recyclerAnimais.setAdapter(perfilAdapter);

        // TextView para contar animais
        tvAnimalCount = root.findViewById(R.id.tvAnimalCount);
        updateAnimalCount();

        // Carregar dados do perfil/animais
        carregarDados();

        return root;
    }

    private String getAccessToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    private void carregarDados() {
        String token = getAccessToken();
        if (token == null) {
            // sem token: limpa lista (poderá usar cache se implementado)
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                perfilAnimais.clear();
                if (perfilAdapter != null) perfilAdapter.notifyDataSetChanged();
                updateAnimalCount();
            });
            return;
        }

        // Pede lista de animais ao Singleton
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
                    // Em caso de erro mantém lista vazia / mostra mensagem se desejar
                    perfilAnimais.clear();
                    if (perfilAdapter != null) perfilAdapter.notifyDataSetChanged();
                    updateAnimalCount();
                });
            }
        });
    }

    private void updateAnimalCount() {
        if (tvAnimalCount == null) return;
        int count = perfilAnimais != null ? perfilAnimais.size() : 0;
        tvAnimalCount.setText(String.valueOf(count) + " animais");
    }
}
