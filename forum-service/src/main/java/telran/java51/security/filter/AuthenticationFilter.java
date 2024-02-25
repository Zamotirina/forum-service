package telran.java51.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

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


@Component
@RequiredArgsConstructor
@Order(10)
public class AuthenticationFilter implements Filter {

	final UserRepository userRepository;
	

	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			
			try {

				String[] credentials = getCredentials(request.getHeader("Authorization"));
				User user = userRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);

				if (!BCrypt.checkpw(credentials[1], user.getPassword())) {

					throw new RuntimeException();
				}
				
		
				
				request=new WrappedRequest(request, user.getLogin());
				
			} catch (Exception e) {

				response.sendError(401);
				return;
			} 
			
	
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
