package telran.java51.accounting.controller;

import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java51.accounting.dto.RoleDto;
import telran.java51.accounting.dto.UserCreateDto;
import telran.java51.accounting.dto.UserDto;
import telran.java51.accounting.dto.UserUpdateDto;
import telran.java51.accounting.dto.exceptions.UserForbiddenException;
import telran.java51.accounting.model.User;
import telran.java51.accounting.service.UserService;


@RestController
@RequiredArgsConstructor
//@RequestMapping("/account")
public class UserController {
	
	final UserService userService;
	
	
	@PostMapping("/account/register")
	public UserDto registerUser (@RequestBody UserCreateDto userCreateDto) {

		return userService.registerUser(userCreateDto);
	}
	
	
	@PostMapping("/account/login")
	public UserDto loginUser (Principal principal) {
		
		return userService.findById(principal.getName());
	}
	
	
	@DeleteMapping("/account/user/{user}")
	public UserDto deleteById (@PathVariable("user")  String login) {

			return userService.deleteById(login);

	}

	@PutMapping("/account/user/{user}")
	public UserDto updateUser ( @PathVariable("user")  String login, @RequestBody UserUpdateDto userUpdateDto) {
	
			return userService.updateUser(login, userUpdateDto);

	
	}
	
	@PutMapping("/account/user/{user}/role/{role}")
	public RoleDto addRole(@PathVariable("user")  String login, @PathVariable String role) {
		
			return userService.addRole(login, role);


	}
	
	@DeleteMapping("/account/user/{user}/role/{role}")
	public RoleDto deleteRole(@PathVariable("user")  String login, @PathVariable String role) {
	

			
			return userService.deleteRole(login, role);
	
		
		
	}
	
	@GetMapping("/account/user/{user}")
	public UserDto getUser(@PathVariable("user")  String login) {
	return userService.findById(login);
		
	}
	
	@PutMapping("/account/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(Principal principal, @RequestHeader("X-Password") String newPassword) {
	
			
			userService.changePassword(principal.getName(),newPassword);


	}
	


}
