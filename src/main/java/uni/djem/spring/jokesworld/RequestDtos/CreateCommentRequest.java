package uni.djem.spring.jokesworld.RequestDtos;

public class CreateCommentRequest {
	private String content;
	private int jokeId;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getJokeId() {
		return jokeId;
	}

	public void setJokeId(int jokeId) {
		this.jokeId = jokeId;
	}
}
