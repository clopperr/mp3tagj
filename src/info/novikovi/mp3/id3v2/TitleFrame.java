package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;

/**
 * <p>Название трека.</p>
 */
public class TitleFrame extends SimpleTextFrame
{
	/**
	 * название трека
	 */
	private String title;
	
	/**
	 * <p>Конструктор.</p>
	 * @param buf буфер данных
	 * @param offset смещение к началу фрейма
	 * @throws UnsupportedFlag
	 * @throws UnknownTextEncoding
	 */
	protected TitleFrame(byte[] buf, int offset) throws UnsupportedFlag, UnknownTextEncoding
	{
		super(buf, offset);
		// текст
		title = Utils.readFrameText(buf, offset + FRAME_HEADER_LENGTH, getDataSize());
	}

	public String getTitle() {return title;}

	@Override
	public String toString()
	{
		return getId() + ": " + title;
	}
}
