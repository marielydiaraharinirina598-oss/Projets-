package com.example.microcredit.ui.credit;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.microcredit.data.entity.Credit;
import com.example.microcredit.data.repository.CreditRepository;
import java.util.List;

public class CreditViewModel extends AndroidViewModel {

    private final CreditRepository repository;

    public CreditViewModel(Application application) {
        super(application);
        repository = new CreditRepository(application);
    }

    public LiveData<List<Credit>> getAllCredits()                    { return repository.getAllCredits(); }
    public LiveData<List<Credit>> getCreditsByClient(long clientId) { return repository.getCreditsByClient(clientId); }
    public LiveData<List<Credit>> getCreditsActifs()                { return repository.getCreditsActifs(); }
    public LiveData<List<Credit>> getCreditsEnRetard()              { return repository.getCreditsEnRetard(); }
    public LiveData<Double> getTotalMontantPrete()                  { return repository.getTotalMontantPrete(); }
    public LiveData<Double> getTotalRestantDu()                     { return repository.getTotalRestantDu(); }

    public void insert(Credit c)                        { repository.insert(c); }
    public void update(Credit c)                        { repository.update(c); }
    public void delete(Credit c)                        { repository.delete(c); }
    public void updateStatut(long id, String statut)    { repository.updateStatut(id, statut); }

    public void insertAvecEcheancier(Credit credit,
                                     com.example.microcredit.domain.usecase.CreateEcheancierUseCase useCase) {
        repository.insert(credit);
        // Génère l'échéancier après insertion
        useCase.execute(credit.getClientId(), credit.getMontantPrincipal(),
                credit.getTauxInteret(), credit.getDureeMois(), credit.getDateDebut());
    }
}