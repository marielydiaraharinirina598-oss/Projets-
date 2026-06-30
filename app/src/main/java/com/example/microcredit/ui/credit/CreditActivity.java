package com.example.microcredit.ui.credit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Credit;
import com.example.microcredit.ui.echeancier.EcheancierActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreditActivity extends AppCompatActivity {

    public static final String EXTRA_CREDIT_ID = "credit_id";
    public static final String EXTRA_MODE      = "mode";

    private CreditViewModel viewModel;
    private CreditAdapter   adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        viewModel = new ViewModelProvider(this).get(CreditViewModel.class);

        RecyclerView recycler = findViewById(R.id.recyclerCredits);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CreditAdapter();
        recycler.setAdapter(adapter);

        TextView tvTotal = findViewById(R.id.tvTotal);
        TextView tvCount = findViewById(R.id.tvCount);

        // Observer tous les crédits
        viewModel.getAllCredits().observe(this, credits -> {
            adapter.setCredits(credits);
            tvCount.setText(credits.size() + " crédit(s)");
        });

        // Total prêté
        viewModel.getTotalMontantPrete().observe(this, total -> {
            if (total != null)
                tvTotal.setText(String.format("Total prêté : %,.0f Ar", total));
        });

        // Clics
        adapter.setListener(new CreditAdapter.OnCreditClickListener() {
            @Override
            public void onVoirEcheancier(Credit credit) {
                Intent i = new Intent(CreditActivity.this, EcheancierActivity.class);
                i.putExtra(EcheancierActivity.EXTRA_CREDIT_ID, credit.getId());
                startActivity(i);
            }
            @Override
            public void onDelete(Credit credit) {
                new AlertDialog.Builder(CreditActivity.this)
                        .setTitle("Supprimer ce crédit ?")
                        .setMessage(String.format("Montant : %,.0f Ar\nCette action supprime aussi l'échéancier.", credit.getMontantPrincipal()))
                        .setPositiveButton("Supprimer", (d, w) -> viewModel.delete(credit))
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        });

        // FAB → nouveau crédit
        FloatingActionButton fab = findViewById(R.id.fabAjouter);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, CreditFormActivity.class);
            i.putExtra(EXTRA_MODE, "add");
            startActivity(i);
        });

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
    }
}