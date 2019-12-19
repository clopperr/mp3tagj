package info.novikovi.mp3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * <p>Утилиты.</p>
 */
public class Utils
{
	/**
	 * количество байт для кодирования короткого int
	 */
	public static final int SYNC_SAFE_INT_LENGTH = 4;
	
	/**
	 * кодировка для трактовки ISO-8859-1
	 */
	private static Charset iso = Charset.forName("ISO-8859-1");
	
	private Utils() {}
	
	/**
	 * <p>Читает и возвращает блок указанного размера из потока. Данные в потоке могут закончится раньше ожидаемого.
	 * В этом случае размер буфера будет уменьшен.</p>
	 * @param is поток для чтения
	 * @param read_size размер блока
	 * @return прочитанный блок
	 * @throws IOException
	 */
	public static byte[] readBlock(InputStream is, int read_size) throws IOException
	{
		// сначала выделяем буфер ожидаемого размера
		byte[] buf = new byte[read_size];
		// читаем блок
		int p = 0;
		int k = is.read(buf, p, read_size);
		while (k > -1 && p < read_size)
		{
			p += k;
			if (p < read_size) k = is.read(buf, p, read_size - p);
		}
		// если данные кончились раньше, усекаем буфер
		if (p < read_size) buf = Arrays.copyOf(buf, p);
		return buf;
	}
	
	/**
	 * <p>Возвращает строку, созданную из буфера. В качестве ограничителя данных воспринимается любой байт меньше 32.</p>
	 * @param buf буфер данных
	 * @param charset кодировка строки
	 * @return строка
	 */
	public static String makeASCIIZ(byte[] buf, int offset, int len, Charset charset)
	{
		// первоначальная длина буфера
		int buf_len = len;
		// ищем конец строки
		for (int i = 0; i < buf_len; i++)
			// всё, что меньше пробела - конец
			if ((buf[i + offset] & 0xFF) < 32) buf_len = i;
		return new String(buf, offset, buf_len , charset);
	}
	
	/**
	 * <p>Преобразует набор из четырёх семибитных чисел в четырёхбайтовый int перестановкой битов от старших байт к младшим.</p>
	 * <p>0000000 0101111 1111001 1100000 -&gt; 00001011 11111100 11100000</p> 
	 * @param buf байтовый буфер
	 * @param offset смещение в буфере
	 * @return декодированное целое число
	 */
	public static int synchSafeSize2Int(byte[] buf, int offset)
	{
		int result = 0;
		// перебираем байты
		for (int i = 0; i < SYNC_SAFE_INT_LENGTH; i++)
		{
			if ((buf[offset + i] & 0x80) != 0) throw new NumberFormatException("high bit is non zero");
			// от каждого берём семь бит
			result = (result << 7) + (buf[offset + i] & 0x7F);
		}
		return result;
	}
	
	/**
	 * <p>Преобразует целое число в набор из четырёх семибитных байтов. Старшие четыре бита в числе должны быть нулевыми.</p>
	 * <p>00001011 11111100 11100000 -&gt; 0000000 0101111 1111001 1100000</p> 
	 * @param x целое число
	 * @return набор из четырёх семибитных байтов
	 */
	public static byte[] int2SynchSafeSize(int x)
	{
		if ((x & 0xF0000000) != 0) throw new NumberFormatException("four higher bits are non zero");
		byte[] buf = new byte[SYNC_SAFE_INT_LENGTH];
		for (int i = SYNC_SAFE_INT_LENGTH - 1; i >= 0; i--)
		{
			// берём младшие семь бит
			buf[i] = (byte)(x & 0x7F);
			// сдвигаем число на семь бит
			x = x >>> 7;
		}
		return buf;
	}
	
	/**
	 * <p>Склеивает четыре байта в целое число. Используется порядок big-endian. Отрицательные числа не поддерживаются.</p>
	 * @param buf байтовый буфер
	 * @param offset смещение в буфере
	 * @return целое число
	 */
	public static int unsafeSize2Int(byte[] buf, int offset)
	{
		// старший байт не должен иметь знака
		if ((buf[offset] & 0x80) != 0) throw new NumberFormatException("high bit is non zero");
		int result = 0;
		// перебираем байты
		for (int i = 0; i < SYNC_SAFE_INT_LENGTH; i++) result = (result << 8) + (buf[offset + i] & 0xFF);
		return result;
	}
	
	/**
	 * <p>Возвращает номер кодировки текста во фрейме.</p>
	 * @param buf буфер данных
	 * @param offset смещение текста
	 * @return номер кодировки; см. {@link TextEncoding}
	 */
	public static int getFrameEncoding(byte[] buf, int offset)
	{
		return buf[offset] & 0xFF;
	}
	
	/**
	 * <p>Читает строку из данных фрейма с учётом кодировки.</p>
	 * @param buf буфер данных
	 * @param offset смещение текста
	 * @param len размер фрейма
	 * @return строка
	 * @throws UnknownTextEncoding 
	 */
	public static String readFrameText(byte[] buf, int offset, int len) throws UnknownTextEncoding
	{
		// кодировка текста; сразу увеличиваем смещение
		int text_enc = getFrameEncoding(buf, offset++);
		// уменьшаем размер на один байт
		len--;
		
		String result;
		switch (text_enc)
		{
			case TextEncoding.ISO8859:
				// некоторые не пишут ноль
				if (buf[offset + len - 1] == 0) len--;
				// формируем строку
				result = new String(buf, offset, len, iso);
				break;
			case TextEncoding.UTF_16:
				// некоторые не пишут ноль
				if (buf[offset + len - 1] == 0 && buf[offset + len - 2] == 0) len -= 2;
				// формируем строку
				result = new String(buf, offset, len, Charset.forName("UTF-16"));
				break;
			case TextEncoding.UTF_16BE:
				// некоторые не пишут ноль
				if (buf[offset + len - 1] == 0 && buf[offset + len - 2] == 0) len -= 2;
				// формируем строку
				result = new String(buf, offset, len, Charset.forName("UTF-16BE"));
				break;
			case TextEncoding.UTF_8:
				// некоторые не пишут ноль
				if (buf[offset + len - 1] == 0) len--;
				// формируем строку
				result = new String(buf, offset, len, Charset.forName("UTF-8"));
				break;
			default:
				throw new UnknownTextEncoding("unknown text encoding: " + text_enc);
		}
		return result;
	}
	
	/**
	 * <p>Устанавливает кодировку для трактовки кода {@link TextEncoding#ISO8859}.</p>
	 * @param charset_name имя кодировки
	 */
	public static void setAnsiCharset(String charset_name)
	{
		iso = Charset.forName(charset_name);
	}
	
}
