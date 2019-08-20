package info.novikovi.mp3.id3v2;

/**
 * <p>Неподдерживаемый флаг в заголовке.</p>
 */
public class UnsupportedFlag extends Exception
{
	private static final long serialVersionUID = -1238417267634345976L;

	public UnsupportedFlag(String message)
	{
		super(message);
	}
}
