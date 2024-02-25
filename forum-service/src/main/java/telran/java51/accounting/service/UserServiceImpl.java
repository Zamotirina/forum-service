package telran.java51.accounting.service;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java51.accounting.dao.UserRepository;
import telran.java51.accounting.dto.RoleDto;
import telran.java51.accounting.dto.UserCreateDto;
import telran.java51.accounting.dto.UserDto;
import telran.java51.accounting.dto.UserUpdateDto;
import telran.java51.accounting.dto.exceptions.UserExistsException;
import telran.java51.accounting.dto.exceptions.UserNotFoundException;
import telran.java51.accounting.model.User;
import telran.java51.post.dao.PostRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, CommandLineRunner {
	
	/*
	 * Интерфейс CommandLineRunner мы добавили позже, чтобы имет возможность добавить администратора, который бы смог раздать первые роли
	 * 
	 * Иначе мы попадали в тупик, когда роли на форуме может раздавать только администратор, а администратора у нас еще и нет
	 * 
	 * В целом его можно реализовать в разных частях приложения. 
	 * 
	 * Но его смысл в том, что у него есть метод run(), который запускается сразу после создания аппликационного контекста.
	 * 
	 * В нашем случае (сам метод прописан ниже), этот метод запускается, проверяет, если ли среди нагих пользователей уже пользователь с логином admin
	 * 
	 * Если такого пользователя нет, то он его добавляет и дает ему роли администратора и модератора
	 */

	final UserRepository userRepository;
	final ModelMapper modelMapper;
	
	@Override
	public UserDto findById(String login) {
	
		User user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);
		return modelMapper.map(user, UserDto.class);
	}

	
	@Override
	public UserDto registerUser(UserCreateDto userCreateDto) {
		
		if(userRepository.existsById(userCreateDto.getLogin())) {
			
			throw new UserExistsException();
		}
		
		User user = modelMapper.map(userCreateDto, User.class);
		
		
		String password = BCrypt.hashpw(userCreateDto.getPassword(), BCrypt.gensalt());
		user.setPassword(password);
		userRepository.save(user);
		
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto deleteById(String login) {
		
		User user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);
		
		userRepository.delete(user);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserUpdateDto userUpdateDto) {
		
		User user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);

		user.setFirstName(userUpdateDto.getFirstName());
		user.setLastName(userUpdateDto.getLastName());
		
		userRepository.save(user);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public RoleDto addRole(String login, String role) {
		
		User user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);

		user.addRole(role);
		
		userRepository.save(user);
	
		return modelMapper.map(user, RoleDto.class);
	}

	@Override
	public RoleDto deleteRole(String login, String role) {
		User user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);

		user.deleteRole(role);
		
		userRepository.save(user);
	
		return modelMapper.map(user, RoleDto.class);
	}

	@Override
	public void changePassword(String login, String password) {
	
		User user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);

		String newPassword = BCrypt.hashpw(password, BCrypt.gensalt());

		user.setPassword(newPassword);
		
		userRepository.save(user);
		
	}


	@Override
	public void run(String... args) throws Exception {
		
		if(!userRepository.existsById("admin")) {
			
			String password = BCrypt.hashpw("admin", BCrypt.gensalt());
			User user = new User("admin", password,"","");
			user.addRole("ADMINISTRATOR");
			user.addRole("MODERATOR");
			userRepository.save(user);
		}
		
	}

}
