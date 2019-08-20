package info.novikovi.mp3;

/**
 * <p>Константы кодировок текста.</p>
 */
public class TextEncoding
{
	private TextEncoding() {}
	
	/**
	 * ISO-8859-1. Terminated with $00.
	 */
	public static final int ISO8859 = 0;
	/**
	 * UTF-16 encoded Unicode with BOM.
	 * All strings in the same frame SHALL have the same byteorder.
	 * Terminated with $00 00.
	 */
	public static final int UTF_16 = 1;
	/**
	 * UTF-16BE encoded Unicode without BOM. Terminated with $00 00.
	 */
	public static final int UTF_16BE = 2;
	/**
	 * UTF-8 encoded Unicode. Terminated with $00.
	 */
	public static final int UTF_8 = 3;
	
	/**
	 * Перечисление для получения имён кодировок.
	 */
	public static enum ENCODINGS {ISO8859, UTF_16, UTF_16BE, UTF_8};
}
