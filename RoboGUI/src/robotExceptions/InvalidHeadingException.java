package robotExceptions;

public class InvalidHeadingException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2161036224105219101L;

	private String				message;

	public InvalidHeadingException()
	{
		this.message = "Heading is not valid for attempted manuever";

	}

	public InvalidHeadingException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

}
