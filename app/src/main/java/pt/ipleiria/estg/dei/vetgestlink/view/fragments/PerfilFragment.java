package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
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
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.activities.MainActivity;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.PerfilAnimalAdapter;

public class PerfilFragment extends Fragment implements Singleton.ApiStateChangeListener {

    private RecyclerView recyclerAnimais;
    private PerfilAnimalAdapter perfilAdapter;
    private List<Animal> perfilAnimais = new ArrayList<>();
    private TextView tvAnimalCount;
    private MaterialButton btnEditarPerfil;
    private ConnectivityManager.NetworkCallback networkCallback;

    // TextViews do utilizador
    private TextView tvNomeCompleto;
    private TextView tvEmail;
    private TextView tvTelefone;

    // TextViews da Morada
    private TextView tvRua;
    private TextView tvNPorta;
    private TextView tvCdPostal;
    private TextView tvLocalidade;

    // Removidas as constantes de SharedPreferences locais, pois agora usamos o Singleton

    public PerfilFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar RecyclerView
        recyclerAnimais = view.findViewById(R.id.rvAnimais);
        recyclerAnimais.setLayoutManager(new LinearLayoutManager(requireContext()));
        perfilAdapter = new PerfilAnimalAdapter(perfilAnimais);
        recyclerAnimais.setAdapter(perfilAdapter);

        // TextView para contar animais
        tvAnimalCount = view.findViewById(R.id.tvAnimalCount);
        updateAnimalCount();

        // Inicializar TextViews do utilizador
        tvNomeCompleto = view.findViewById(R.id.tvNomeCompleto);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTelefone = view.findViewById(R.id.tvTelefone);

        // Inicializar TextViews da Morada
        tvRua = view.findViewById(R.id.tvRua);
        tvNPorta = view.findViewById(R.id.tvNPorta);
        tvCdPostal = view.findViewById(R.id.tvCdPostal);
        tvLocalidade = view.findViewById(R.id.tvLocalidade);

