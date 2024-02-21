package telran.java51.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import javax.swing.Spring;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java51.accounting.dao.UserRepository;
import telran.java51.accounting.dto.exceptions.UserNotFoundException;
import telran.java51.accounting.model.User;

/*
 * Этот класс мы используем, чтобы написать свою собственную версию аутентификации на сайте
 * 
 * Вообще у Spring есть целый отдельный сервис Spring Security, но там опять работает чистая магия
 * 
 * Чтобы понять, как магия работает мы пишем собственную версию фильтров.
 * 
 * То есть под капотом у Spring Security работают те же фильтры
 * 
 */

/*
 * 1. Класс должен имплементировать интерфейс Filter библиотеки Jakarta
 * 
 * После этого у класса появляется метод doFilter(),
 * который принимает запрос, ответ на запрос и цепочку фильтров
 */

@Component //Сообщает Springу, что этот класс тоже надо учитывать
@RequiredArgsConstructor//Generates a constructor with required arguments.Required arguments are final fields and fields with constraints such as @NonNull. 
public class AuthenticationFilter implements Filter {

	final UserRepository userRepository;
	
	/*
	 * 2. Мы делаем кастинг любых сервлет-запросов до HTTTP-запросов, так как у нас именно http
	 */
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//
//	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		/*
		 * 3. Добавляем к классу аннотацию @Component, чтобы зарегистрировать bean и наш Spring вообще узнал о существвоании этого класса.
		 */
		
		/*
		 * 4. request  - это у нас объект запроса, из него мы можем вытащить кучу информации: метод (Ex GET), путь (Ex /account/user/JavaFan), Headers и пр
		 * 
		 * Чтобы сделать систему безопасной, нам нужно достать из request логин и пароль, проверить, что они такие же в базе и пропустить или не пропустить дальше
		 * 
		 * Создаем массив для пароля и логина
		 * 
		 * В отдельном методе прописываем обратотку заголовка.
		 * 
		 * То есть мы получаем заголовок, отделяем Base, оставляем только цепочку символов, в которой зашифрованы логин и пароль в формате login:password
		 * 
		 * Превращаем их в массив и получаем масссив с логином и паролем
		 */
		
		//String [] credentials = getCredentials(request.getHeader("Authorization"));
		
		/*
		 * 5. Добавляем ссылку на базу данных в поле класса final UserRepository userRepository;
		 *  и добавляем аннотацию @RequeredArgsConstructor
		 */
		
		//User user = userRepository.findById(credentials[0]).orElseThrow(UserNotFoundException::new);
		
		/*
		 * 6. С помощью BCrypt и его метода checkpw() (check password) сравниваем введенный пароль и пароль в базе
		 */
//		if(!BCrypt.checkpw(credentials[1], user.getPassword())) {
//			
//			response.sendError(401); //Так при несовпадении пароля отправляем 401 ошибку
//			return; //Написать этот return ОЧЕНЬ ВАЖНО, так как именно он не позволяет пройти пользователю дальше
//		}
		
		/*
		 * 7. Весь предудщий код закемменчу и оберну в try-catch с отловкой вообще любой ошибку RunTimeException
		 * 
		 * Таким образом при абсолютно любой ошибке мы будем отправлять 401 и не допускать пользователя дальше
		 * 
		 * Иначе, например, при введение несуществующего пользователя Postman выдывал ошибку 500 Internal Server Error, потому что условно YserNotFoundException у нас существует внутри Spring-а, а мы тут еще не добрались до сервлета и значит до Spring-а, то есть для системы это срабатывает как некорректный запрос, а не как НЕпрохождение фильтра безопасности
		 * 
		 * Теперь при любой ошибке аутентификации у нас будет вылезать 401
		 */
		
		/*
		 * 8. Теперь весь try catch мы заворачиваем в if, благодаря которому исключим несколько endpoint-ов
		 * 
		 * Например, без этого if у нас пользователь, не прошедший аутентификацию, не сможет зерегистрироваться. То есть ни один новый пользователь не сможет загеристрироваться
		 * 
		 * То есть часть запросов (endpoint-ов) должна быть доступна абсолютно всем
		 * 
		 * Пишем метод checkEndPoint(), который принимает метод (ex GET) и path (ex /register)
		 */
		
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			try {

				String[] credentials = getCredentials(request.getHeader("Authorization"));
				User user = userRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);

				if (!BCrypt.checkpw(credentials[1], user.getPassword())) {

					throw new RuntimeException();
				}
				
				
				/*
				 * 9. Создаем объект Princical после аутентификации
				 * 
				 * Для этого создаем иннер-класс
				 */
				
				request=new WrappedRequest(request, user.getLogin());
				
			} catch (Exception e) {

				response.sendError(401);
				return;
			} 
			
	
		}
		

		
		
		/*
		 * 10. У объекта chain вызывает метод doFilter()
		 * 
		 * Если мы его не вызовем, то все наши запросы дальше по цепочке не пройдут и не попадут в сервлет
		 */
		
		chain.doFilter(request, response);
		
	}

	private boolean checkEndPoint(String method, String servletPath) {
		
		return !(HttpMethod.POST.matches(method) && servletPath.matches("/account/register"));//Здесь у нас регулярное выражение
	}

	private String[] getCredentials(String header) {
		
		String token = header.split(" ") [1];
		String decodedToken = new String(Base64.getDecoder().decode(token));
		
		return decodedToken.split(":");
	}
	
	private class WrappedRequest extends HttpServletRequestWrapper {
		
		private String login;
		
		public WrappedRequest(HttpServletRequest request, String login) {
			super(request);
			
			this.login=login;
		}
		
		@Override
		public Principal getUserPrincipal() {
			
			return () -> login;
		}

	}

}
