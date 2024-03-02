package telran.java51.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.Spring;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.core.annotation.Order;
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
import telran.java51.security.context.SecurityContextInterface;
import telran.java51.security.model.UserAddition;

@Component
@RequiredArgsConstructor
@Order(10)
public class AuthenticationFilter implements Filter {

	final UserRepository userRepository;
	final SecurityContextInterface securityContextInterface; // [2]

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		/*
		 * [2] Учимся работать с куками
		 * 
		 * Для этого запрашиваем id сессии
		 */
//		String sessionId = request.getSession().getId();
//		
//		System.out.println(sessionId);//611D2DF07802038C5D0C1F58CA0996FE

		if (checkEndPoint(request.getMethod(), request.getServletPath())) {

			String sessionId = request.getSession().getId();// [2]

			UserAddition userAddition = securityContextInterface.getUserBySessionId(sessionId);// [2]

//			try {
//
//				String[] credentials = getCredentials(request.getHeader("Authorization"));
//				User user = userRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
//
//				if (!BCrypt.checkpw(credentials[1], user.getPassword())) {
//
//					throw new RuntimeException();
//				}
//				
//				/*
//				 * 
//				 * //[1] Строчку ниже тоже отредактировали позже, когда написали класс UserAddidion
//				 * дополнили иннер-класс WrappedRequest и дополнили его конструктор
//				 */
			// request=new WrappedRequest(request,
			// user.getLogin(),user.getRoles().stream().map(x->x.name()).collect(Collectors.toSet()));

//								
//			} catch (Exception e) {
//
//				response.sendError(401);
//				return;
//			} 
//			
//	
//		}
//		
//
//
//		
//		chain.doFilter(request, response);

			
			
			
			if (userAddition==null) {
				try {

					String[] credentials = getCredentials(request.getHeader("Authorization"));
					User user = userRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);

					if (!BCrypt.checkpw(credentials[1], user.getPassword())) {

						throw new RuntimeException();
					}

					userAddition = new UserAddition(user.getLogin(), user.getRoles().stream().map(x -> x.name()).collect(Collectors.toSet()));
					securityContextInterface.addUserSession(sessionId, userAddition);
					
				} catch (Exception e) {

					response.sendError(401);
					return;
				} 
			}
	//		request = new WrappedRequest(request, user.getLogin(),
	//				user.getRoles().stream().map(x -> x.name()).collect(Collectors.toSet()));

			request = new WrappedRequest(request, userAddition.getName(),
					userAddition.getRoles());

			
		}

		chain.doFilter(request, response);

	}

	private boolean checkEndPoint(String method, String servletPath) {

//		return !((HttpMethod.POST.matches(method) && servletPath.matches("/account/register")) || 
//				(HttpMethod.POST.matches(method)&&servletPath.contains("/forum/posts/")) ||
//						(HttpMethod.GET.matches(method)&&servletPath.contains("/forum/posts/")));

		return !((HttpMethod.POST.matches(method) && servletPath.matches("/account/register")) ||

				servletPath.matches("/forum/posts/\\w+(/\\w+)?"));

	}

	private String[] getCredentials(String header) {

		String token = header.split(" ")[1];
		String decodedToken = new String(Base64.getDecoder().decode(token));

		return decodedToken.split(":");
	}

	private class WrappedRequest extends HttpServletRequestWrapper {

		private String login;

		private Set<String> roles;// [1] Добавили позже, после того как прописали класс UserAddition, чтобы
									// оптимизировать код и меньше лазать в базу данных с запросами

		public WrappedRequest(HttpServletRequest request, String login, Set<String> roles) {
			super(request);

			this.login = login;
			this.roles = roles; // [1] Тоже добавили позже
		}

		@Override
		public Principal getUserPrincipal() {

			// return () -> login; //[1]

			return new UserAddition(login, roles);
		}

	}

}
