package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class ExemploFragment extends Fragment {

    private EditText etNumeros;
    private Button btnExemplo;
    private TextView tvBottom;
    private Button btnExemploApi;
    private TextView tvBottomAPI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exemplo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar componentes
        etNumeros = view.findViewById(R.id.et_numeros);
        btnExemplo = view.findViewById(R.id.btn_exemplo);
        tvBottom = view.findViewById(R.id.tv_bottom);
        btnExemploApi = view.findViewById(R.id.btn_exemplo_api);
        tvBottomAPI = view.findViewById(R.id.tv_bottomAPI);

        // Botão 1: Calcular raiz quadrada
        btnExemplo.setOnClickListener(v -> calcularRaiz());

        // Botão 2: Request API count
        btnExemploApi.setOnClickListener(v -> obterCountAPI());
    }

    private void calcularRaiz() {
        String input = etNumeros.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(getContext(), "Insira um número", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double numero = Double.parseDouble(input);
            if (numero < 0) {
                tvBottom.setText("Resultado: Número inválido (negativo)");
                return;
            }
            double raiz = Math.sqrt(numero);
            tvBottom.setText(String.format("Resultado: √%.0f = %.2f", numero, raiz)); //metodo de mostrar string igual ao C
        } catch (NumberFormatException e) {
            tvBottom.setText("Resultado: Erro - número inválido");
        }
    }

    private void obterCountAPI() {
        String token = Singleton.getInstance(getContext()).getAccessToken();

        if (token == null) {
            Toast.makeText(getContext(), "Token não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        tvBottomAPI.setText("Resultado: Carregando...");

        Singleton.getInstance(getContext()).getMarcacoesCount(token, new Singleton.CountCallback() {
            @Override
            public void onSuccess(int count) {
                tvBottomAPI.setText("Resultado: Total de marcações = " + count + "\n √" + count + " = " + String.format("%.2f", Math.sqrt(count))); //metodo de texto de concatenação normal :D
            }

            @Override
            public void onError(String message) {
                tvBottomAPI.setText("Resultado: Erro - " + message);
            }
        });
    }
}

