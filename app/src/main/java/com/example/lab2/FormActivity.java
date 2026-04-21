package com.example.lab2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.databinding.ActivityFormBinding;

import java.util.ArrayList;
import java.util.List;

public class FormActivity extends AppCompatActivity {

    private ActivityFormBinding binding;
    private String modo = "crear";
    private int indiceEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón atrás en ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        configurarSpinnerTipo();
        recibirDatos();

        binding.botonGuardar.setOnClickListener(v -> validarYConfirmar());
    }

    private void configurarSpinnerTipo() {
        List<String> tipos = new ArrayList<>();
        tipos.add("Multímetro");
        tipos.add("OTDR");
        tipos.add("Medidor de potencia");
        tipos.add("Analizador de espectro");
        tipos.add("Pinza amperimétrica");

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                R.layout.spinner_item, tipos);
        adaptador.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerTipo.setAdapter(adaptador);
    }

    private void recibirDatos() {
        modo = getIntent().getStringExtra("modo");
        if (modo == null) modo = "crear";

        if (modo.equals("editar")) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Actualizar equipo");
            }

            indiceEditar = getIntent().getIntExtra("indice", -1);
            String codigo = getIntent().getStringExtra("codigo");
            String nombre = getIntent().getStringExtra("nombre");
            String tipo = getIntent().getStringExtra("tipo");
            String estado = getIntent().getStringExtra("estado");
            String observaciones = getIntent().getStringExtra("observaciones");

            binding.editCodigo.setText(codigo);
            binding.editNombre.setText(nombre);
            binding.editObservaciones.setText(observaciones);

            // Código y Tipo deshabilitados al editar
            binding.editCodigo.setEnabled(false);
            binding.spinnerTipo.setEnabled(false);

            // Seleccionar tipo en spinner
            ArrayAdapter<String> adaptador = (ArrayAdapter<String>) binding.spinnerTipo.getAdapter();
            if (adaptador != null) {
                int pos = adaptador.getPosition(tipo);
                binding.spinnerTipo.setSelection(pos);
            }

            // Seleccionar estado en RadioGroup
            if (estado != null) {
                switch (estado) {
                    case "Operativo":
                        binding.radioOperativo.setChecked(true);
                        break;
                    case "En mantenimiento":
                        binding.radioMantenimiento.setChecked(true);
                        break;
                    case "Fuera de servicio":
                        binding.radioFueraServicio.setChecked(true);
                        break;
                }
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Registrar equipo");
            }
        }
    }

    private void validarYConfirmar() {
        String codigo = binding.editCodigo.getText().toString().trim();
        String nombre = binding.editNombre.getText().toString().trim();
        String tipo = binding.spinnerTipo.getSelectedItem().toString();
        String observaciones = binding.editObservaciones.getText().toString().trim();

        // Validar campos obligatorios
        if (codigo.isEmpty()) {
            binding.editCodigo.setError("El código es obligatorio");
            return;
        }
        if (nombre.isEmpty()) {
            binding.editNombre.setError("El nombre es obligatorio");
            return;
        }
        if (binding.radioGroupEstado.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleccione un estado", Toast.LENGTH_SHORT).show();
            return;
        }

        String estado = "";
        int idSeleccionado = binding.radioGroupEstado.getCheckedRadioButtonId();
        if (idSeleccionado == R.id.radioOperativo) {
            estado = "Operativo";
        } else if (idSeleccionado == R.id.radioMantenimiento) {
            estado = "En mantenimiento";
        } else if (idSeleccionado == R.id.radioFueraServicio) {
            estado = "Fuera de servicio";
        }

        String mensajeConfirmacion = modo.equals("editar") ?
                "¿Está seguro que desea actualizar?" :
                "¿Está seguro que desea registrar?";

        String estadoFinal = estado;
        new AlertDialog.Builder(this)
                .setMessage(mensajeConfirmacion)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    guardarEquipo(codigo, nombre, tipo, estadoFinal, observaciones);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarEquipo(String codigo, String nombre, String tipo,
                               String estado, String observaciones) {
        if (modo.equals("editar") && indiceEditar >= 0) {
            Equipo equipo = MainActivity.listaEquipos.get(indiceEditar);
            equipo.setNombre(nombre);
            equipo.setEstado(estado);
            equipo.setObservaciones(observaciones);
            Toast.makeText(this, "Equipo actualizado", Toast.LENGTH_SHORT).show();
        } else {
            Equipo nuevoEquipo = new Equipo(codigo, nombre, tipo, estado, observaciones);
            MainActivity.listaEquipos.add(nuevoEquipo);
            Toast.makeText(this, "Equipo registrado", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}