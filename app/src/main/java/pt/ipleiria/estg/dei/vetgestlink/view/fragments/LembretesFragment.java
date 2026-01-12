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

public class LembretesFragment extends Fragment {

    private RecyclerView rvLembretes;
    private LembreteAdapter adapter;
    private List<Lembrete> listaLembretes;
    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lembretes, container, false);

        rvLembretes = view.findViewById(R.id.rvLembretes);
        rvLembretes.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAdd = view.findViewById(R.id.fab_add_lembrete);

        listaLembretes = new ArrayList<>();

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("userprofile_id", 0);

        adapter = new LembreteAdapter(listaLembretes, currentUserId);
        adapter.setOnLembreteChangedListener(this::carregarLembretes);
        rvLembretes.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> mostrarDialogoCriar());

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
                Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show();
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
}
