package com.example.microcredit.ui.retard;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microcredit.R;
import com.example.microcredit.ui.echeancier.EcheancierAdapter;
import com.example.microcredit.ui.echeancier.EcheancierViewModel;
import com.example.microcredit.data.entity.Echeancier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RetardActivity extends AppCompatActivity {

    private EcheancierViewModel viewModel;
    private EcheancierAdapter   adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retard);

        viewModel = new ViewModelProvider(this).get(EcheancierViewModel.class);

        RecyclerView recycler = findViewById(R.id.recyclerRetards);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EcheancierAdapter();
        recycler.setAdapter(adapter);

        TextView tvCount      = findViewById(R.id.tvCountRetard);
        TextView tvTotalRetard = findViewById(R.id.tvTotalRetard);

        // Date d'aujourd'hui pour filtrer les retards
        String dateAuj = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        // Observer les échéances en retard
        viewModel.getEcheancesEnRetard(dateAuj).observe(this, liste -> {
            if (liste == null) return;
            adapter.setEcheanciers(liste);
            tvCount.setText(liste.size() + " échéance(s) en retard");

            // Calculer le total en retard
            double total = 0;
            for (Echeancier e : liste) total += e.getMontantRestant();
            tvTotalRetard.setText(String.format("Montant total en retard : %,.0f Ar", total));
        });

        // Paiement depuis l'écran retard
        adapter.setListener(echeancier -> {
            // On a besoin du creditId — on utilise le creditId de l'échéancier
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(this);
            android.view.View dialogView = getLayoutInflater()
                    .inflate(R.layout.dialog_paiement, null);

            TextView tvInfo    = dialogView.findViewById(R.id.tvInfoEcheance);
            android.widget.EditText etMontant =
                    dialogView.findViewById(R.id.etMontantPaiement);

            tvInfo.setText(String.format(
                    "Échéance n°%d — Crédit #%d\nDate prévue : %s\nRestant : %,.0f Ar",
                    echeancier.getNumeroEcheance(),
                    echeancier.getCreditId(),
                    echeancier.getDateEcheance(),
                    echeancier.getMontantRestant()));
            etMontant.setText(String.format("%.0f", echeancier.getMontantRestant()));

            builder.setTitle("⚠️ Paiement en retard")
                    .setView(dialogView)
                    .setPositiveButton("Confirmer", (d, w) -> {
                        String s = etMontant.getText().toString().trim();
                        if (s.isEmpty()) return;
                        double montant = Double.parseDouble(s);
                        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(new Date());
                        viewModel.enregistrerPaiement(
                                echeancier.getId(),
                                echeancier.getCreditId(),
                                montant, today);
                        Toast.makeText(this,
                                String.format("Paiement de %,.0f Ar enregistré ✓", montant),
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
    }
}