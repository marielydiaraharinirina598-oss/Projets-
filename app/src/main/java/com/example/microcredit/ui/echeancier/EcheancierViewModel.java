package com.example.microcredit.ui.echeancier;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.microcredit.data.entity.Echeancier;
import com.example.microcredit.data.repository.CreditRepository;
import java.util.List;

public class EcheancierViewModel extends AndroidViewModel {

    private final CreditRepository repository;

    public EcheancierViewModel(Application application) {
        super(application);
        repository = new CreditRepository(application);
    }

    public LiveData<List<Echeancier>> getEcheanciersByCredit(long creditId) {
        return repository.getEcheanciersByCredit(creditId);
    }

    public LiveData<Echeancier> getProchaineEcheance(long creditId) {
        return repository.getProchaineEcheance(creditId);
    }

    public LiveData<Double> getTotalPaye(long creditId) {
        return repository.getTotalPayeParCredit(creditId);
    }

    // ← utilisé par RetardActivity
    public LiveData<List<Echeancier>> getEcheancesEnRetard(String dateAujourdhui) {
        return repository.getEcheancesEnRetard(dateAujourdhui);
    }

    public void enregistrerPaiement(long echeancierEid, long creditId,
                                    double montant, String date) {
        repository.enregistrerPaiement(echeancierEid, montant, date);
        repository.ajouterRemboursement(creditId, montant);
    }
}