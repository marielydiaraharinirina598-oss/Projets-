package com.example.microcredit.ui.credit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Credit;
import java.util.ArrayList;
import java.util.List;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.CreditViewHolder> {

    private List<Credit> credits = new ArrayList<>();
    private OnCreditClickListener listener;

    public interface OnCreditClickListener {
        void onVoirEcheancier(Credit credit);
        void onDelete(Credit credit);
    }

    public void setListener(OnCreditClickListener l) { this.listener = l; }

    public void setCredits(List<Credit> liste) {
        this.credits = liste;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CreditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credit, parent, false);
        return new CreditViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditViewHolder h, int pos) {
        Credit c = credits.get(pos);

        h.tvMontant.setText(String.format("%,.0f Ar", c.getMontantPrincipal()));
        h.tvTaux.setText(String.format("Taux : %.1f%% — %d mois", c.getTauxInteret(), c.getDureeMois()));
        h.tvMensualite.setText(String.format("Mensualité : %,.0f Ar", c.getMensualite()));
        h.tvRestant.setText(String.format("Restant : %,.0f Ar", c.getMontantRestant()));
        h.tvDate.setText("Début : " + (c.getDateDebut() != null ? c.getDateDebut() : "-"));
        h.tvStatut.setText(c.getStatut() != null ? c.getStatut() : "-");
        h.tvFrequence.setText(c.getFrequencePaiement() != null ? c.getFrequencePaiement() : "-");

        // Couleur statut
        int color;
        switch (c.getStatut() != null ? c.getStatut() : "") {
            case "ACTIF":      color = 0xFF2E7D32; break;
            case "EN_RETARD":  color = 0xFFC62828; break;
            case "SOLDE":      color = 0xFF1565C0; break;
            case "EN_ATTENTE": color = 0xFFE65100; break;
            default:           color = 0xFF757575;
        }
        h.tvStatut.setTextColor(color);

        h.btnEcheancier.setOnClickListener(v -> { if (listener != null) listener.onVoirEcheancier(c); });
        h.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(c); });
    }

    @Override
    public int getItemCount() { return credits.size(); }

    static class CreditViewHolder extends RecyclerView.ViewHolder {
        TextView tvMontant, tvTaux, tvMensualite, tvRestant,
                tvDate, tvStatut, tvFrequence, btnEcheancier, btnDelete;
        CreditViewHolder(View v) {
            super(v);
            tvMontant    = v.findViewById(R.id.tvMontant);
            tvTaux       = v.findViewById(R.id.tvTaux);
            tvMensualite = v.findViewById(R.id.tvMensualite);
            tvRestant    = v.findViewById(R.id.tvRestant);
            tvDate       = v.findViewById(R.id.tvDate);
            tvStatut     = v.findViewById(R.id.tvStatut);
            tvFrequence  = v.findViewById(R.id.tvFrequence);
            btnEcheancier= v.findViewById(R.id.btnEcheancier);
            btnDelete    = v.findViewById(R.id.btnDelete);
        }
    }
}