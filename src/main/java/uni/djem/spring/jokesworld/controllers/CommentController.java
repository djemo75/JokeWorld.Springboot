package uni.djem.spring.jokesworld.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import uni.djem.spring.jokesworld.RequestDtos.CreateCommentRequest;
import uni.djem.spring.jokesworld.RequestDtos.EditCommentRequest;
import uni.djem.spring.jokesworld.RequestDtos.EditJokeRequest;
import uni.djem.spring.jokesworld.entities.CommentEntity;
import uni.djem.spring.jokesworld.entities.JokeEntity;
import uni.djem.spring.jokesworld.entities.UserEntity;
import uni.djem.spring.jokesworld.repositories.CommentRepository;
import uni.djem.spring.jokesworld.repositories.JokeRepository;

@RequestMapping(path="/comments")
@RestController
public class CommentController {
	private CommentRepository commentRepository;
	private JokeRepository jokeRepository;
	
	public CommentController(CommentRepository commentRepository, JokeRepository jokeRepository) {
		this.commentRepository=commentRepository;
		this.jokeRepository=jokeRepository;
	}
	
	@GetMapping("")
	public ResponseEntity<List<CommentEntity>> getCommentsByJokeId(@RequestParam int jokeId){
		List<CommentEntity> comments = commentRepository.findByJokeIdOrderByCreatedDateDesc(jokeId);
		
		return new ResponseEntity<List<CommentEntity>>(comments,HttpStatus.OK);
	}
	
	@PostMapping("/")
	public ResponseEntity<CommentEntity> createComment (@RequestBody CreateCommentRequest commentRequest, HttpSession session) {
		UserEntity user = (UserEntity)session.getAttribute("user");
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		if(commentRequest.getContent()=="") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to provide content!");
		}
		
		JokeEntity joke = jokeRepository.findById(commentRequest.getJokeId());
		
		if(joke==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found joke with this id!");
		}
		
		CommentEntity comment = new CommentEntity();
		comment.setContent(commentRequest.getContent());
		comment.setUser(user);
		comment.setJoke(joke);
		comment.setCreatedDate(new Date());
		
		commentRepository.saveAndFlush(comment);
		return new ResponseEntity<CommentEntity>(comment, HttpStatus.OK);
	}
	
	@PutMapping("")
	public ResponseEntity<String> editJoke(@RequestParam int id, @RequestBody EditCommentRequest commentRequest, HttpSession session) {
		UserEntity user = (UserEntity)session.getAttribute("user");
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		CommentEntity comment = commentRepository.findById(id);
		
		if(comment==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment with this id not found!");
		}
		
		if(comment.getUser().getId()!=user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't edit other people's comments!");
		}
		
		if(commentRequest.getContent()=="") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to provide content!");
		}
		
		comment.setContent(commentRequest.getContent());
		
		commentRepository.save(comment);
		
		return new ResponseEntity<String>("You have successfully edited the comment", HttpStatus.OK);
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteComment(@RequestParam int id, HttpSession session) {
		UserEntity user = (UserEntity)session.getAttribute("user");
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		CommentEntity comment = commentRepository.findById(id);
		
		if(comment==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment with this id not found!");
		}
		
		if(comment.getUser().getId()!=user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't delete other people's comments!");
		}
		
		commentRepository.delete(comment);
		
		return new ResponseEntity<String>("You have successfully deleted the comment", HttpStatus.OK);
	}
}
