package telran.java51.security.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java51.accounting.dao.UserRepository;
import telran.java51.accounting.model.User;

@Component
@RequiredArgsConstructor
@Order(20) //Порядок исполнения фильтра. Ставится через 10 или 100. То есть этот у нас сейчас второй после аутентификации
public class AdminManagingRolesFilter implements Filter {

	
	final UserRepository userRepository;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request =  (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			
			/*
			 * В строчке ниже findById() возввращает Optional, но мы уверены, что null быть не может, потому что аутентификация (первый фильтр) уже пройден.
			 * Поэтому в конце мы смело ставим get()
			 */
			
			User user = userRepository.findById(request.getUserPrincipal().getName()).get();
			
			if(!user.getRoles().contains("ADMINISTRATOR")) {
				
				response.sendError(403, "Permisssion denied");
				return;
			}
			
		/*
		 * Чтобы разрешить передавать сообщения с ошибкой нужно в файле applications.properties добавить строчку
		 * 
		 * server.error.include-message=always
		 */
			
		}
		
		chain.doFilter(request, response);

	}

	private boolean checkEndPoint(String method, String servletPath) {
		return servletPath.matches("/account/user/\\w+/role/\\w+");
	}

}
