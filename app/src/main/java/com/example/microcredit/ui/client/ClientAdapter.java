package com.example.microcredit.ui.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Client;
import java.util.ArrayList;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private List<Client> clients = new ArrayList<>();
    private OnClientClickListener listener;

    public interface OnClientClickListener {
        void onEdit(Client client);
        void onDelete(Client client);
    }

    public void setListener(OnClientClickListener l) { this.listener = l; }

    public void setClients(List<Client> liste) {
        this.clients = liste;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder h, int pos) {
        Client c = clients.get(pos);
        h.tvNom.setText(c.getNomComplet());
        h.tvCin.setText("CIN : " + (c.getCin() != null ? c.getCin() : "-"));
        h.tvTel.setText("Tél : " + (c.getTelephone() != null ? c.getTelephone() : "-"));
        h.tvStatut.setText(c.getStatut() != null ? c.getStatut() : "ACTIF");

        // Couleur statut
        int color;
        switch (c.getStatut() != null ? c.getStatut() : "") {
            case "ACTIF":     color = 0xFF2E7D32; break;
            case "INACTIF":   color = 0xFF757575; break;
            case "SUSPENDU":  color = 0xFFC62828; break;
            default:          color = 0xFF1565C0;
        }
        h.tvStatut.setTextColor(color);

        h.btnEdit.setOnClickListener(v -> { if (listener != null) listener.onEdit(c); });
        h.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(c); });
    }

    @Override
    public int getItemCount() { return clients.size(); }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvCin, tvTel, tvStatut, btnEdit, btnDelete;
        ClientViewHolder(View v) {
            super(v);
            tvNom    = v.findViewById(R.id.tvNom);
            tvCin    = v.findViewById(R.id.tvCin);
            tvTel    = v.findViewById(R.id.tvTel);
            tvStatut = v.findViewById(R.id.tvStatut);
            btnEdit  = v.findViewById(R.id.btnEdit);
            btnDelete= v.findViewById(R.id.btnDelete);
        }
    }
}