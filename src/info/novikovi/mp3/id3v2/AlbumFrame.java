package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;

public class AlbumFrame extends SimpleTextFrame
{
	/**
	 * название альбома
	 */
	private String album;

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
		// текст
		album = Utils.readFrameText(buf, offset + FRAME_HEADER_LENGTH, getDataSize());
	}
	
	public String getAlbum() {return album;}

	@Override
	public String toString()
	{
		return getId() + ": " + album;
	}

	
}
