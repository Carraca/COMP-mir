package mir;

public class SemanticException extends Exception {
	public String message;
	public Integer line;
	
	public SemanticException(String cause, int line) {
		this.line = line;
		if(line == -1) {
			message = String.format("Semantic error: %s", cause, cause);
		} else {
			message = String.format("Semantic error (line %d): %s", line, cause);
		}
	}
	
	@Override
	public String getMessage() {
		if(message == null) {
			return super.getMessage();
		}
		
		return message;
	}
}
