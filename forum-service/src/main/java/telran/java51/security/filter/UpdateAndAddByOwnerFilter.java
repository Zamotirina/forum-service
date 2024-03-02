package telran.java51.security.filter;

import java.io.IOException;
import java.security.Principal;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

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
@Order(30)
public class UpdateAndAddByOwnerFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
	
		
		HttpServletRequest request =  (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
	
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
		
		Principal principal = request.getUserPrincipal();
		String [] array = request.getServletPath().split("/");
		String user = array[array.length-1];
	
		if(!principal.getName().equalsIgnoreCase(user)) {
			
			response.sendError(403, "Permisssion denied");
			return;
		}
		}
		
		chain.doFilter(request, response);

	}
	
	/*
	 * Этим методом мы делаем так, чтобы
	 * (1) обновить пост мог только пользователь
	 * (2) добавить пост мог только автор добавляемого поста
	 * (3) оставить комментарий мог только автор добавляемого комментария
	 */

	private boolean checkEndPoint(String method, String servletPath) {
		return (HttpMethod.PUT.matches(method)  &&  servletPath.matches("/account/user/\\w+"))
				|| (HttpMethod.POST.matches(method)  &&  servletPath.matches("/forum/post/\\w+"))
				|| (HttpMethod.PUT.matches(method)  &&  servletPath.matches("/forum/post/\\w+/comment/\\w+"));
	}
}
