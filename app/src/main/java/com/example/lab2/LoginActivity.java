package com.example.lab2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.botonIngresar.setOnClickListener(v -> {
            if (tieneConexion()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "Error: No hay conexión a Internet. No es posible continuar.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean tieneConexion() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo infoRed = cm.getActiveNetworkInfo();
            return infoRed != null && infoRed.isConnected();
        }
        return false;
    }
}