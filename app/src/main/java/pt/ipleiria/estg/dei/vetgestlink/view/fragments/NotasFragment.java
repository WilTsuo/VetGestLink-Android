package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Animal;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.NotasAdapter;

public class NotasFragment extends Fragment {

    private MaterialAutoCompleteTextView autoCompleteAnimal;
    private RecyclerView recyclerNotas;
    private int currentUserId;
    private NotasAdapter notasAdapter;
    private List<Animal> animais = new ArrayList<>();
    private List<Nota> notas = new ArrayList<>();
    private MaterialButton btnAddNota;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public NotasFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notas, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("userprofile_id", -1);

        autoCompleteAnimal = root.findViewById(R.id.autoCompleteAnimal);
        recyclerNotas = root.findViewById(R.id.recyclerNotas);
        btnAddNota = root.findViewById(R.id.btnAddNota);

        recyclerNotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        notasAdapter = new NotasAdapter(notas, currentUserId);
        recyclerNotas.setAdapter(notasAdapter);

        notasAdapter.setOnNotaChangedListener(() -> {
            String selectedName = autoCompleteAnimal.getText() != null ? autoCompleteAnimal.getText().toString() : "";
            Animal selected = null;
            for (Animal a : animais) {
                if (a.getNome() != null && a.getNome().equals(selectedName)) {
                    selected = a;
                    break;
                }
            }
            if (selected != null) {
                loadNotasForAnimal(getAccessToken(), selected.getId());
            }
        });

        btnAddNota.setOnClickListener(v -> showAddNotaDialog());

        autoCompleteAnimal.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            Animal selected = null;
            for (Animal a : animais) {
                if (a.getNome() != null && a.getNome().equals(selectedName)) {
                    selected = a;
                    break;
                }
            }
            if (selected != null) {
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
            return;
        }

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

    private void showAddNotaDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_nota, null);

        TextInputEditText etDescricao = dialogView.findViewById(R.id.et_descricao);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setNegativeButton(getString(R.string.cancel), (d, which) -> {})
                .setPositiveButton(getString(R.string.save), null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setOnClickListener(v -> {
            String descricao = etDescricao.getText() != null ? etDescricao.getText().toString().trim() : "";

            if (descricao.isEmpty()) {
                etDescricao.setError(getString(R.string.campo_obrigatorio));
                return;
            }

            etDescricao.setError(null);

            // Obter o animal selecionado
            String selectedName = autoCompleteAnimal.getText() != null ? autoCompleteAnimal.getText().toString() : "";
            Animal selectedAnimal = null;
            for (Animal a : animais) {
                if (a.getNome() != null && a.getNome().equals(selectedName)) {
                    selectedAnimal = a;
                    break;
                }
            }

            if (selectedAnimal == null) {
                Toast.makeText(requireContext(), "Selecione um animal", Toast.LENGTH_SHORT).show();
                return;
            }

            // Integração com API
            String token = getAccessToken();
            final int animalId = selectedAnimal.getId();

            Singleton.getInstance(requireContext()).criarNota(token, animalId, descricao, new Singleton.MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), getString(R.string.nota_guardada), Toast.LENGTH_SHORT).show();
                        loadNotasForAnimal(token, animalId); // Recarrega as notas do animal
                        dialog.dismiss();
                    });
                }

                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Erro: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }
}