package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroResiduosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residuos_adapter); // ✅ XML corregido

        Button btnOrganico = findViewById(R.id.btn_organico);
        Button btnPlastico = findViewById(R.id.btn_plastico);
        Button btnPapel = findViewById(R.id.btn_papel);
        Button btnVidrio = findViewById(R.id.btn_vidrio);
        Button btnMetal = findViewById(R.id.btn_metal);

        btnOrganico.setOnClickListener(v -> abrirFormulario("Orgánico"));
        btnPlastico.setOnClickListener(v -> abrirFormulario("Plástico"));
        btnPapel.setOnClickListener(v -> abrirFormulario("Papel"));
        btnVidrio.setOnClickListener(v -> abrirFormulario("Vidrio"));
        btnMetal.setOnClickListener(v -> abrirFormulario("Metal"));
    }

    private void abrirFormulario(String tipo) {
        Intent intent = new Intent(this, FormularioResiduosActivity.class);
        intent.putExtra("tipo_residuo", tipo);
        startActivity(intent);
}
}