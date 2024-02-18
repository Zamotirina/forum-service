package telran.java51.accounting.dao;

import org.springframework.data.repository.CrudRepository;

import telran.java51.accounting.model.User;

public interface UserRepository extends CrudRepository<User, String> {

}
