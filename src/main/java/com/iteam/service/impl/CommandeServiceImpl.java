package com.iteam.service.impl;

import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.Commande;
import com.iteam.entities.Product;
import com.iteam.entities.Status;
import com.iteam.entities.User;
import com.iteam.repositories.CommandeRepository;
import com.iteam.repositories.ProductRepository;
import com.iteam.repositories.UserRepository;
import com.iteam.service.CommandeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Commande> findAll() {

        return commandeRepository.findAll();
    }

    @Override
    public Commande createCommande(Long userId,List<Long> productsId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundEntityExceptions("User with ID : " + userId + " not found"));
        List<Product> products = productRepository.findAllById(productsId);
        if(products.size() != productsId.size()){
            throw new NotFoundEntityExceptions("One or more products were not found");
        }
        Commande commande = new Commande();
        commande.setUser(user);
        commande.setProducts(products);
        commande.setStatus(Status.En_attente);
        commande.setDateCommande(LocalDateTime.now());

        Double totalPrice = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
        commande.setPriceTotale(totalPrice);
        return commandeRepository.save(commande);
    }


    @Override
    public Commande findCommandeById(Long id) {

        return commandeRepository.findById(id)
                .orElseThrow(()->new NotFoundEntityExceptions("Commande with ID : " + id + " not found"));
    }

    @Override
    public void deleteCommande(Long id) {

        Optional<Commande> commande = commandeRepository.findById(id);
        if(!commande.isPresent()){
            throw new NotFoundEntityExceptions("No Orders with the ID: " + id);
        } else {
            commandeRepository.deleteById(id);
        }
       //Commande deletedCommande = findCommandeById(id);
       //commandeRepository.delete(deletedCommande);
    }

    @Override
    public Commande updateCommande(Long id, Commande commande) {

        Commande commandeToUpdate = findCommandeById(id);
        commandeToUpdate.setProducts(commande.getProducts());
        commandeToUpdate.setStatus(commande.getStatus());
        return commandeRepository.save(commandeToUpdate);

    }
}
