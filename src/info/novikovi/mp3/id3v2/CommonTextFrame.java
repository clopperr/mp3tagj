package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.TextEncoding;
import info.novikovi.mp3.TextEncoding.ENCODINGS;

/**
 * <p>Интерфейс для работы с текстовыми фреймами.</p>
 */
public interface CommonTextFrame
{
	/**
	 * <p>Возвращает кодировку содержимого фрейма.</p>
	 * @return исходная кодировка строки; см. {@link TextEncoding}
	 */
	public ENCODINGS getEncoding();
	
	/**
	 * <p>Возвращает кодировку содержимого фрейма.</p>
	 * @return исходная кодировка строки; см. {@link TextEncoding}
	 */
	public int getEncodingCode();
}
