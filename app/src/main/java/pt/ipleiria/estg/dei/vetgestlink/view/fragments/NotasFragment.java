package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
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

public class NotasFragment extends Fragment implements Singleton.ApiStateChangeListener {

    private MaterialAutoCompleteTextView autoCompleteAnimal;
    private RecyclerView recyclerNotas;
    private int currentUserId;
    private NotasAdapter notasAdapter;
    private List<Animal> animais = new ArrayList<>();
    private List<Nota> notas = new ArrayList<>();
    private MaterialButton btnAddNota;
    private ConnectivityManager.NetworkCallback networkCallback;

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
        btnAddNota = root.findViewById(R.id.btnAdicionarNota);

        recyclerNotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        notasAdapter = new NotasAdapter(notas, currentUserId);
        recyclerNotas.setAdapter(notasAdapter);

        // Registrar listener no Singleton para mudanças de estado da API
        Singleton.getInstance(requireContext()).addApiStateChangeListener(this);

        // Setup NetworkCallback para reagir instantaneamente a mudanças de rede
        setupNetworkCallback();

        // Ler estado atual da API (não faz chamada HTTP, apenas lê SharedPreferences)
        boolean apiOk = Singleton.getInstance(requireContext()).getApiAvailable();
        applyApiState(apiOk);

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
                    // CORREÇÃO: Adicionado 'null' como segundo argumento (animalId) para buscar todas
                    Singleton.getInstance(requireContext()).getNotas(token, null, new Singleton.NotasCallback() {
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

        // Busca da API para obter dados atualizados
        // CORREÇÃO: Substituído getTodasNotas por getNotas e passado null no ID para buscar todas
        Singleton.getInstance(requireContext()).getNotas(token, null, new Singleton.NotasCallback() {
            @Override
            public void onSuccess(List<Nota> todasNotas) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    notas.clear();
                    // Filtra manualmente conforme a lógica original
                    for (Nota n : todasNotas) {
                        if (n.getAnimalNome() != null && n.getAnimalNome().equals(finalAnimalName)) {
                            notas.add(n);
                        }
                    }
                    notasAdapter.updateList(notas);
                });
            }

            @Override
            public void onError(String error) {
                // Se falhar, carrega do cache
                carregarNotasCache(finalAnimalName);
            }
        });
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
                        dialog.dismiss();
                        loadNotasForAnimal(token, animalId); // Recarrega as notas do animal
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

    private void setupNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // Quando a rede ficar disponível, verificar a API rapidamente
                Singleton.getInstance(requireContext()).quickCheckApiState(requireContext(), responding -> {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> applyApiState(responding));
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                // Quando perder a rede, desabilitar imediatamente
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        applyApiState(false);
                        // Carregar do cache
                        String selectedName = autoCompleteAnimal.getText() != null ? autoCompleteAnimal.getText().toString() : "";
                        if (!selectedName.isEmpty() && !animais.isEmpty()) {
                            for (Animal a : animais) {
                                if (a.getNome() != null && a.getNome().equals(selectedName)) {
                                    carregarNotasCache(a.getNome());
                                    break;
                                }
                            }
                        }
                    });
                }
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        cm.registerNetworkCallback(networkRequest, networkCallback);
    }

    @Override
    public void onApiStateChanged(boolean available) {
        // Callback do Singleton quando o estado da API muda
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> applyApiState(available));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remover listener do Singleton
        Singleton.getInstance(requireContext()).removeApiStateChangeListener(this);

        // Remover NetworkCallback
        if (networkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                cm.unregisterNetworkCallback(networkCallback);
            }
        }
    }
}
