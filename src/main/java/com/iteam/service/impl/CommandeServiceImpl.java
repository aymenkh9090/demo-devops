package com.iteam.service.impl;

import com.iteam.entities.Commande;
import com.iteam.repositories.CommandeRepository;
import com.iteam.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;

    @Override
    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    @Override
    public Commande createCommande(Commande commande)
    {
        return commandeRepository.save(commande);
    }

    @Override
    public Commande findCommandeById(Long id) {

        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found"));
    }

    @Override
    public void deleteCommande(Long id) {
       Commande deletedCommande = findCommandeById(id);
       commandeRepository.delete(deletedCommande);
    }

    @Override
    public Commande updateCommande(Long id, Commande commande) {
        Commande updatedCommande = findCommandeById(id);
        updatedCommande.setStatus(commande.getStatus());
        updatedCommande.setUser(commande.getUser());
        updatedCommande.setProducts(commande.getProducts());
        updatedCommande.setPriceTotale(commande.getPriceTotale());
        return commandeRepository.save(updatedCommande);
    }
}
