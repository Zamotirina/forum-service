package telran.java51.post.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java51.post.model.Post;

public interface PostRepository extends CrudRepository<Post, String> {

	
	Stream <Post> findAllByAuthorIgnoringCase(String author);
	Stream <Post> getAllByTagsIn(List <String>tags);
	Stream <Post> findAllByDateCreatedBetween (LocalDate dateFrom, LocalDate dateTo);
}
