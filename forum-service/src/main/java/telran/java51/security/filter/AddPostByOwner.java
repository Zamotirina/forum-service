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
import telran.java51.post.dao.PostRepository;
import telran.java51.post.dto.exceptions.PostNotFoundException;
import telran.java51.post.model.Post;

@Component
@RequiredArgsConstructor
@Order(60)
public class AddPostByOwner implements Filter {
	
	final PostRepository postRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
	
		
		HttpServletRequest request =  (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
	
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
		
		Principal principal = request.getUserPrincipal();
		String [] array = request.getServletPath().split("/");
		String author = array[array.length-1];
		
	
		if(!principal.getName().equalsIgnoreCase(author)) {
			
			response.sendError(403, "Permisssion denied");
			return;
		}
		}
		
		chain.doFilter(request, response);

	}

	private boolean checkEndPoint(String method, String servletPath) {
		return HttpMethod.POST.matches(method)  &&  servletPath.matches("/forum/post/\\w+");
	}
}
