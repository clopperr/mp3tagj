package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.TextEncoding.ENCODINGS;
import info.novikovi.mp3.TextEncoding;
import info.novikovi.mp3.Utils;

public abstract class SimpleTextFrame extends CommonFrame implements CommonTextFrame
{
	/**
	 * исходная кодировка строки; см. {@link TextEncoding}
	 */
	private int encoding;
	
	/**
	 * <p>Конструктор.</p>
	 * @param buf буфер данных
	 * @param offset смещение к началу фрейма
	 * @throws UnsupportedFlag 
	 */
	protected SimpleTextFrame(byte[] buf, int offset) throws UnsupportedFlag
	{
		super(buf, offset);
		// кодировка
		encoding = Utils.getFrameEncoding(buf, offset + FRAME_HEADER_LENGTH);
	}

	@Override
	public ENCODINGS getEncoding()
	{
		return TextEncoding.ENCODINGS.values()[encoding];
	}

	@Override
	public int getEncodingCode() {return encoding;}
}
