package telran.java51.accounting.dto;

import java.util.Set;

import org.springframework.data.annotation.Id;

import lombok.Getter;

@Getter
public class RoleDto {


	String login;
	Set <String> roles;
}
