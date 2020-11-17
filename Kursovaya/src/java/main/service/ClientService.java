package main.service;


import main.entity.Client;

import java.util.List;

public interface ClientService {

    List<Client> listClients();

    Client findClient(long id);

    Client addClient(Client client);

    void deleteClient(long id);

    long[] getClientBookIds(long id);
}
