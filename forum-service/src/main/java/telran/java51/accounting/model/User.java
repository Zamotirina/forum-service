package telran.java51.accounting.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of="login")
@Document(collection = "users")
public class User {
	@Id
	String login;
	@Setter
	String password;
	@Setter
	String firstName;
	@Setter
	String lastName;
	Set <String> roles;
	
	public User() {
		
		this.roles=new HashSet<String>(Set.of("USER"));
	}
	
	public User(String login, String password, String firstName, String lastName) {
		
		this.login=login;
		this.password=password;
		this.firstName=firstName;
		this.lastName=lastName;
		this.roles=new HashSet<String>(Set.of("USER"));
	}
	
	public void addRole (String role) {
		
		this.roles.add(role.toUpperCase());
		
	}
	
	public void deleteRole (String role) {
		
		this.roles.remove(role.toUpperCase());
		
	}

}
