package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Nota;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.LembreteAdapter;

public class LembretesFragment extends Fragment {

    private RecyclerView rvLembretes;
    private LembreteAdapter adapter;
    private List<Nota> listaLembretes;
    private static final String PREFS_NAME = "VetGestLinkPrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lembretes, container, false);

        rvLembretes = view.findViewById(R.id.rvLembretes);
        rvLembretes.setLayoutManager(new LinearLayoutManager(getContext()));

        listaLembretes = new ArrayList<>();

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("userprofile_id", 0);

        adapter = new LembreteAdapter(listaLembretes, currentUserId);
        adapter.setOnLembreteChangedListener(this::carregarLembretes);
        rvLembretes.setAdapter(adapter);

        carregarLembretes();

        return view;
    }

    private void carregarLembretes() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", "");

        Singleton.getInstance(requireContext()).getNotas(token, null, new Singleton.NotasCallback() {
            @Override
            public void onSuccess(List<Nota> notas) {
                listaLembretes.clear();
                listaLembretes.addAll(notas);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