        // Botão ver lembretes
        Button btnVerLembretes = view.findViewById(R.id.btnVerLembretes);
        btnVerLembretes.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navegarParaLembretes();
            }
        });

        // Botão Editar Perfil
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnEditarPerfil.setOnClickListener(v -> {
            if (!Singleton.getInstance(requireContext()).getApiAvailable()){
                Toast.makeText(requireContext(), "sem ligação a API, verifica a tua conexao a Internet", Toast.LENGTH_SHORT).show();
                return;
            }
            abrirDialogEdicao();
        });

        // registrar listener pro Singleton
        Singleton.getInstance(requireContext()).addApiStateChangeListener(this);

        // Setup do NetworkCallback pra reagir a mudancas de rede
        setupNetworkCallback();

        // ler o estado atual da API (nao chama http, so lê as preferencias)
        boolean apiOk = Singleton.getInstance(requireContext()).getApiAvailable();
        applyApiState(apiOk);

        // Carregar dados
        carregarDados();

        return view;
    }

    private void carregarDados() {
        // ALTERAÇÃO: Obtém o token diretamente do Singleton
        String token = Singleton.getInstance(requireContext()).getAccessToken();

        if (token == null) {
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                perfilAnimais.clear();
                if (perfilAdapter != null) perfilAdapter.notifyDataSetChanged();
                updateAnimalCount();
                clearUserInfo();
            });
            return;
        }

        // Carrega animais
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

        // Carrega o perfil
        loadUserProfile(token);
    }

    private void loadUserProfile(@Nullable String token) {
        if (tvNomeCompleto == null || tvEmail == null || tvTelefone == null ) return;

        if (token == null) {
            clearUserInfo();
            return;
        }

        Singleton.getInstance(requireContext()).getPerfil(token, new Singleton.ProfileCallback() {
            @Override
            public void onSuccess(String nome, String email, String telefone, String moradaCompleta) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    tvNomeCompleto.setText(nome != null ? nome : "");
                    tvEmail.setText(email != null ? email : "");
                    tvTelefone.setText(telefone != null ? telefone : "");

                    distribuirMorada(moradaCompleta);
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    clearUserInfo();
                });
            }
        });
    }

    private void distribuirMorada(String moradaCompleta) {
        if (tvRua == null) return;

        if (moradaCompleta != null && !moradaCompleta.isEmpty()) {
            Pattern patternCP = Pattern.compile("(\\d{4}-\\d{3})");
            Matcher matcher = patternCP.matcher(moradaCompleta);

            if (matcher.find()) {
                String cp = matcher.group(1);
                tvCdPostal.setText(cp);
                String depoisCP = moradaCompleta.substring(matcher.end()).trim();
                tvLocalidade.setText(depoisCP);
                String antesCP = moradaCompleta.substring(0, matcher.start()).trim();
                int lastCommaIndex = antesCP.lastIndexOf(",");
                if (lastCommaIndex != -1) {
                    tvRua.setText(antesCP.substring(0, lastCommaIndex).trim());
                    tvNPorta.setText(antesCP.substring(lastCommaIndex + 1).trim());
                } else {
                    tvRua.setText(antesCP);
                    tvNPorta.setText("");
                }
            } else {
                tvRua.setText(moradaCompleta);
                tvNPorta.setText("");
                tvCdPostal.setText("");
                tvLocalidade.setText("");
            }
        } else {
            tvRua.setText("Sem morada");
            tvNPorta.setText("-");
            tvCdPostal.setText("-");
            tvLocalidade.setText("-");
        }
    }

    private void clearUserInfo() {
        if (tvNomeCompleto != null) tvNomeCompleto.setText("");
        if (tvEmail != null) tvEmail.setText("");
        if (tvTelefone != null) tvTelefone.setText("");
        if (tvRua != null) tvRua.setText("");
        if (tvNPorta != null) tvNPorta.setText("");
        if (tvCdPostal != null) tvCdPostal.setText("");
        if (tvLocalidade != null) tvLocalidade.setText("");
    }

    private void updateAnimalCount() {
        if (tvAnimalCount == null) return;
        int count = perfilAnimais != null ? perfilAnimais.size() : 0;
        tvAnimalCount.setText(String.valueOf(count) + " animais");
    }

    // --- Lógica de Edição ---

    private void abrirDialogEdicao() {
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Referências do Dialog
        final TextInputEditText etNome = dialogView.findViewById(R.id.etNomeCompleto);
        final TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        final TextInputEditText etTelefone = dialogView.findViewById(R.id.etTelefone);
        final TextInputEditText etRua = dialogView.findViewById(R.id.etRua);
        final TextInputEditText etPorta = dialogView.findViewById(R.id.etNPorta);
        final TextInputEditText etPostal = dialogView.findViewById(R.id.etCdPostal);
        final TextInputEditText etLocalidade = dialogView.findViewById(R.id.etLocalidade);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardar);

        // Preencher com dados atuais
        etNome.setText(tvNomeCompleto.getText());
        etEmail.setText(tvEmail.getText());
        etTelefone.setText(tvTelefone.getText());
        etRua.setText(tvRua.getText());
        etPorta.setText(tvNPorta.getText());
        etPostal.setText(tvCdPostal.getText());
        etLocalidade.setText(tvLocalidade.getText());

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            // Obtém o token diretamente do Singleton
            String token = Singleton.getInstance(requireContext()).getAccessToken();

            if (token == null) return;

            // Capturar valores
            String novoNome = etNome.getText().toString();
            String novoEmail = etEmail.getText().toString();
            String novoTelefone = etTelefone.getText().toString();
            String novaRua = etRua.getText().toString();
            String novaPorta = etPorta.getText().toString();
            String novoPostal = etPostal.getText().toString();
            String novaLocalidade = etLocalidade.getText().toString();

            // Chamar o Singleton
            Singleton.getInstance(requireContext()).atualizarPerfil(
                    token,
                    novoNome,
                    novoEmail,
                    novoTelefone,
                    novaRua,
                    novaPorta,
                    novoPostal,
                    novaLocalidade,
                    new Singleton.ProfileUpdateCallback() {
                        @Override
                        public void onSuccess(String message) {
                            if (!isAdded()) return;

                            // Atualizar UI do Fragment
                            tvNomeCompleto.setText(novoNome);
                            tvEmail.setText(novoEmail);
                            tvTelefone.setText(novoTelefone);
                            tvRua.setText(novaRua);
                            tvNPorta.setText(novaPorta);
                            tvCdPostal.setText(novoPostal);
                            tvLocalidade.setText(novaLocalidade);

                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String error) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });

        dialog.show();
    }

    // Aplica o estado da api aos botoes
    private void applyApiState(boolean ok) {
        if (btnEditarPerfil != null) {
            btnEditarPerfil.setEnabled(ok);
            btnEditarPerfil.setAlpha(ok ? 1f : 0.5f);
        }
    }

    private void setupNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // qnd rede ficar disponivel, verifica a API rapidamnte
                Singleton.getInstance(requireContext()).quickCheckApiState(requireContext(), responding -> {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> applyApiState(responding));
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                // quando perder rede, desabilita imediatamente
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
        // callback do singleton qnd o estado da api muda
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> applyApiState(available));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // remove listener do Singleton
        Singleton.getInstance(requireContext()).removeApiStateChangeListener(this);

        // remove NetworkCallback
        if (networkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                cm.unregisterNetworkCallback(networkCallback);
            }
        }
    }
}
