package info.novikovi.mp3.id3v2;

/**
 * <p>Некорректный размер резерва.</p>
 */
public class WrongPaddingSize extends IllegalArgumentException
{
	private static final long serialVersionUID = 7250342178348259027L;

	public WrongPaddingSize(String message)
	{
		super(message);
	}
}
