package com.iteam.repository;

import com.iteam.entities.Commande;
import com.iteam.entities.Product;
import com.iteam.entities.Status;
import com.iteam.entities.User;
import com.iteam.repositories.CommandeRepository;
import com.iteam.repositories.ProductRepository;
import com.iteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")
@DisplayName("Test sur commande repository")
public class CommandeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    private Commande commande;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Ahmed");
        user.setLastName(" Ben Ali");
        user.setEmail("ahmed@email.com");
        user.setPhoneNumber("12345678");
        user=entityManager.persist(user);

        product = new Product();
        product.setNameProduct("Laptop");
        product.setPrice(1500.0);
        product.setQuantity(10);
        product = entityManager.persist(product);


        commande = new Commande();
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatus(Status.En_attente);
        commande.setPriceTotale(1500.0);
        commande.setUser(user);
        commande.setProducts(new ArrayList<>(Arrays.asList(product)));
    }

    @Test
    @DisplayName("Sauvegarder une commande")
    void save_ShouldPersistCommande() {

        Commande savedCommande = commandeRepository.save(commande);

        assertThat(savedCommande).isNotNull();
        assertThat(savedCommande.getId()).isNotNull();
        assertThat(savedCommande.getStatus()).isEqualTo(Status.En_attente);
        assertThat(savedCommande.getPriceTotale()).isEqualTo(1500.0);
        assertThat(savedCommande.getUser()).isEqualTo(user);
     //   assertThat(savedCommande.getProducts()).contains(product);
    }

    @Test
    @DisplayName("Trouver une commande par ID")
    void findById_ShouldReturnCommande() {

        Commande savedCommande = entityManager.persist(commande);
        entityManager.flush();


        Optional<Commande> foundCommande = commandeRepository.findById(savedCommande.getId());


        assertThat(foundCommande).isPresent();
        assertThat(foundCommande.get().getStatus()).isEqualTo(Status.En_attente);
        assertThat(foundCommande.get().getPriceTotale()).isEqualTo(1500.0);
    }

    @Test
    @DisplayName("Trouver toutes les commandes")
    void findAll_ShouldReturnAllCommandes() {

        entityManager.persist(commande);

        Commande commande2 = new Commande();
        commande2.setDateCommande(LocalDateTime.now());
        commande2.setStatus(Status.En_cours);
        commande2.setPriceTotale(2000.0);
        commande2.setUser(user);
        commande2.setProducts(new ArrayList<>(Arrays.asList(product))); // Liste modifiable
        entityManager.persist(commande2);

        entityManager.flush();


        List<Commande> commandes = commandeRepository.findAll();


        assertThat(commandes.size()).isEqualTo(2);
        //assertThat(commandes)
                //.extracting(commandes.stream().map(commande1 -> commande1.getStatus()))
                //.containsExactlyInAnyOrder(Status.En_attente, Status.En_cours);
    }

    @Test
    @DisplayName("Supprimer une commande")
    void delete_ShouldRemoveCommande() {

        Commande savedCommande = entityManager.persist(commande);
        entityManager.flush();
        Long commandeId = savedCommande.getId();


        assertThat(commandeRepository.findById(commandeId)).isPresent();


        commandeRepository.delete(savedCommande);
        entityManager.flush();


        assertThat(commandeRepository.findById(commandeId)).isEmpty();
    }

    @Test
    @DisplayName("Mettre à jour une commande")
    void save_ShouldUpdateCommande() {

        Commande savedCommande = entityManager.persist(commande);
        entityManager.flush();
        Long commandeId = savedCommande.getId();


        savedCommande.setStatus(Status.Livré);
        savedCommande.setPriceTotale(1800.0);
        Commande updatedCommande = commandeRepository.save(savedCommande);
        entityManager.flush();


        Optional<Commande> foundCommande = commandeRepository.findById(commandeId);
        assertThat(foundCommande).isPresent();
        assertThat(foundCommande.get().getStatus()).isEqualTo(Status.Livré);
        assertThat(foundCommande.get().getPriceTotale()).isEqualTo(1800.0);
    }

    @Test
    @DisplayName("Vérifier l'existence d'une commande")
    void existsById_ShouldReturnTrueForExistingCommande() {

        Commande savedCommande = entityManager.persist(commande);
        entityManager.flush();
        Long commandeId = savedCommande.getId();


        boolean exists = commandeRepository.existsById(commandeId);


        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Vérifier la non-existence d'une commande")
    void existsById_ShouldReturnFalseForNonExistingCommande() {

        boolean exists = commandeRepository.existsById(999L);


        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Sauvegarder une commande avec statut Annulé")
    void save_WithAnnuleStatus() {

        commande.setStatus(Status.Annulé);
        commande.setPriceTotale(0.0);


        Commande savedCommande = commandeRepository.save(commande);


        assertThat(savedCommande.getStatus()).isEqualTo(Status.Annulé);
        assertThat(savedCommande.getPriceTotale()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Compter le nombre de commandes")
    void count_ShouldReturnNumberOfCommandes() {

        entityManager.persist(commande);

        Commande commande2 = new Commande();
        commande2.setDateCommande(LocalDateTime.now());
        commande2.setStatus(Status.En_cours);
        commande2.setPriceTotale(2000.0);
        commande2.setUser(user);
        commande2.setProducts(new ArrayList<>(Arrays.asList(product)));
        entityManager.persist(commande2);

        entityManager.flush();


        long count = commandeRepository.count();


        assertThat(count).isEqualTo(2);
    }







}
