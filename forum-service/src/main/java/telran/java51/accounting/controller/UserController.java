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
	List <String> roles=List.of("ADMINISTRATOR", "MODERATOR");
	
	
	@PostMapping("/account/register")
	public UserDto registerUser (@RequestBody UserCreateDto userCreateDto) {

		return userService.registerUser(userCreateDto);
	}
	
	@PostMapping("/account/login")
	public UserDto loginUser (Principal principal) {
		
		return userService.findById(principal.getName());
	}
	
	
	@DeleteMapping("/account/user/{user}")
	public UserDto deleteById (Principal principal, @PathVariable("user")  String login) {
	
		if(checkRole(principal, roles.subList(0,1)) || checkLogin(principal.getName(), login)) {
			return userService.deleteById(login);
		} else {
			
			throw new UserForbiddenException();
		}
	}

	@PutMapping("/account/user/{user}")
	public UserDto updateUser (Principal principal, @PathVariable("user")  String login, @RequestBody UserUpdateDto userUpdateDto) {
		
		if(checkLogin(principal.getName(), login)) {
			return userService.updateUser(login, userUpdateDto);
		} else {
			
			throw new UserForbiddenException();
		}
	
	}
	
	@PutMapping("/account/user/{user}/role/{role}")
	public RoleDto addRole(Principal principal, @PathVariable("user")  String login, @PathVariable String role) {
		
		if(checkRole(principal, roles.subList(0,1))) {
			return userService.addRole(login, role);
		} else {
			
			throw new UserForbiddenException();
		}

	}
	
	@DeleteMapping("/account/user/{user}/role/{role}")
	public RoleDto deleteRole(Principal principal,@PathVariable("user")  String login, @PathVariable String role) {
	
		
		if(checkRole(principal, roles.subList(0,1))) {
			
			return userService.deleteRole(login, role);
		} else {
			
			throw new UserForbiddenException();
		}
		
		
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
	
	
	
	private boolean checkRole(Principal principal, List <String> roles) {
		
		UserDto userDto = userService.findById(principal.getName());
		
		return roles.stream().anyMatch(x->userDto.getRoles().contains(x));

	}
	
	private boolean checkLogin(String name, String login) {
		
		return name.equalsIgnoreCase(login);
	}

}
