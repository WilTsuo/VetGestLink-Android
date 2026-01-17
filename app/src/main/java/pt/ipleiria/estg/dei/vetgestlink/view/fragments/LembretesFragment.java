package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Lembrete;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.LembreteAdapter;

public class LembretesFragment extends Fragment implements Singleton.ApiStateChangeListener {

    private RecyclerView rvLembretes;
    private LembreteAdapter adapter;
    private List<Lembrete> listaLembretes;
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private FloatingActionButton fabAdd;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lembretes, container, false);

        rvLembretes = view.findViewById(R.id.rvLembretes);
        rvLembretes.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAdd = view.findViewById(R.id.fab_AdicionarLembretes);

        listaLembretes = new ArrayList<>();

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("userprofile_id", 0);

        adapter = new LembreteAdapter(listaLembretes, currentUserId);
        adapter.setOnLembreteChangedListener(this::carregarLembretes);
        rvLembretes.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            if (!Singleton.getInstance(requireContext()).getApiAvailable()) {
                Toast.makeText(requireContext(), "API indisponivel, verifica a conexao", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarDialogoCriar();
        });

        // regista listener no Singleton
        Singleton.getInstance(requireContext()).addApiStateChangeListener(this);

        // setup network monitoring
        setupNetworkCallback();

        // le o estado da api atual
        boolean apiOk = Singleton.getInstance(requireContext()).getApiAvailable();
        applyApiState(apiOk);

        carregarLembretes();

        return view;
    }

    private void carregarLembretes() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", "");

        Singleton.getInstance(requireContext()).getLembretes(token, new Singleton.LembretesCallback() {
            @Override
            public void onSuccess(List<Lembrete> lembretes) {
                listaLembretes.clear();
                listaLembretes.addAll(lembretes);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("Vetgetlink-LembretesService", error);
            }
        });
    }

    private void mostrarDialogoCriar() {
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_nota, null);

        TextInputEditText etDescricao = dialogView.findViewById(R.id.et_descricao);
        etDescricao.setText("");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Novo Lembrete")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Criar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String descricao = etDescricao.getText() != null ? etDescricao.getText().toString().trim() : "";
            if (descricao.isEmpty()) {
                etDescricao.setError("Campo obrigatório");
                return;
            }

            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String token = prefs.getString("access_token", "");

            Singleton.getInstance(requireContext()).criarLembrete(token, descricao, new Singleton.MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getContext(), "Lembrete criado", Toast.LENGTH_SHORT).show();
                    carregarLembretes();
                    dialog.dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // aplica o estado da api no FAB
    private void applyApiState(boolean ok) {
        if (adapter != null) adapter.setApiAvailable(ok);
        if (fabAdd != null) {
            fabAdd.setEnabled(ok);
            fabAdd.setAlpha(ok ? 1f : 0.5f);
        }
    }

    private void setupNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // qnd a rede fica disponivel, checa a API rapidamente
                Singleton.getInstance(requireContext()).quickCheckApiState(requireContext(), responding -> {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> applyApiState(responding));
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                // perde a rede, desabilita
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> applyApiState(false));
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
        // callback do singleton quando o estado da API muda
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> applyApiState(available));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // remove listener do Singleton
        Singleton.getInstance(requireContext()).removeApiStateChangeListener(this);

        // remove o NetworkCallback
        if (networkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                cm.unregisterNetworkCallback(networkCallback);
            }
        }
    }
}
