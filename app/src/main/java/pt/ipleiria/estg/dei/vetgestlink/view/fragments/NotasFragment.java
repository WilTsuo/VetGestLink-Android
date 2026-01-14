package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private boolean apiAvailable = true;

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
        boolean apiOk = isThereNetworkAvaliability(requireContext());
        notasAdapter.setApiAvailable(apiOk);

        // aplica estado ao botão do fragment
        btnAddNota.setEnabled(apiOk);
        btnAddNota.setAlpha(apiOk ? 1f : 0.5f);
        btnAddNota.setOnClickListener(v -> {
            if (!apiAvailable) {
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
            // Sem token: tenta usar cache local de notas em vez de limpar tudo
            ArrayList<Nota> cached = Singleton.getInstance(requireContext()).getNotasBD();
            if (cached != null && !cached.isEmpty()) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    // Mantém animais vazios (não há nomes) mas mostra notas em cache
                    animais.clear();
                    ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
                    autoCompleteAnimal.setAdapter(emptyAdapter);
                    autoCompleteAnimal.setText("", false);

                    notas.clear();
                    notas.addAll(cached);
                    notasAdapter.updateList(notas);

                    Toast.makeText(requireContext(), "Offline — a usar notas em cache", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Sem token e sem cache: limpa a UI
                animais.clear();
                notas.clear();
                notasAdapter.updateList(notas);
                ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
                autoCompleteAnimal.setAdapter(emptyAdapter);
                autoCompleteAnimal.setText("", false);
            }
            return;
        }

        // restante implementação inalterada...
        Singleton.getInstance(requireContext()).getNomesAnimais(token, new Singleton.AnimaisCallback() {
            @Override
            public void onSuccess(List<Animal> animaisList) {
                if (!isAdded()) return;
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
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    // Se já temos animais carregados na memória => mantemos e recarregamos notas (cache/API)
                    if (!animais.isEmpty()) {
                        String selectedName = autoCompleteAnimal.getText() != null ? autoCompleteAnimal.getText().toString() : "";
                        Animal selected = null;
                        for (Animal a : animais) {
                            if (a.getNome() != null && a.getNome().equals(selectedName)) {
                                selected = a;
                                break;
                            }
                        }
                        if (selected != null) {
                            loadNotasForAnimal(token, selected.getId());
                        } else {
                            loadNotasForAnimal(token, animais.get(0).getId());
                        }
                        Toast.makeText(requireContext(), "Offline — a usar dados em cache se disponíveis", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Não temos animais carregados: tenta usar notas da BD local
                    ArrayList<Nota> cachedNotas = Singleton.getInstance(requireContext()).getNotasBD();
                    if (cachedNotas != null && !cachedNotas.isEmpty()) {
                        notas.clear();
                        notas.addAll(cachedNotas);
                        notasAdapter.updateList(notas);

                        ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
                        autoCompleteAnimal.setAdapter(emptyAdapter);
                        autoCompleteAnimal.setText("", false);

                        Toast.makeText(requireContext(), "Offline — a usar notas em cache", Toast.LENGTH_SHORT).show();
                    } else {
                        // Sem dados locais: manter UI limpa
                        animais.clear();
                        notas.clear();
                        notasAdapter.updateList(notas);
                        ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
                        autoCompleteAnimal.setAdapter(emptyAdapter);
                        autoCompleteAnimal.setText("", false);
                        Toast.makeText(requireContext(), "Sem ligação e sem dados locais", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadNotasForAnimal(String token, Integer animalId) {
        if (token == null) {
            // sem token não tentamos API; tentamos carregar cache filtrado
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                ArrayList<Nota> cached = Singleton.getInstance(requireContext()).getNotasBD();
                if (cached != null && !cached.isEmpty()) {
                    // tenta filtrar por nome do animal (se conseguirmos obter o nome a partir do id)
                    List<Nota> filtered = new ArrayList<>();
                    String animalName = null;
                    if (animalId != null) {
                        for (Animal a : animais) {
                            if (a.getId() == animalId) {
                                animalName = a.getNome();
                                break;
                            }
                        }
                    }
                    if (animalName != null) {
                        for (Nota n : cached) {
                            if (n.getAnimalNome() != null && n.getAnimalNome().equals(animalName)) {
                                filtered.add(n);
                            }
                        }
                    } else {
                        filtered.addAll(cached);
                    }

                    notas.clear();
                    notas.addAll(filtered);
                    notasAdapter.updateList(notas);
                } else {
                    notas.clear();
                    notasAdapter.updateList(notas);
                }
            });
            return;
        }

        Singleton.getInstance(requireContext()).getNotas(token, animalId, new Singleton.NotasCallback() {
            @Override
            public void onSuccess(List<Nota> resultNotas) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    notas.clear();
                    notas.addAll(resultNotas);
                    notasAdapter.updateList(notas);
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    // tenta usar notas em cache e filtrar pelo animal se possível
                    ArrayList<Nota> cached = Singleton.getInstance(requireContext()).getNotasBD();
                    List<Nota> filtered = new ArrayList<>();

                    String animalName = null;
                    if (animalId != null) {
                        for (Animal a : animais) {
                            if (a.getId() == animalId) {
                                animalName = a.getNome();
                                break;
                            }
                        }
                    }

                    if (cached != null && !cached.isEmpty()) {
                        if (animalName != null) {
                            for (Nota n : cached) {
                                if (n.getAnimalNome() != null && n.getAnimalNome().equals(animalName)) {
                                    filtered.add(n);
                                }
                            }
                        } else {
                            filtered.addAll(cached);
                        }
                    }

                    if (!filtered.isEmpty()) {
                        notas.clear();
                        notas.addAll(filtered);
                        notasAdapter.updateList(notas);
                        Toast.makeText(requireContext(), "Offline — a usar notas em cache", Toast.LENGTH_SHORT).show();
                    } else {
                        notas.clear();
                        notasAdapter.updateList(notas);
                        Toast.makeText(requireContext(), "Erro ao carregar notas e sem cache disponível", Toast.LENGTH_SHORT).show();
                    }
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
            if (!apiAvailable) {
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
        apiAvailable = ok;
        if (notasAdapter != null) notasAdapter.setApiAvailable(ok);
        if (btnAddNota != null) {
            btnAddNota.setEnabled(ok);
            btnAddNota.setAlpha(ok ? 1f : 0.5f);
        }
    }

    private void updateApiState() {
        // primeiro verifica se há rede local
        boolean networkOk = isThereNetworkAvaliability(requireContext());
        if (!networkOk) {
            // sem rede => marca API como indisponível
            requireActivity().runOnUiThread(() -> applyApiState(false));
            return;
        }

        // se houver rede, pergunta ao endpoint /health via Singleton (assíncrono)
        Singleton.getInstance(requireContext()).isApiResponding(responding -> {
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> applyApiState(responding));
        });
    }

    private boolean isThereNetworkAvaliability(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }
}