package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.view.activities.MainActivity;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.AnimalAdapter;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.PerfilAnimalAdapter;

public class PerfilFragment extends Fragment {

    private TextView tvNome, tvEmail, tvTelefone, tvMorada, tvAnimalCount;
    private RecyclerView rvAnimais;
    private PerfilAnimalAdapter adapter;
    private List<Animal> listaAnimais;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        tvNome = view.findViewById(R.id.tvNomeCompleto);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTelefone = view.findViewById(R.id.tvTelefone);
        tvMorada = view.findViewById(R.id.tvMorada);
        tvAnimalCount = view.findViewById(R.id.tvAnimalCount);
        rvAnimais = view.findViewById(R.id.rvAnimais);

        rvAnimais.setLayoutManager(new LinearLayoutManager(getContext()));
        listaAnimais = new ArrayList<>();
        adapter = new PerfilAnimalAdapter(listaAnimais);
        rvAnimais.setAdapter(adapter);

        Button btnVerLembretes = view.findViewById(R.id.btnVerLembretes);

        btnVerLembretes.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).navegarParaLembretes();
        });

        carregarDados();

        return view;
    }

    private void carregarDados() {
        SharedPreferences prefs = getActivity().getSharedPreferences("VetGestLinkPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", "");

        Singleton singleton = Singleton.getInstance(getContext());

        singleton.getProfile(token, new Singleton.ProfileCallback() {
            @Override
            public void onSuccess(String nome, String email, String telefone, String moradaCompleta) {
                tvNome.setText(nome);
                tvEmail.setText(email);
                tvTelefone.setText(telefone);
                tvMorada.setText(moradaCompleta);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        singleton.getAnimais(token, new Singleton.AnimaisCallback() {
            @Override
            public void onSuccess(List<Animal> animais) {
                listaAnimais.clear();
                listaAnimais.addAll(animais);
                adapter.notifyDataSetChanged();
                tvAnimalCount.setText(animais.size() + " animais registados");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}