package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;

public class ArtistFrame extends SimpleTextFrame
{
	/**
	 * исполнитель
	 */
	private String artist;
	
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
		// текст
		artist = Utils.readFrameText(buf, offset + FRAME_HEADER_LENGTH, getDataSize());
	}
	
	public String getArtist() {return artist;}
	
	@Override
	public String toString()
	{
		return getId() + ": " + artist;
	}
}
