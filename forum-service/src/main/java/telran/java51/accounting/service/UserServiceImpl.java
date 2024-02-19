package telran.java51.accounting.service;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java51.accounting.dao.UserRepository;
import telran.java51.accounting.dto.RoleDto;
import telran.java51.accounting.dto.UserCreateDto;
import telran.java51.accounting.dto.UserDto;
import telran.java51.accounting.dto.UserExistsException;
import telran.java51.accounting.dto.UserNotFoundException;
import telran.java51.accounting.dto.UserUpdateDto;
import telran.java51.accounting.model.User;
import telran.java51.post.dao.PostRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	final UserRepository userRepository;
	final ModelMapper modelMapper;
	
	@Override
	public UserDto findById(String login) {
	
		User user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);
		return modelMapper.map(user, UserDto.class);
	}

	
	/*
	 * В этом методе мы хэшируем пароль, поскольку мы сохраняем его в базу данных. 
	 * То есть пароль должен быть известен только пользователю, а в бузу сохраняться хэшированным 
	 */
	@Override
	public UserDto registerUser(UserCreateDto userCreateDto) {
		
		if(userRepository.existsById(userCreateDto.getLogin())) {
			
			throw new UserExistsException();
		}
		
		User user = modelMapper.map(userCreateDto, User.class);
		
		/*
		 * Делаем это мы с помощью BCrypt и его метода hashpw() (то есть hash password)
		 * 
		 * Метод принимает сам пароль salt - это ключ криптования, который мы тут же генерируем с помощью BCrypt.gensalt()
		 */
		
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

		user.setPassword(password);
		
		userRepository.save(user);
		
	}

}
