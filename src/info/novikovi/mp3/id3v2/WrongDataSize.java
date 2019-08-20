package info.novikovi.mp3.id3v2;

/**
 * <p>Неверный размер данных</p>
 */
public class WrongDataSize extends Exception
{
	private static final long serialVersionUID = -8131631850417039086L;

	public WrongDataSize(String message)
	{
		super(message);
	}
}
