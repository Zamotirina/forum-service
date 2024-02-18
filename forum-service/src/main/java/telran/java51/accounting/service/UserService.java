package telran.java51.accounting.service;

import telran.java51.accounting.dto.RoleDto;
import telran.java51.accounting.dto.UserCreateDto;
import telran.java51.accounting.dto.UserDto;
import telran.java51.accounting.dto.UserUpdateDto;

public interface UserService {

	UserDto findById (String login);
	UserDto registerUser (UserCreateDto userCreateDto);
	UserDto deleteById (String login);
	UserDto updateUser (String login, UserUpdateDto userUpdateDto);
	RoleDto addRole (String login, String role);
	RoleDto deleteRole (String login, String role);
	void changePassword (String login, String password);
	
}
