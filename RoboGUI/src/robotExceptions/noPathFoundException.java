package robotExceptions;

public class noPathFoundException extends Exception {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2161036224105219101L;

	private String				message;

	public noPathFoundException()
	{
		this.message = "No path found for robot.";

	}

	public noPathFoundException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}
}
