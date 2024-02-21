package telran.java51.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserForbiddenException extends RuntimeException{

	private static final long serialVersionUID = -4387235531242323494L;

	

}
