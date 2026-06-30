package com.example.microcredit.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.microcredit.data.local.AppDatabase;
import com.example.microcredit.data.dao.ClientDao;
import com.example.microcredit.data.entity.Client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClientRepository — couche entre DAO et ViewModel
 *
 * Principe SOLID (SRP) : gère uniquement l'accès aux données Client
 * Principe SOLID (DIP) : le ViewModel dépend du Repository, pas du DAO
 *
 * IMPORTANT : Room interdit les opérations INSERT/UPDATE/DELETE
 * sur le thread principal → on utilise un ExecutorService (thread séparé)
 */
public class ClientRepository {

    private final ClientDao clientDao;
    // Thread séparé pour les opérations d'écriture (insert, update, delete)
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ClientRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        clientDao = db.clientDao();
    }

    // ─────────────────────────────────────────
    //  ÉCRITURE — sur thread séparé
    // ─────────────────────────────────────────

    public void insert(Client client) {
        executor.execute(() -> clientDao.insert(client));
    }

    public void update(Client client) {
        executor.execute(() -> clientDao.update(client));
    }

    public void delete(Client client) {
        executor.execute(() -> clientDao.delete(client));
    }

    // ─────────────────────────────────────────
    //  LECTURE — LiveData (thread principal OK)
    // ─────────────────────────────────────────

    public LiveData<List<Client>> getAllClients() {
        return clientDao.getAllClients();
    }

    public LiveData<Client> getClientById(long id) {
        return clientDao.getClientById(id);
    }

    public LiveData<List<Client>> rechercherClients(String recherche) {
        return clientDao.rechercherClients(recherche);
    }

    public LiveData<List<Client>> getClientsActifs() {
        return clientDao.getClientsActifs();
    }

    public LiveData<List<Client>> getClientsByGroupe(String groupe) {
        return clientDao.getClientsByGroupe(groupe);
    }

    public LiveData<Integer> countClients() {
        return clientDao.countClients();
    }
}