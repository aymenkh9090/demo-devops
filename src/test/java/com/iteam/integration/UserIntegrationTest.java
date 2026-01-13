package com.iteam.integration;

import com.iteam.entities.User;
import com.iteam.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests d'intégration User")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        //userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Scénario complet: CRUD utilisateur")
    void fullCrudScenario() throws Exception {
        // 1. CREATE
        User newUser = new User();
        newUser.setFirstName("Ahmed");
        newUser.setLastName("Ben Ali");
        newUser.setEmail("ahmed@email.com");
        newUser.setPhoneNumber("12345678");

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.firstName").value("Ahmed"));


        assertThat(userRepository.count()).isEqualTo(1);


        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));


        Long userId = userRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ahmed@email.com"));


        User updatedUser = new User();
        updatedUser.setFirstName("Ahmed Updated");
        updatedUser.setLastName("New Lastname");
        updatedUser.setEmail("new@email.com");
        updatedUser.setPhoneNumber("99999999");

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ahmed Updated"));


        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
        assertThat(userRepository.count()).isEqualTo(0);
    }
}
