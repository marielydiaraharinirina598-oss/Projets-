package com.example.microcredit.ui.client;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.microcredit.data.entity.Client;
import com.example.microcredit.data.repository.ClientRepository;
import java.util.List;

public class ClientViewModel extends AndroidViewModel {

    private final ClientRepository repository;
    private final LiveData<List<Client>> tousLesClients;

    public ClientViewModel(Application application) {
        super(application);
        repository      = new ClientRepository(application);
        tousLesClients  = repository.getAllClients();
    }

    public LiveData<List<Client>> getTousLesClients() { return tousLesClients; }
    public LiveData<List<Client>> rechercherClients(String q) { return repository.rechercherClients(q); }
    public LiveData<Integer> countClients() { return repository.countClients(); }

    public void insert(Client c) { repository.insert(c); }
    public void update(Client c) { repository.update(c); }
    public void delete(Client c) { repository.delete(c); }
}