package uni.djem.spring.jokesworld.RequestDtos;

public class EditJokeRequest {
	private String content;
	private String category;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
