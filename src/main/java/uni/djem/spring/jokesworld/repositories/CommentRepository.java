package uni.djem.spring.jokesworld.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uni.djem.spring.jokesworld.entities.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
	CommentEntity findById(int id);
	List<CommentEntity> findByJokeIdOrderByCreatedDateDesc(int id);
}
