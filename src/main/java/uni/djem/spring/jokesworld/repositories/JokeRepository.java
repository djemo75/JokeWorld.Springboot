package uni.djem.spring.jokesworld.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uni.djem.spring.jokesworld.entities.JokeEntity;

@Repository
public interface JokeRepository extends JpaRepository<JokeEntity, Integer> {
	JokeEntity findById(int id);
	List<JokeEntity> findByCategoryContainingAndContentContainingIgnoreCaseOrderByCreatedDateDesc(String category, String content);
	List<JokeEntity> findByUserUsernameContaining(String username);
}
