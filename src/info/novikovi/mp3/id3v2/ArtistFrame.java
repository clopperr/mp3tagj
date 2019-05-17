package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.TextEncoding;
import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;
import info.novikovi.mp3.TextEncoding.ENCODINGS;
import info.novikovi.mp3.Utils.ExtString;

public class ArtistFrame extends CommonFrame implements CommonTextFrame
{
	/**
	 * исполнитель
	 */
	private String artist;
	
	/**
	 * исходная кодировка строки; см. {@link TextEncoding}
	 */
	private int encoding;
	
	/**
	 * <p>Конструктор.</p>
	 * @param buf буфер данных
	 * @param offset смещение к началу фрейма
	 * @throws UnsupportedFlag
	 * @throws UnknownTextEncoding
	 */
	protected ArtistFrame(byte[] buf, int offset) throws UnsupportedFlag, UnknownTextEncoding
	{
		super(buf, offset);
		// смещаемся к данным
		offset += FRAME_HEADER_LENGTH;
		// текст
		ExtString str = Utils.readFrameText(buf, offset, getDataSize());
		artist = str.getString();
		encoding = str.getEncoding();
	}
	
	public String getArtist() {return artist;}
	
	@Override
	public String toString()
	{
		return getId() + ": " + artist;
	}
	
	@Override
	public ENCODINGS getEncoding()
	{
		return TextEncoding.ENCODINGS.values()[encoding];
	}

	@Override
	public int getEncodingCode() {return encoding;}
}
