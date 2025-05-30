package com.selimhorri.app.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.UserRepository;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .imageUrl("http://example.com/image.jpg")
                .credentialDto( new CredentialDto()) // Assuming no credential for simplicity
                .addressDtos(java.util.Collections.emptySet()) // Assuming no addresses for simplicity
                .build();
        UserDto userDto2 = UserDto.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Doe")
                .email("jame@example.com")
                .phone("0987654321")
                .imageUrl("http://example.com/image.jpg")
                .credentialDto( new CredentialDto()) // Assuming no credential for simplicity
                .addressDtos(java.util.Collections.emptySet()) // Assuming no addresses for simplicity
                .build();

        when(userRepository.findById(1)).
                thenReturn(Optional.of(UserMappingHelper.map(userDto)));
        when(userRepository.findAll()).
                thenReturn(List.of(UserMappingHelper.map(userDto), UserMappingHelper.map(userDto2)));

        when(userRepository.findById(3)).
                thenReturn(Optional.empty());
        
     when(userRepository.save(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    }

    @Test
    public void getAllUsersWithfindAll() {

        List<UserDto> userDtos = userService.findAll();
        assertNotNull(userDtos);
        assertFalse(userDtos.isEmpty());
        assertEquals(2, userDtos.size());
    }

    @Test
    public void getUserByIdWithfindbyId() {
        Integer userId = 1;
        UserDto userDto = userService.findById(userId);
        assertNotNull(userDto);
        assertEquals(userId, userDto.getUserId());
    }

    @Test
    public void getUserByIdWithfindbyIdNotFound() {
        Integer userId = 3;
        assertThrows(UserObjectNotFoundException.class, () -> {
            userService.findById(userId);
        });
    }

    @Test
    public void saveUserWithSave() {
        UserDto userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .imageUrl("http://example.com/image.jpg")
                .credentialDto( new CredentialDto()) // Assuming no credential for simplicity
                .addressDtos(java.util.Collections.emptySet()) // Assuming no addresses for simplicity
                .build();
        
        UserDto savedUser = userService.save(userDto);
        assertNotNull(savedUser);
        assertEquals(userDto.getUserId(), savedUser.getUserId());
    }

    @Test
    public void updateUserWithUpdate() {
        UserDto userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doeee")
                .email("john1@example.com")
                .phone("1234567890")
                .imageUrl("http://example.com/image.jpg")
                .credentialDto( new CredentialDto()) // Assuming no credential for simplicity
                .addressDtos(java.util.Collections.emptySet()) // Assuming no addresses for simplicity
                .build();
        UserDto updatedUser = userService.update(userDto);
        assertNotNull(updatedUser);
        assertEquals(userDto.getUserId(), updatedUser.getUserId());
        assertEquals(userDto.getLastName(), updatedUser.getLastName());
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
    }

}