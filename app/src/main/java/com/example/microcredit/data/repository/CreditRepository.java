package com.example.microcredit.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.microcredit.data.local.AppDatabase;
import com.example.microcredit.data.dao.CreditDao;
import com.example.microcredit.data.dao.EcheancierDao;
import com.example.microcredit.data.entity.Credit;
import com.example.microcredit.data.entity.Echeancier;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CreditRepository — couche entre DAO et ViewModel
 */
public class CreditRepository {

    private final CreditDao creditDao;
    private final EcheancierDao echeancierDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CreditRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        creditDao     = db.creditDao();
        echeancierDao = db.echeancierDao();
    }

    // ─────────────────────────────────────────
    //  CRÉDIT — écriture
    // ─────────────────────────────────────────

    public void insert(Credit credit) {
        executor.execute(() -> creditDao.insert(credit));
    }

    public void update(Credit credit) {
        executor.execute(() -> creditDao.update(credit));
    }

    public void delete(Credit credit) {
        executor.execute(() -> creditDao.delete(credit));
    }

    public void updateStatut(long creditId, String statut) {
        executor.execute(() -> creditDao.updateStatut(creditId, statut));
    }

    public void ajouterRemboursement(long creditId, double montant) {
        executor.execute(() -> creditDao.ajouterRemboursement(creditId, montant));
    }

    // ─────────────────────────────────────────
    //  CRÉDIT — lecture
    // ─────────────────────────────────────────

    public LiveData<List<Credit>> getAllCredits() {
        return creditDao.getAllCredits();
    }

    public LiveData<Credit> getCreditById(long id) {
        return creditDao.getCreditById(id);
    }

    public LiveData<List<Credit>> getCreditsByClient(long clientId) {
        return creditDao.getCreditsByClient(clientId);
    }

    public LiveData<List<Credit>> getCreditsActifs() {
        return creditDao.getCreditsActifs();
    }

    public LiveData<List<Credit>> getCreditsEnRetard() {
        return creditDao.getCreditsEnRetard();
    }

    public LiveData<Double> getTotalMontantPrete() {
        return creditDao.getTotalMontantPrete();
    }

    public LiveData<Double> getTotalRestantDu() {
        return creditDao.getTotalRestantDu();
    }

    // ─────────────────────────────────────────
    //  ÉCHÉANCIER — écriture
    // ─────────────────────────────────────────

    public void insertEcheances(List<Echeancier> echeanciers) {
        executor.execute(() -> echeancierDao.insertAll(echeanciers));
    }

    public void enregistrerPaiement(long echeancierEid, double montant, String date) {
        executor.execute(() -> echeancierDao.enregistrerPaiement(echeancierEid, montant, date));
    }

    // ─────────────────────────────────────────
    //  ÉCHÉANCIER — lecture
    // ─────────────────────────────────────────

    public LiveData<List<Echeancier>> getEcheanciersByCredit(long creditId) {
        return echeancierDao.getEcheanciersByCredit(creditId);
    }

    public LiveData<Echeancier> getProchaineEcheance(long creditId) {
        return echeancierDao.getProchaineEcheance(creditId);
    }

    public LiveData<List<Echeancier>> getEcheancesEnRetard(String dateAujourdhui) {
        return echeancierDao.getEcheancesEnRetard(dateAujourdhui);
    }

    public LiveData<Double> getTotalPayeParCredit(long creditId) {
        return echeancierDao.getTotalPayeParCredit(creditId);
    }
}