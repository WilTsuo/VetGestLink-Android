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
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.models.Morada;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.activities.MainActivity;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.PerfilAnimalAdapter;

public class PerfilFragment extends Fragment {

    private RecyclerView recyclerAnimais;
    private PerfilAnimalAdapter perfilAdapter;
    private List<Animal> perfilAnimais = new ArrayList<>();
    private TextView tvAnimalCount;

    // TextViews do utilizador
    private TextView tvNomeCompleto;
    private TextView tvEmail;
    private TextView tvTelefone;

    // TextViews da Morada
    private TextView tvRua;
    private TextView tvNPorta;
    private TextView tvCdPostal;
    private TextView tvLocalidade;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

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
            ((MainActivity) requireActivity()).navegarParaLembretes();
        });

        // Carregar dados
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

        Singleton.getInstance(requireContext()).getProfile(token, new Singleton.ProfileCallback() {
            @Override
            public void onSuccess(String nome, String email, String telefone, String moradaCompleta) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    tvNomeCompleto.setText(nome != null ? nome : "");
                    tvEmail.setText(email != null ? email : "");
                    tvTelefone.setText(telefone != null ? telefone : "");

                    // Lógica para distribuir a string da morada nos campos corretos
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

    /**
     * Tenta separar a string completa da morada nos campos individuais.
     * Assume formato aproximado: "Rua..., Porta CodPostal Localidade"
     */
    private void distribuirMorada(String moradaCompleta) {
        if (tvRua == null) return;

        if (moradaCompleta != null && !moradaCompleta.isEmpty()) {
            // Regex para encontrar Código Postal no formato XXXX-XXX
            Pattern patternCP = Pattern.compile("(\\d{4}-\\d{3})");
            Matcher matcher = patternCP.matcher(moradaCompleta);

            if (matcher.find()) {
                // 1. Extrair Código Postal
                String cp = matcher.group(1);
                tvCdPostal.setText(cp);

                // 2. Extrair Localidade (tudo depois do CP)
                String depoisCP = moradaCompleta.substring(matcher.end()).trim();
                tvLocalidade.setText(depoisCP);

                // 3. Extrair Rua e Porta (tudo antes do CP)
                String antesCP = moradaCompleta.substring(0, matcher.start()).trim();

                // Tenta separar Rua e Porta pela última vírgula, se existir
                int lastCommaIndex = antesCP.lastIndexOf(",");
                if (lastCommaIndex != -1) {
                    tvRua.setText(antesCP.substring(0, lastCommaIndex).trim());
                    tvNPorta.setText(antesCP.substring(lastCommaIndex + 1).trim());
                } else {
                    // Se não houver vírgula, assume que é tudo Rua e deixa Porta vazia
                    tvRua.setText(antesCP);
                    tvNPorta.setText("");
                }
            } else {
                // Fallback: Se não encontrar padrão de CP, coloca tudo na Rua
                tvRua.setText(moradaCompleta);
                tvNPorta.setText("");
                tvCdPostal.setText("");
                tvLocalidade.setText("");
            }
        } else {
            // Morada vazia
            tvRua.setText("Sem morada");
            tvNPorta.setText("-");
            tvCdPostal.setText("-");
            tvLocalidade.setText("-");
        }
    }

    // Mantido caso no futuro o Singleton retorne o objeto Morada diretamente
    private void preencherMorada(Morada morada) {
        if (tvRua == null) return;

        if (morada != null) {
            tvRua.setText(morada.getRua() != null ? morada.getRua() : "N/A");

            String portaInfo = morada.getNporta();
            if (morada.getAndar() != null && !morada.getAndar().isEmpty()) {
                portaInfo += " " + morada.getAndar();
            }
            tvNPorta.setText(portaInfo != null ? portaInfo : "N/A");

            tvCdPostal.setText(morada.getCdpostal() != null ? morada.getCdpostal() : "N/A");
            tvLocalidade.setText(morada.getLocalidade() != null ? morada.getLocalidade() : "N/A");
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
}
