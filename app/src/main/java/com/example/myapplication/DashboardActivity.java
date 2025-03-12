package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.button.MaterialButton;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        MaterialButton btnRegistrar = findViewById(R.id.btn_registrar_residuos);
        MaterialButton btnVerLista = findViewById(R.id.btn_ver_lista);
        MaterialButton btnGenerarPDF = findViewById(R.id.btn_generar_pdf);

        // Navegar a Registrar Residuos
        btnRegistrar.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, RegistroResiduosActivity.class))
        );

        // Navegar a Lista de Residuos
        btnVerLista.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ListaResiduosActivity.class))
        );

        // Generar PDF con permisos
        btnGenerarPDF.setOnClickListener(v -> {
            if (checkPermission()) {
                generarPDF();
            } else {
                requestPermission();
            }
        });
    }

    private boolean checkPermission() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generarPDF();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generarPDF() {
        try {
            File file = new File(getExternalFilesDir(null), "ReporteResiduos.pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Título
            document.add(new Paragraph("Reporte de Residuos").setBold().setFontSize(18));

            // Crear tabla con encabezados
            float[] columnWidths = {50, 200, 100};
            Table table = new Table(columnWidths);
            table.addCell("ID");
            table.addCell("Tipo de Residuo");
            table.addCell("Cantidad");

            // Obtener datos de la base de datos
            ArrayList<String[]> residuos = dbHelper.getResiduosForPDF();
            if (residuos.isEmpty()) {
                Toast.makeText(this, "No hay datos en la base de datos", Toast.LENGTH_SHORT).show();
                document.add(new Paragraph("No hay datos disponibles."));
            } else {
                for (String[] row : residuos) {
                    table.addCell(row[0]); // ID
                    table.addCell(row[1]); // Tipo de Residuo
                    table.addCell(row[2]); // Cantidad
                }
                document.add(table);
            }

            document.close();
            Toast.makeText(this, "PDF generado en " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            abrirPDF(file);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirPDF(File file) {
        Uri uri = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
