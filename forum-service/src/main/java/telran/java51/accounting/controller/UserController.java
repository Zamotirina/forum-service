package telran.java51.accounting.controller;

import java.security.Principal;
import java.util.Base64;

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
	
	/*
	 * В этом методе мы рассматриваем вариант того, как можно работать с авторизацией 
	 * 
	 * В Postman этот метод запрос отправляет еще и набор Headers, один из заголовков Authorization со значением Basic SmF2YUZhbjoxMjM0
	 * 
	 * В SmF2YUZhbjoxMjM0 зашифрованы логин и пароль, а Basic - это указание на тип шифрования
	 * 
	 * В теории мы можем не придумывать ничего хитрого и считывать этот заголовок примерно так же, как и предыдущие с помощью аннотаций @PathVariable и @RequestBody
	 * 
	 * То есть мы добавляем аннотацию @RequestHeader
	 * 
	 * Важно: Но так делать не надо, потому что у нас тут нет никакой безопасности. Мы не проверяем пароль
	 * Эдурад нам это показал только, чтобы показать, что так можно работать с заголовками. И в крайнем случае можно что-то так вытащить.
	 */
	
	@PostMapping("/account/login")
	public UserDto loginUser (@RequestHeader("Authorization") String token) {
		
		token= token.split(" ") [1]; //Таким образом мы делим строку Basic SmF2YUZhbjoxMjM0 по пробелу и получаем первый (не нулевой) элемент из возвращенного массива. То есть сохраняем только SmF2YUZhbjoxMjM0
		
		/*
		 * Класс Base64 нужен в Java, чтобы зашифровывать и расшифровывать такие штуки
		 * 
		 * У него есть методы getDecoder() и getEncoder()
		 * 
		 * Метод getDecoder() расшифровывает заданную строку и возвращает массив байт
		 * 
		 * Чтобы превратить массив байт в стринг, мы используем конструктор String, который принимает массив байт
		 * 
		 * Таким образом мы получаем расшифрованную строку, которая собой представляет собой пару login:password
		 */
		String credentials= new String(Base64.getDecoder().decode(token));
		
		
		/*
		 * Так мы выковыриваем логин (первым элементом при этом в массиве будет пароль) и прокидываем его в метод
		 */
		
		return userService.findById(credentials.split(":") [0]);
	}
	
	@DeleteMapping("/account/user/{user}")
	public UserDto deleteById (@PathVariable ("user")  String login) {
		return userService.deleteById(login);
	}
	
	@PutMapping("/account/user/{user}")
	public UserDto updateUser (@PathVariable("user")  String login, @RequestBody UserUpdateDto userUpdateDto) {
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
