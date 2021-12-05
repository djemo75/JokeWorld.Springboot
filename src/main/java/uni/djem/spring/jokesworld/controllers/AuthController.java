package uni.djem.spring.jokesworld.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

import uni.djem.spring.jokesworld.RequestDtos.EditCommentRequest;
import uni.djem.spring.jokesworld.RequestDtos.EditUserRequest;
import uni.djem.spring.jokesworld.RequestDtos.LoginRequest;
import uni.djem.spring.jokesworld.RequestDtos.RegisterRequest;
import uni.djem.spring.jokesworld.ResponseDtos.UserDetailsResponse;
import uni.djem.spring.jokesworld.entities.CommentEntity;
import uni.djem.spring.jokesworld.entities.UserEntity;
import uni.djem.spring.jokesworld.repositories.UserRepository;

@RequestMapping(path = "/auth")
@RestController
public class AuthController {
	private UserRepository userRepository;
	
	public AuthController(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	@PostMapping(path = "/login")
	public ResponseEntity<UserDetailsResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) throws Exception {
		if(loginRequest.getUsername()=="" || loginRequest.getPassword()=="") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You need to provide username and password!");
		}
		
		UserEntity user = userRepository.findUserByUsernameAndPassword(loginRequest.getUsername(), hashMe(loginRequest.getPassword()));
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username or password is wrong!");
		}
		
		session.setAttribute("user", user);
		UserDetailsResponse response = new UserDetailsResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName());
		
		return new ResponseEntity<UserDetailsResponse>(response, HttpStatus.OK);
	}
	
	@PostMapping(path = "/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest, HttpSession session) throws Exception {
		UserEntity user = userRepository.findByUsername(registerRequest.getUsername());
		
		if(registerRequest.getUsername()=="" || registerRequest.getPassword()=="" ||
		   registerRequest.getFirstName()==""	|| registerRequest.getLastName()=="") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You need to provide content for required fields!");
		}
		
		if(user!=null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Username exist!");
		}
		
		UserEntity newUser = new UserEntity(registerRequest.getUsername(),
											hashMe(registerRequest.getPassword()),
											registerRequest.getFirstName(),
											registerRequest.getLastName());
		userRepository.saveAndFlush(newUser);
		return new ResponseEntity<String>("You have successfully registered!", HttpStatus.OK);
	}
	
	@PostMapping(path="/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();
		return new ResponseEntity<String>("You have successfully logged out!", HttpStatus.OK);
	}
	
	@GetMapping(path = "/profile")
	public ResponseEntity<UserDetailsResponse> profile(HttpSession session) {
		UserEntity userSession = (UserEntity) session.getAttribute("user");
		
		if(userSession==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		UserEntity user = userRepository.findByUsername(userSession.getUsername());
		
		UserDetailsResponse response = new UserDetailsResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName());
		
		return new ResponseEntity<UserDetailsResponse>(response, HttpStatus.OK);
	}
	
	@PutMapping("/edit-profile")
	public ResponseEntity<String> editUser(@RequestBody EditUserRequest userRequest, HttpSession session) {
		UserEntity userSession = (UserEntity)session.getAttribute("user");
		
		if(userSession==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		UserEntity user = userRepository.findByUsername(userSession.getUsername());
		
		if(userRequest.getFirstName()=="" || userRequest.getLastName()=="") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to provide first and last name!");
		}
		
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		
		userRepository.save(user);
		
		return new ResponseEntity<String>("You have successfully edited the profile", HttpStatus.OK);
	}
	
	@DeleteMapping("/close-account")
	public ResponseEntity<String> closeAccount(@RequestParam int id, HttpSession session) {
		UserEntity user = (UserEntity)session.getAttribute("user");
		
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to login!");
		}
		
		if(id!=user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can close only your account!");
		}
		
		userRepository.delete(user);
		session.invalidate();
		
		return new ResponseEntity<String>("You have successfully closed you account", HttpStatus.OK);
	}
	
	private String hashMe(String password) {		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.update(password.getBytes());
			
			byte[] arr = md.digest();
			
			StringBuilder hash = new StringBuilder();
			
			for(int i = 0; i < arr.length; i++) {
				hash.append((char)arr[i]);
			}
			
			return hash.toString();
			
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}		
				
		return null;
	}
}
