package com.example.microcredit.ui.echeancier;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Echeancier;
import java.util.ArrayList;
import java.util.List;

public class EcheancierAdapter extends RecyclerView.Adapter<EcheancierAdapter.EcheancierViewHolder> {

    private List<Echeancier> echeanciers = new ArrayList<>();
    private OnPaiementClickListener listener;

    public interface OnPaiementClickListener {
        void onPayer(Echeancier echeancier);
    }

    public void setListener(OnPaiementClickListener l) { this.listener = l; }

    public void setEcheanciers(List<Echeancier> liste) {
        this.echeanciers = liste;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EcheancierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_echeancier, parent, false);
        return new EcheancierViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EcheancierViewHolder h, int pos) {
        Echeancier e = echeanciers.get(pos);

        h.tvNumero.setText("Échéance n°" + e.getNumeroEcheance());
        h.tvDate.setText("📅 " + e.getDateEcheance());
        h.tvCapital.setText(String.format("Capital : %,.0f Ar", e.getMontantCapital()));
        h.tvInteret.setText(String.format("Intérêt : %,.0f Ar", e.getMontantInteret()));
        h.tvTotal.setText(String.format("Total : %,.0f Ar", e.getMontantEcheance()));
        h.tvRestant.setText(String.format("Restant dû : %,.0f Ar", e.getCapitalRestantDu()));
        h.tvStatut.setText(e.getStatut() != null ? e.getStatut() : "-");

        // Montant payé
        if (e.getMontantPaye() > 0) {
            h.tvPaye.setVisibility(View.VISIBLE);
            h.tvPaye.setText(String.format("Payé : %,.0f Ar", e.getMontantPaye()));
        } else {
            h.tvPaye.setVisibility(View.GONE);
        }

        // Couleur fond + statut selon état
        int bgColor;
        int statutColor;
        switch (e.getStatut() != null ? e.getStatut() : "") {
            case "PAYEE":
                bgColor    = 0xFFE8F5E9; // vert clair
                statutColor = 0xFF2E7D32;
                h.btnPayer.setVisibility(View.GONE);
                break;
            case "EN_RETARD":
                bgColor    = 0xFFFFEBEE; // rouge clair
                statutColor = 0xFFC62828;
                h.btnPayer.setVisibility(View.VISIBLE);
                break;
            case "PARTIELLEMENT_PAYEE":
                bgColor    = 0xFFFFF8E1; // jaune clair
                statutColor = 0xFFE65100;
                h.btnPayer.setVisibility(View.VISIBLE);
                break;
            default: // EN_ATTENTE
                bgColor    = 0xFFFFFFFF;
                statutColor = 0xFF1565C0;
                h.btnPayer.setVisibility(View.VISIBLE);
                break;
        }
        h.itemView.setBackgroundColor(bgColor);
        h.tvStatut.setTextColor(statutColor);

        h.btnPayer.setOnClickListener(v -> {
            if (listener != null) listener.onPayer(e);
        });
    }

    @Override
    public int getItemCount() { return echeanciers.size(); }

    static class EcheancierViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero, tvDate, tvCapital, tvInteret, tvTotal,
                tvRestant, tvStatut, tvPaye, btnPayer;

        EcheancierViewHolder(View v) {
            super(v);
            tvNumero  = v.findViewById(R.id.tvNumero);
            tvDate    = v.findViewById(R.id.tvDate);
            tvCapital = v.findViewById(R.id.tvCapital);
            tvInteret = v.findViewById(R.id.tvInteret);
            tvTotal   = v.findViewById(R.id.tvTotal);
            tvRestant = v.findViewById(R.id.tvRestant);
            tvStatut  = v.findViewById(R.id.tvStatut);
            tvPaye    = v.findViewById(R.id.tvPaye);
            btnPayer  = v.findViewById(R.id.btnPayer);
        }
    }
}