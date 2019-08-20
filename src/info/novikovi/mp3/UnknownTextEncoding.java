package info.novikovi.mp3;

/**
 * <p>Неизвестная кодировка текста.</p>
 * @see {@link TextEncoding}
 */
public class UnknownTextEncoding extends Exception
{
	private static final long serialVersionUID = 8656422865653297350L;
	
	public UnknownTextEncoding(String msg)
	{
		super(msg);
	}

}
