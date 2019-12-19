package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.UnknownTextEncoding;

/**
 * <p>Фабрика фреймов.</p>
 */
public class FrameFactory
{
	private FrameFactory() {}
	
	/**
	 * название трека
	 */
	public static final String FRAME_TITLE = "TIT2";
	/**
	 * альбом
	 */
	public static final String FRAME_ALBUM = "TALB";
	/**
	 * номер трека
	 */
	public static final String FRAME_TRACK_NUM = "TRCK";
	/**
	 * исполнитель
	 */
	public static final String FRAME_ARTIST = "TPE1";
	/**
	 * тип содержимого
	 */
	public static final String FRAME_CONTENT = "TCON";
	/**
	 * год
	 */
	public static final String FRAME_YEAR = "TYER";
	
	/**
	 * <p>Метод подбирает класс для работы с фреймом.</p>
	 * @param buf буфер данных
	 * @param offset смещение к фрейму
	 * @return
	 * @throws UnsupportedFlag 
	 * @throws WrongDataSize 
	 * @throws UnknownTextEncoding 
	 */
	public static CommonFrame getFrame(byte[] buf, int offset) throws UnsupportedFlag, WrongDataSize, UnknownTextEncoding
	{
		// читаем заголовок
		CommonFrame frame = new CommonFrame(buf, offset);
		// ищем стандартный фрейм
		if (frame.isFrameOfType(FRAME_TITLE))
			frame = new TitleFrame(buf, offset);
		else if (frame.isFrameOfType(FRAME_ALBUM))
			frame = new AlbumFrame(buf, offset);
		else if (frame.isFrameOfType(FRAME_TRACK_NUM))
			frame = new TrackNumberFrame(buf, offset);
		else if (frame.isFrameOfType(FRAME_ARTIST))
			frame = new ArtistFrame(buf, offset);
		else if (frame.isFrameOfType(FRAME_CONTENT))
			frame = new ContentFrame(buf, offset);
		else if (frame.isFrameOfType(FRAME_YEAR))
			frame = new YearFrame(buf, offset);
 
		return frame;
	}
}
