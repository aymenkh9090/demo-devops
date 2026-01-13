package com.iteam.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.User;
import com.iteam.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Test du controleur UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User user1;
    private User user2;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setFirstName("aymen");
        user1.setLastName("bouraoui");
        user1.setEmail("aymen.bouraoui@gmail.com");
        user1.setPhoneNumber("123456");

        user2 = new User();
        user2.setId(2L);
        user2.setFirstName("wajdi");
        user2.setLastName("ben ameur");
        user2.setEmail("wajdi@gmail.com");
        user2.setPhoneNumber("1234567");
    }

    @Test
    @DisplayName("GET /api/users  - Retourner toutes les utilisateurs")
    void getAllUsers_Users() throws Exception {
        List<User> users = Arrays.asList(user1, user2);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("aymen"))
                .andExpect(jsonPath("$[1].firstName").value("wajdi"));

    }



    @Test
    @DisplayName("POST /api/users/create - Créer un utilisateur")
    void createUser_Success() throws Exception {

        when(userService.createUser(any(User.class))).thenReturn(user1);

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.user.firstName").value("aymen"))
                .andExpect(jsonPath("$.user.email").value("aymen.bouraoui@gmail.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Utilisateur trouvé")
    void getUserById_Found() throws Exception {
        when(userService.findUserById(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("aymen"))
                .andExpect(jsonPath("$.email").value("aymen.bouraoui@gmail.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Utilisateur non trouvé")
    void getUserById_NotFound() throws Exception {
        when(userService.findUserById(99L))
                .thenThrow(new NotFoundEntityExceptions("No User present with the ID: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No User present with the ID: 99"))
                .andExpect(jsonPath("$.error").value("Ressources_Not_Found"))
                .andExpect(jsonPath("$.status").value(404));
    }


    @Test
    @DisplayName("PUT /api/users/{id} - Mise à jour réussie")
    void updateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setFirstName("Ahmed Updated");
        updatedUser.setLastName("New Lastname");
        updatedUser.setEmail("new@email.com");
        updatedUser.setPhoneNumber("99999999");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ahmed Updated"))
                .andExpect(jsonPath("$.email").value("new@email.com"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Suppression")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted with success"))
                .andExpect(jsonPath("$.id").value(1));
    }
}