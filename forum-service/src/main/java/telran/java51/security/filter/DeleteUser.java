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
import telran.java51.security.model.UserAddition;

@Component
//@RequiredArgsConstructor //[1] //Закомментили позже, когда добавили класс UserAddition
@Order(40)

public class DeleteUser implements Filter {

	//final UserRepository userRepository;//[1]
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request =  (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		

		
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			
			//Principal principal = request.getUserPrincipal();//[1]
			String [] array = request.getServletPath().split("/");
			String userName = array[array.length-1];
			
		//	User user = userRepository.findById(request.getUserPrincipal().getName()).get(); //[1]
			
			UserAddition userAddition = (UserAddition) request.getUserPrincipal();
			//if(!(user.getRoles().contains("ADMINISTRATOR") || principal.getName().equalsIgnoreCase(userName))) {//[1]
				if(!(userAddition.getRoles().contains("ADMINISTRATOR") || userAddition.getName().equalsIgnoreCase(userName))) {//[1]
				response.sendError(403, "Permisssion denied");
				return;
			}
			
		}
		
		chain.doFilter(request, response);

	}


	private boolean checkEndPoint(String method, String servletPath) {
		return HttpMethod.DELETE.matches(method)  &&  servletPath.matches("/account/user/\\w+");
	}


	

}
