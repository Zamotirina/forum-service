package telran.java51.security.context;

import telran.java51.security.model.UserAddition;

/*
 *[2] Этот интерфейс мы пишем, чтобы поработать с куками и убрать для наших пользователей авторизацию каждый раз
 */

public interface SecurityContextInterface {

	UserAddition addUserSession (String sessionId, UserAddition userAddition);
	
	UserAddition removeUserSession (String sessionId);
	
	UserAddition getUserBySessionId (String sessionId);
}
