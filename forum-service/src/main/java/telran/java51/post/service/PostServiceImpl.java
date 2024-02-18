package telran.java51.post.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.xml.stream.events.Comment;

import org.modelmapper.ModelMapper;
import org.springframework.core.metrics.StartupStep.Tags;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java51.post.dao.PostRepository;
import telran.java51.post.dto.CommentDto;
import telran.java51.post.dto.DatePeriodDto;
import telran.java51.post.dto.NewCommentDto;
import telran.java51.post.dto.NewPostDto;
import telran.java51.post.dto.PostDto;
import telran.java51.post.dto.exceptions.PostNotFoundException;
import telran.java51.post.model.Post;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	
	final PostRepository postRepository;
	final ModelMapper modelMapper;

	@Override
	public PostDto addNewPost(String author, NewPostDto newPostDto) {
		Post post = modelMapper.map(newPostDto, Post.class);
		post.setAuthor(author);
		post = postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto findPostById(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException());
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto removePost(String id) {
		 Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		 postRepository.delete(post);
		return modelMapper.map(post,PostDto.class);
	}

	@Override
	public PostDto updatePost(String id, NewPostDto newPostDto) {
		
		 Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);

		 if(newPostDto.getContent()!=null) {
			 
			 post.setContent(newPostDto.getContent());
			 
		 }
		 
		 if(newPostDto.getTitle() !=null) {
			 
			 post.setTitle(newPostDto.getTitle());
			 
		 }
		 
	 if(newPostDto.getTags()!=null) {
			 
		 newPostDto.getTags().forEach(post::addTag);
			 
			 
			 return modelMapper.map(post, PostDto.class);
		 }
		
		return null;
	}

	@Override
	public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
		
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		
		CommentDto commentDto = new CommentDto(author,newCommentDto.getMessage(),LocalDateTime.now(),0);
		
		telran.java51.post.model.Comment comment = modelMapper.map(commentDto, telran.java51.post.model.Comment.class);
		
		post.addComment(comment);
		
		postRepository.save(post);
		
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public void addLike(String id) {
	
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		
		post.addLike();
		
		postRepository.save(post);

	}

	@Override
	public Iterable<PostDto> findPostByAuthor(String author) {
		
		return postRepository.findAllByAuthorIgnoringCase(author).map(x->modelMapper.map(x,PostDto.class)).toList();
		
	}

	@Override
	public Iterable<PostDto> findPostsByTags(List<String> tags) {
		
		return postRepository.getAllByTagsIn(tags).map(x-> modelMapper.map(x,PostDto.class)).toList();
	}

	@Override
	public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
	
		return postRepository.findAllByDateCreatedBetween(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo()).map(x-> modelMapper.map(x,PostDto.class)).toList();
	}

}
