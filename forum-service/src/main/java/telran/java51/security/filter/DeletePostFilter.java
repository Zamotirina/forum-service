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
import telran.java51.security.model.UserAddition;

@Component
@RequiredArgsConstructor 
@Order(50)
public class DeletePostFilter implements Filter {

	final PostRepository postRepository;
	//final UserRepository userRepository;//[1]
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
	
		
		HttpServletRequest request =  (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
	
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			
		UserAddition userAddition = (UserAddition) request.getUserPrincipal();//[1]
		//User user  = userRepository.findById(request.getUserPrincipal().getName()).get();
		String [] array = request.getServletPath().split("/");

		String postId =  array[array.length-1];
		
		/*
		 * Мы здесь делаем именно так, то есть оставляем null, а не отправляем PostNotFoundException, так как иначе
		 * 
		 *  система вернет не 404 ошибку, а 500, так как фильтры находятся до котроллера
		 *  
		 *  Поэтому любой Exception, который прерывает работу фильтра и не дает дойти дальше, будет трасформирован томкатом как 500-ая ошибка
		 *  
		 *  Соответственно надо делать так, либо добавлять try -catch и отлавливать одну ошибку и отправлять через response.send() другую. Мы так сделалаи в классе AuthenticationFilter
		 */
 	
		Post post = postRepository.findById(postId).orElse(null);
		
		if(post==null) {
			
			response.sendError(404);
			
			return;
		}
				
	//	if(!(user.getRoles().contains("MODERATOR") || user.getLogin().equalsIgnoreCase(post.getAuthor()))) { //[1]
			
			if(!(userAddition.getRoles().contains("MODERATOR") || userAddition.getName().equalsIgnoreCase(post.getAuthor()))) { //[1]

			response.sendError(403, "Permisssion denied");
			return;
		}
		}
		
		chain.doFilter(request, response);

	}
	

	private boolean checkEndPoint(String method, String servletPath) {
		return HttpMethod.DELETE.matches(method)  &&  servletPath.matches("/forum/post/\\w+");
	}
}
