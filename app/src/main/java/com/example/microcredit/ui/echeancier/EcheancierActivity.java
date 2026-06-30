package com.example.microcredit.ui.echeancier;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Echeancier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EcheancierActivity extends AppCompatActivity {

    public static final String EXTRA_CREDIT_ID = "credit_id";

    private EcheancierViewModel viewModel;
    private EcheancierAdapter   adapter;
    private long creditId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echeancier);

        creditId  = getIntent().getLongExtra(EXTRA_CREDIT_ID, -1);
        viewModel = new ViewModelProvider(this).get(EcheancierViewModel.class);

        RecyclerView recycler = findViewById(R.id.recyclerEcheancier);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EcheancierAdapter();
        recycler.setAdapter(adapter);

        TextView tvTotalPaye   = findViewById(R.id.tvTotalPaye);
        TextView tvNbEcheance  = findViewById(R.id.tvNbEcheance);
        TextView tvProchaine   = findViewById(R.id.tvProchaine);
        TextView tvProgression = findViewById(R.id.tvProgression);

        // Observer la liste
        viewModel.getEcheanciersByCredit(creditId).observe(this, liste -> {
            if (liste == null) return;
            adapter.setEcheanciers(liste);
            tvNbEcheance.setText(liste.size() + " échéance(s)");

            long nbPayees = 0;
            for (Echeancier e : liste) {
                if ("PAYEE".equals(e.getStatut())) nbPayees++;
            }
            int pct = liste.isEmpty() ? 0 : (int)(nbPayees * 100 / liste.size());
            tvProgression.setText("Progression : " + nbPayees + "/" + liste.size() + " (" + pct + "%)");
        });

        // Total payé
        viewModel.getTotalPaye(creditId).observe(this, total ->
                tvTotalPaye.setText(String.format("Total payé : %,.0f Ar",
                        total != null ? total : 0.0)));

        // Prochaine échéance
        viewModel.getProchaineEcheance(creditId).observe(this, e -> {
            if (e != null)
                tvProchaine.setText("Prochaine : " + e.getDateEcheance()
                        + " — " + String.format("%,.0f Ar", e.getMontantEcheance()));
            else
                tvProchaine.setText("✅ Toutes les échéances sont payées !");
        });

        // Clic payer
        adapter.setListener(e -> afficherDialogPaiement(e));

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
    }

    private void afficherDialogPaiement(Echeancier echeancier) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_paiement, null);

        TextView tvInfo    = dialogView.findViewById(R.id.tvInfoEcheance);
        EditText etMontant = dialogView.findViewById(R.id.etMontantPaiement);

        tvInfo.setText(String.format(
                "Échéance n°%d\nDate : %s\nMontant dû : %,.0f Ar\nDéjà payé : %,.0f Ar\nRestant : %,.0f Ar",
                echeancier.getNumeroEcheance(),
                echeancier.getDateEcheance(),
                echeancier.getMontantEcheance(),
                echeancier.getMontantPaye(),
                echeancier.getMontantRestant()));

        etMontant.setText(String.format("%.0f", echeancier.getMontantRestant()));

        new AlertDialog.Builder(this)
                .setTitle("💳 Enregistrer un paiement")
                .setView(dialogView)
                .setPositiveButton("Confirmer", (d, w) -> {
                    String sMontant = etMontant.getText().toString().trim();
                    if (sMontant.isEmpty()) {
                        Toast.makeText(this, "Entrez un montant", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double montant = Double.parseDouble(sMontant);
                    if (montant <= 0) {
                        Toast.makeText(this, "Montant invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String dateAuj = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(new Date());

                    // Passe les deux IDs : échéancier + crédit
                    viewModel.enregistrerPaiement(
                            echeancier.getId(), creditId, montant, dateAuj);

                    Toast.makeText(this,
                            String.format("Paiement de %,.0f Ar enregistré ✓", montant),
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}