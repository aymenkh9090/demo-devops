package com.iteam.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iteam.dto.CreateCommandeRequestDTO;
import com.iteam.entities.Commande;
import com.iteam.entities.Product;
import com.iteam.entities.Status;
import com.iteam.entities.User;
import com.iteam.service.CommandeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommandeController.class)
@DisplayName("Test pour commande controller")
class CommandeControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommandeService commandeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Commande commande;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        user = new User();
        user.setId(1L);
        user.setFirstName("Ahmed");
        user.setLastName("Ben Ali");
        user.setEmail("ahmed@email.com");

        product = new Product();
        product.setId(1L);
        product.setNameProduct("Laptop");
        product.setPrice(1500.0);
        product.setQuantity(10);

        commande = new Commande();
        commande.setId(1L);
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatus(Status.En_attente);
        commande.setPriceTotale(1500.0);
        commande.setUser(user);
        commande.setProducts(Arrays.asList(product));


    }

    @Test
    @DisplayName("POST /api/ordres/create - Créer une commande")
    void createCommande_Success() throws Exception {

        CreateCommandeRequestDTO requestDTO = new CreateCommandeRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setProductsId(Arrays.asList(1L));

        when(commandeService.createCommande(1L, Arrays.asList(1L))).thenReturn(commande);


        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order create with success"))
                .andExpect(jsonPath("$.order.id").value(1L)) //
                .andExpect(jsonPath("$.order.status").value("En_attente"))
                .andExpect(jsonPath("$.order.priceTotale").value(1500.0));
    }

    @Test
    @DisplayName("GET /api/ordres - Liste toutes les commandes")
    void findAllCommandes_Success() throws Exception {

        List<Commande> commandes = Arrays.asList(commande);
        when(commandeService.findAll()).thenReturn(commandes);


        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("En_attente"));
    }

    @Test
    @DisplayName("GET /api/ordres/{id} - Trouver une commande par ID")
    void findCommandeById_Success() throws Exception {

        when(commandeService.findCommandeById(1L)).thenReturn(commande);


        mockMvc.perform(get("/api/ordres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("En_attente"));
    }

    @Test
    @DisplayName("GET /api/ordres/{id} - Commande non trouvée")
    void findCommandeById_NotFound() throws Exception {

        when(commandeService.findCommandeById(99L))
                .thenThrow(new com.iteam.Exceptions.NotFoundEntityExceptions("Commande with ID : 99 not found"));


        mockMvc.perform(get("/api/ordres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande with ID : 99 not found"))
                .andExpect(jsonPath("$.error").value("Ressources_Not_Found"));
    }

    @Test
    @DisplayName("PUT /api/ordres/{id} - Mettre à jour le statut d'une commande")
    void updateCommande_Success() throws Exception {

        Commande updatedCommande = new Commande();
        updatedCommande.setId(1L);
        updatedCommande.setStatus(Status.Livré);
        updatedCommande.setPriceTotale(2000.0);

        when(commandeService.updateCommande(eq(1L), any(Commande.class))).thenReturn(updatedCommande);


        mockMvc.perform(put("/api/ordres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Orders Successufully"))
                .andExpect(jsonPath("$.order.id").value(1L)) // "orders" en minuscule
                .andExpect(jsonPath("$.order.status").value("Livré"))
                .andExpect(jsonPath("$.order.priceTotale").value(2000.0));
    }

    @Test
    @DisplayName("PUT /api/ordres/{id} - Mettre à jour une commande (En_cours)")
    void updateCommande_EnCours() throws Exception {

        Commande updatedCommande = new Commande();
        updatedCommande.setId(1L);
        updatedCommande.setStatus(Status.En_cours);
        updatedCommande.setPriceTotale(1800.0);

        when(commandeService.updateCommande(eq(1L), any(Commande.class))).thenReturn(updatedCommande);


        mockMvc.perform(put("/api/ordres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Orders Successufully"))
                .andExpect(jsonPath("$.order.status").value("En_cours"))
                .andExpect(jsonPath("$.order.priceTotale").value(1800.0));
    }

    @Test
    @DisplayName("PUT /api/ordres/{id} - Mettre à jour une commande (Annulé)")
    void updateCommande_Annule() throws Exception {
        // Arrange
        Commande updatedCommande = new Commande();
        updatedCommande.setId(1L);
        updatedCommande.setStatus(Status.Annulé);
        updatedCommande.setPriceTotale(0.0);

        when(commandeService.updateCommande(eq(1L), any(Commande.class))).thenReturn(updatedCommande);


        mockMvc.perform(put("/api/ordres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommande)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Orders Successufully"))
                .andExpect(jsonPath("$.order.status").value("Annulé"))
                .andExpect(jsonPath("$.order.priceTotale").value(0.0));
    }

    @Test
    @DisplayName("DELETE /api/ordres/{id} - Supprimer une commande")
    void deleteCommande_Success() throws Exception {

        doNothing().when(commandeService).deleteCommande(1L);


        mockMvc.perform(delete("/api/ordres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("order delete with success"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/ordres/{id} - Commande non trouvée")
    void deleteCommande_NotFound() throws Exception {

        doThrow(new com.iteam.Exceptions.NotFoundEntityExceptions("No Orders with the ID: 99"))
                .when(commandeService).deleteCommande(99L);


        mockMvc.perform(delete("/api/ordres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No Orders with the ID: 99"))
                .andExpect(jsonPath("$.error").value("Ressources_Not_Found"));
    }

    @Test
    @DisplayName("POST /api/ordres/create - Créer commande avec différents statuts")
    void createCommande_WithDifferentStatus() throws Exception {

        CreateCommandeRequestDTO requestDTO = new CreateCommandeRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setProductsId(Arrays.asList(1L));

        Commande commandeEnCours = new Commande();
        commandeEnCours.setId(2L);
        commandeEnCours.setDateCommande(LocalDateTime.now());
        commandeEnCours.setStatus(Status.En_cours);
        commandeEnCours.setPriceTotale(1500.0);
        commandeEnCours.setUser(user);
        commandeEnCours.setProducts(Arrays.asList(product));

        when(commandeService.createCommande(eq(1L), eq(Arrays.asList(1L))))
                .thenReturn(commandeEnCours);

        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order create with success"))
                .andExpect(jsonPath("$.order.status").value("En_cours"))
                .andExpect(jsonPath("$.order.priceTotale").value(1500.0));
    }

    @Test
    @DisplayName("GET /api/orders - Liste vide")
    void findAllCommandes_EmptyList() throws Exception {

        when(commandeService.findAll()).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/ordres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @DisplayName("POST /api/orders/create - Vérifier la structure de la réponse")
    void createCommande_ResponseStructure() throws Exception {

        CreateCommandeRequestDTO requestDTO = new CreateCommandeRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setProductsId(Arrays.asList(1L));

        Commande newCommande = new Commande();
        newCommande.setId(5L);
        newCommande.setDateCommande(LocalDateTime.now());
        newCommande.setStatus(Status.Livré);
        newCommande.setPriceTotale(3000.0);

        when(commandeService.createCommande(eq(1L), eq(Arrays.asList(1L))))
                .thenReturn(newCommande);


        mockMvc.perform(post("/api/ordres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.order").exists())
                .andExpect(jsonPath("$.order.id").exists())
                .andExpect(jsonPath("$.order.status").exists())
                .andExpect(jsonPath("$.order.priceTotale").exists());
    }

}