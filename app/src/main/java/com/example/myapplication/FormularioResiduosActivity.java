package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FormularioResiduosActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private String tipoResiduo;
    private ImageView imageResiduo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_residuos);

        dbHelper = new DatabaseHelper(this);

        TextView textTipoResiduo = findViewById(R.id.text_tipo_residuo);
        EditText editCantidad = findViewById(R.id.edit_cantidad);
        Button btnGuardar = findViewById(R.id.btn_guardar);
        imageResiduo = findViewById(R.id.image_residuo); // Imagen del residuo

        // Obtener el tipo de residuo desde el Intent
        tipoResiduo = getIntent().getStringExtra("tipo_residuo");
        textTipoResiduo.setText("Tipo: " + tipoResiduo);

        // Asignar la imagen correspondiente según el tipo de residuo
        setImageForResiduo(tipoResiduo);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cantidadStr = editCantidad.getText().toString().trim();
                if (cantidadStr.isEmpty()) {
                    Toast.makeText(FormularioResiduosActivity.this, "Ingrese una cantidad", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidad = Integer.parseInt(cantidadStr);
                if (dbHelper.insertResiduos(tipoResiduo, cantidad)) {
                    Toast.makeText(FormularioResiduosActivity.this, "Registro guardado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FormularioResiduosActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para cambiar la imagen según el tipo de residuo
    private void setImageForResiduo(String tipo) {
        if (tipo == null) return;

        switch (tipo.toLowerCase()) {
            case "vidrio":
                imageResiduo.setImageResource(R.drawable.vidrio);
                break;
            case "plástico":
                imageResiduo.setImageResource(R.drawable.plastico);
                break;
            case "papel":
                imageResiduo.setImageResource(R.drawable.papel);
                break;
            case "metal":
                imageResiduo.setImageResource(R.drawable.metal);
                break;
            case "orgánico":
                imageResiduo.setImageResource(R.drawable.organico);
                break;
            default:
                imageResiduo.setImageResource(R.drawable.residuos);
                break;
        }
    }
}
