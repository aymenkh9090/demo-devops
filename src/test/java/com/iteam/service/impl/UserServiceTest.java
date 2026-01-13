package com.iteam.service.impl;

import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.User;
import com.iteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaire de User service")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

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
    @DisplayName("Doit creer un utilisateur avec succes")
    void createUser_Succes() {
        //Arrange //Préparer les données et configurer les mocks
        when(userRepository.save(any(User.class))).thenReturn(user1);
        //Act // tester methode
        User result = userService.createUser(user1);
        //Assert verifier le resultat obteunu
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("aymen");
        verify(userRepository,times(1)).save(user1);

    }

    @Test
    @DisplayName("Doit retourner toutes les utilisateurs")
    void findAll_Succes() {

        List<User> users = Arrays.asList(user1, user2);
        //Arrange
        when(userRepository.findAll()).thenReturn(users);
        //Act
        List<User> result = userService.findAll();
        //Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(user1);
        assertThat(result.get(1).getFirstName()).isEqualTo("wajdi");
    }

    @Test
    @DisplayName("Doit retourner un utilisateur par son Id")
    void findUserById_Succes() {
        //Assert
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        //Act
        User result = userService.findUserById(1L);
        //Arrange
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("aymen");
        verify(userRepository,times(1)).findById(1L);
    }

    @Test
    @DisplayName("Doit retourner une exception si utilisateur n'est pas trouvé")
    void findUserById_NotFound_Throw_Exception() {

        //Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        //Act&Assert
        assertThatThrownBy(()->userService.findUserById(99L))
                .isInstanceOf(NotFoundEntityExceptions.class)
                .hasMessage("No User present with the ID: 99");
    }

    @Test
    @DisplayName("Doit supprimer un utilisateur")
    void deleteUserById_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).delete(any(User.class));


        userService.deleteUserById(1L);


        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    @DisplayName("Doit mettre à jour un utilisateur existant")
    void updateUser_Success() {

        User userToUpdate = new User();
        userToUpdate.setFirstName("Ahmed Updated");
        userToUpdate.setLastName("New Lastname");
        userToUpdate.setEmail("new@email.com");
        userToUpdate.setPhoneNumber("99999999");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);


        User result = userService.updateUser(1L, userToUpdate);


        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Ahmed Updated");
        verify(userRepository, times(1)).save(any(User.class));
    }
}