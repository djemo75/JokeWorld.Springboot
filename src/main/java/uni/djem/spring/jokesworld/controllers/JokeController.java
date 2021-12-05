package uni.djem.spring.jokesworld.controllers;

import java.time.Instant;
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

import uni.djem.spring.jokesworld.RequestDtos.CreateJokeRequest;
import uni.djem.spring.jokesworld.RequestDtos.EditJokeRequest;
import uni.djem.spring.jokesworld.entities.JokeEntity;
import uni.djem.spring.jokesworld.entities.UserEntity;
import uni.djem.spring.jokesworld.repositories.JokeRepository;
import uni.djem.spring.jokesworld.repositories.UserRepository;

@RequestMapping(path="/jokes")
@RestController
public class JokeController {
	private JokeRepository jokeRepository;
	
	public JokeController(JokeRepository jokeRepository, UserRepository userRepository) {
		this.jokeRepository=jokeRepository;
	}
	
	@GetMapping("")
	public ResponseEntity<List<JokeEntity>> getJokes(@RequestParam(required = false, defaultValue = "") String category,
			@RequestParam(required = false, defaultValue = "") String phrase) {
		List<JokeEntity> jokes = jokeRepository.findByCategoryContainingAndContentContainingIgnoreCaseOrderByCreatedDateDesc(category, phrase);

		return new ResponseEntity<List<JokeEntity>>(jokes, HttpStatus.OK);
	}
	
	@GetMapping("/")
	public ResponseEntity<JokeEntity> getJokeById(@RequestParam int id) {
		JokeEntity joke = jokeRepository.findById(id);
		
		if(joke==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Joke with this id not found!");
		}
		
		return new ResponseEntity<JokeEntity>(joke, HttpStatus.OK);
	}
	
	@PostMapping("/")
	public ResponseEntity<JokeEntity> createJoke(@RequestBody CreateJokeRequest jokeRequest, HttpSession session){
		UserEntity user = (UserEntity)session.getAttribute("user");
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		if(jokeRequest.getContent()=="" || jokeRequest.getCategory()=="") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to provide content and category!");
		}
		
		JokeEntity joke = new JokeEntity();
		joke.setContent(jokeRequest.getContent());
		joke.setCategory(jokeRequest.getCategory());
		joke.setUser(user);
		joke.setCreatedDate(new Date());
		
		jokeRepository.saveAndFlush(joke);
		return new ResponseEntity<JokeEntity>(joke, HttpStatus.OK);
	}
	
	@PutMapping("")
	public ResponseEntity<String> editJoke(@RequestParam int id, @RequestBody EditJokeRequest jokeRequest, HttpSession session) {
		UserEntity user = (UserEntity)session.getAttribute("user");
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		JokeEntity joke = jokeRepository.findById(id);
		
		if(joke==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Joke with this id not found!");
		}
		
		if(joke.getUser().getId()!=user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't edit other people's jokes!");
		}
		
		if(jokeRequest.getContent()=="" || jokeRequest.getCategory()=="") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to provide content and category!");
		}
		
		joke.setCategory(jokeRequest.getCategory());
		joke.setContent(jokeRequest.getContent());
		
		jokeRepository.save(joke);
		
		return new ResponseEntity<String>("You have successfully edited the joke", HttpStatus.OK);
	}
	
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteJoke(@RequestParam int id, HttpSession session) {
		UserEntity user = (UserEntity)session.getAttribute("user");
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		JokeEntity joke = jokeRepository.findById(id);
		
		if(joke==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Joke with this id not found!");
		}
		
		if(joke.getUser().getId()!=user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't delete other people's jokes!");
		}
		
		jokeRepository.delete(joke);
		
		return new ResponseEntity<String>("You have successfully deleted the joke", HttpStatus.OK);
	}
}
