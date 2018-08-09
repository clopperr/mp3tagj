package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.TextEncoding;
import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;
import info.novikovi.mp3.Utils.ExtString;

public class AlbumFrame extends CommonFrame
{
	/**
	 * название альбома
	 */
	private String album;
	
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
	protected AlbumFrame(byte[] buf, int offset) throws UnsupportedFlag, UnknownTextEncoding
	{
		super(buf, offset);
		// смещаемся к данным
		offset += FRAME_HEADER_LENGTH;
		// текст
		ExtString str = Utils.readFrameText(buf, offset, getDataSize());
		album = str.getString();
		encoding = str.getEncoding();
	}
	
	public String getAlbum() {return album;}
	public int getEncoding() {return encoding;}

	@Override
	public String toString()
	{
		return getId() + ": " + album;
	}
}