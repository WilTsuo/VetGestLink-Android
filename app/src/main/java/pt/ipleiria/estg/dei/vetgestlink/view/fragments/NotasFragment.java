// java
package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Animal;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.NotasAdapter;

public class NotasFragment extends Fragment {

    private MaterialAutoCompleteTextView autoCompleteAnimal;
    private TextView tvAnimalSub;
    private RecyclerView recyclerNotas;
    private NotasAdapter notasAdapter;
    private List<Animal> animais = new ArrayList<>();
    private List<Nota> notas = new ArrayList<>();

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public NotasFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notas, container, false);

        autoCompleteAnimal = root.findViewById(R.id.autoCompleteAnimal);
        tvAnimalSub = root.findViewById(R.id.tvAnimalSub);
        recyclerNotas = root.findViewById(R.id.recyclerNotas);

        recyclerNotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        notasAdapter = new NotasAdapter(notas);
        recyclerNotas.setAdapter(notasAdapter);

        autoCompleteAnimal.setOnItemClickListener((parent, view, position, id) -> {
            // obter o nome seleccionado do adapter e procurar o Animal correspondente na lista 'animais'
            String selectedName = (String) parent.getItemAtPosition(position);
            Animal selected = null;
            for (Animal a : animais) {
                if (a.getNome() != null && a.getNome().equals(selectedName)) {
                    selected = a;
                    break;
                }
            }
            if (selected != null) {
                tvAnimalSub.setText(selected.getNome());
                loadNotasForAnimal(getAccessToken(), selected.getId());
            }
        });

        loadAnimalsAndNotas();

        return root;
    }

    private String getAccessToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    private void loadAnimalsAndNotas() {
        String token = getAccessToken();
        if (token == null) {
            animais.clear();
            notas.clear();
            notasAdapter.updateList(notas);
            ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
            autoCompleteAnimal.setAdapter(emptyAdapter);
            autoCompleteAnimal.setText("", false);
            tvAnimalSub.setText("");
            return;
        }

        // usar o endpoint que devolve apenas nomes e ids
        Singleton.getInstance(requireContext()).getNomesAnimais(token, new Singleton.AnimaisCallback() {
            @Override
            public void onSuccess(List<Animal> animaisList) {
                requireActivity().runOnUiThread(() -> {
                    animais.clear();
                    animais.addAll(animaisList);

                    List<String> nomes = new ArrayList<>();
                    for (Animal a : animais) nomes.add(a.getNome());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, nomes);
                    autoCompleteAnimal.setAdapter(adapter);

                    if (!animais.isEmpty()) {
                        autoCompleteAnimal.setText(nomes.get(0), false);
                        tvAnimalSub.setText(animais.get(0).getNome());
                        loadNotasForAnimal(token, animais.get(0).getId());
                    } else {
                        loadNotasForAnimal(token, null);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    animais.clear();
                    notas.clear();
                    notasAdapter.updateList(notas);
                    ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
                    autoCompleteAnimal.setAdapter(emptyAdapter);
                    autoCompleteAnimal.setText("", false);
                    tvAnimalSub.setText("");
                });
            }
        });
    }

    private void loadNotasForAnimal(String token, Integer animalId) {
        if (token == null) return;
        Singleton.getInstance(requireContext()).getNotas(token, animalId, new Singleton.NotasCallback() {
            @Override
            public void onSuccess(List<Nota> resultNotas) {
                requireActivity().runOnUiThread(() -> {
                    notas.clear();
                    notas.addAll(resultNotas);
                    notasAdapter.updateList(notas);
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    notas.clear();
                    notasAdapter.updateList(notas);
                });
            }
        });
    }
}
