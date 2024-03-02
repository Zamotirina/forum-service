package telran.java51.security.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import telran.java51.security.model.UserAddition;

@Component // Тоже дделаем на шаге [2] Помещаем этот класс в апликационный контекст Spring
public class SecurityContextInterfaceImpl implements SecurityContextInterface {

	/*
	 * [2]. Этот класс - это имплементация наего интерфейса для работы с куками
	 * 
	 * В нем в мапе мы будем хранить пары ключ-значение, где ключ - это id сессии, а значение userAddition, то есть наш request, который мы обернули в WrappedRequest и сначалаи добаили к нему принципал (логин), а потом еще и роли для авторизации
	 */
	private Map <String, UserAddition> context = new ConcurrentHashMap<>(); //Делаем потоко-безопасную мапу для хранения каких сессий и юзеров под номерами этих сессий
	
	@Override
	public UserAddition addUserSession(String sessionId, UserAddition userAddition) {
	
		return context.putIfAbsent(sessionId,userAddition);
		
	}

	@Override
	public UserAddition removeUserSession(String sessionId) {
	
		return context.remove(sessionId);
	}

	@Override
	public UserAddition getUserBySessionId(String sessionId) {

		return context.get(sessionId);
	}

}
