package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.models.Nota;
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
        currentUserId = prefs.getInt("user_id", -1);

        autoCompleteAnimal = root.findViewById(R.id.autoCompleteAnimal);
        recyclerNotas = root.findViewById(R.id.recyclerNotas);
        btnAddNota = root.findViewById(R.id.btnAddNota);

        recyclerNotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        notasAdapter = new NotasAdapter(notas, currentUserId);
        recyclerNotas.setAdapter(notasAdapter);

        updateApiState();

        // verificar disponibilidade de rede/API e aplicar estado
        boolean apiOk = Singleton.getInstance(requireContext()).getApiAvailable();
        notasAdapter.setApiAvailable(apiOk);

        // aplica estado ao botão do fragment
        btnAddNota.setEnabled(apiOk);
        btnAddNota.setAlpha(apiOk ? 1f : 0.5f);
        btnAddNota.setOnClickListener(v -> {
            if (!Singleton.getInstance(requireContext()).getApiAvailable()) {
                Toast.makeText(requireContext(), "Houve um problema na Ligação a API, verifique a sua conexao a Internet", Toast.LENGTH_SHORT).show();
                return;
            }
            showAddNotaDialog();
        });

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
        loadAnimaisAndNotas();
        return root;
    }

    private String getAccessToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }


    private void loadAnimaisAndNotas() {
        String token = getAccessToken();
        if (token == null) {
            carregarCache();
            return;
        }
        Singleton.getInstance(requireContext()).getNomesAnimais(token, new Singleton.AnimaisCallback() {
            @Override
            public void onSuccess(List<Animal> animaisList) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    animais.clear();
                    animais.addAll(animaisList);
                    atualizarDropdown();

                    // CARREGA TODAS AS NOTAS
                    Singleton.getInstance(requireContext()).getTodasNotas(token, new Singleton.NotasCallback() {
                        @Override
                        public void onSuccess(List<Nota> todasNotas) {
                            // Filtra pelo primeiro animal
                            if (!animais.isEmpty()) {
                                String animalName = animais.get(0).getNome();
                                notas.clear();
                                for (Nota n : todasNotas) {
                                    if (n.getAnimalNome() != null && n.getAnimalNome().equals(animalName)) {
                                        notas.add(n);
                                    }
                                }
                                notasAdapter.updateList(notas);
                            }
                        }
                        @Override
                        public void onError(String error) {
                            carregarCache();
                        }
                    });
                });
            }
            @Override
            public void onError(String error) {
                carregarCache();
            }
        });
    }
    private void carregarCache() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            ArrayList<Animal> cachedAnimais = Singleton.getInstance(requireContext()).getAnimaisBD();
            animais.clear();
            if (cachedAnimais != null) animais.addAll(cachedAnimais);
            atualizarDropdown();

            // Filtra notas pelo primeiro animal
            if (!animais.isEmpty()) {
                String animalName = animais.get(0).getNome();
                ArrayList<Nota> cachedNotas = Singleton.getInstance(requireContext()).getNotasBD();
                notas.clear();

                if (cachedNotas != null) {
                    for (Nota n : cachedNotas) {
                        if (n.getAnimalNome() != null && n.getAnimalNome().equals(animalName)) {
                            notas.add(n);
                        }
                    }
                }
                notasAdapter.updateList(notas);
            }

            Log.d("Vetgestlin-NotasFragment", "Carregou dados do cache");
        });
    }
    private void atualizarDropdown() {
        List<String> nomes = new ArrayList<>();
        for (Animal a : animais) nomes.add(a.getNome());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, nomes);
        autoCompleteAnimal.setAdapter(adapter);
        autoCompleteAnimal.setThreshold(0);

        if (!nomes.isEmpty()) {
            autoCompleteAnimal.setText(nomes.get(0), false);
        }
    }

    private void loadNotasForAnimal(String token, Integer animalId) {
        String animalName = null;
        for (Animal a : animais) {
            if (a.getId() == animalId) {
                animalName = a.getNome();
                break;
            }
        }
        final String finalAnimalName = animalName;

        if (token == null || finalAnimalName == null) {
            carregarNotasCache(finalAnimalName);
            return;
        }
        ArrayList<Nota> cached = Singleton.getInstance(requireContext())
                .getNotasByAnimalNome(finalAnimalName);

        notas.clear();
        if (cached != null) notas.addAll(cached);
        notasAdapter.updateList(notas);
    }

    private void carregarNotasCache(String animalName) {
        if (!isAdded() || animalName == null) return;
        requireActivity().runOnUiThread(() -> {
            ArrayList<Nota> cached = Singleton.getInstance(requireContext())
                    .getNotasByAnimalNome(animalName);
            notas.clear();
            if (cached != null) notas.addAll(cached);
            notasAdapter.updateList(notas);
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
            if (!Singleton.getInstance(requireContext()).getApiAvailable()) {
                Toast.makeText(requireContext(), "Houve um problema na Ligação a API, verifique a sua conexao a Internet", Toast.LENGTH_SHORT).show();
                return;
            }

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

    private void applyApiState(boolean ok) {
        if (notasAdapter != null) notasAdapter.setApiAvailable(ok);
        if (btnAddNota != null) {
            btnAddNota.setEnabled(ok);
            btnAddNota.setAlpha(ok ? 1f : 0.5f);
        }
    }

    private void updateApiState() {
        Singleton.getInstance(requireContext()).updateApiState(requireContext(), responding -> {
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> applyApiState(responding));
        });
    }
}