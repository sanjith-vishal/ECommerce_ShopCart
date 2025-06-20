package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceApplicationTests {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	private User sampleUser;

//	@BeforeEach
//	void setup() {
//		MockitoAnnotations.openMocks(this);
//		sampleUser = new User(1, "Alice", "alice@example.com", "password123", "123 Main St", "VISA 1234");
//	}

	@Test
	void testSaveUser_Success() {
		when(userRepository.save(sampleUser)).thenReturn(sampleUser);

		String result = userService.saveUser(sampleUser);

		assertEquals("User Registered Successfully!", result);
		verify(userRepository, times(2)).save(sampleUser); // save called twice in your logic
	}

	@Test
	void testSaveUser_Failure() {
		when(userRepository.save(sampleUser)).thenReturn(null);

		String result = userService.saveUser(sampleUser);

		assertEquals("User Registration Failed.", result);
	}

	@Test
	void testUpdateUser() {
		when(userRepository.save(sampleUser)).thenReturn(sampleUser);

		User result = userService.updateUser(sampleUser);

		assertEquals(sampleUser, result);
		verify(userRepository).save(sampleUser);
	}

	@Test
	void testGetUserById_Found() {
		when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));

		User result = userService.getUserById(1);

		assertNotNull(result);
		assertEquals("Alice", result.getName());
	}

	@Test
	void testGetUserById_NotFound() {
		when(userRepository.findById(2)).thenReturn(Optional.empty());

		User result = userService.getUserById(2);

		assertNull(result);
	}

//	@Test
//	void testGetAllUsers() {
//		List<User> users = List.of(sampleUser,
//				new User(2, "Bob", "bob@example.com", "pass456", "456 Side St", "MASTERCARD 5678"));
//		when(userRepository.findAll()).thenReturn(users);
//
//		List<User> result = userService.getAllUsers();
//
//		assertEquals(2, result.size());
//		verify(userRepository).findAll();
//	}

	@Test
	void testDeleteUserById_Exists() {
		when(userRepository.existsById(1)).thenReturn(true);

		String result = userService.deleteUserById(1);

		assertEquals("User Deleted Successfully!", result);
		verify(userRepository).deleteById(1);
	}

	@Test
	void testDeleteUserById_NotExists() {
		when(userRepository.existsById(3)).thenReturn(false);

		String result = userService.deleteUserById(3);

		assertEquals("User Not Found.", result);
		verify(userRepository, never()).deleteById(3);
	}
}