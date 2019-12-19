package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;

/**
 * <p>Год записи трека.</p>
 * <p>Год хранится в виде строки с кодировкой. Так как год - это число, исходную кодировку не сохраняем.</p>
 */
public class YearFrame extends CommonFrame
{
	/**
	 * год записи
	 */
	private int year;
	
	public YearFrame(byte[] buf, int offset) throws UnsupportedFlag, WrongDataSize, UnknownTextEncoding
	{
		super(buf, offset);
		// год записан строкой, возможно, в кодировке
		String year = Utils.readFrameText(buf, offset + FRAME_HEADER_LENGTH, getDataSize());
		// в число
		this.year = Integer.parseInt(year);
	}

	public int getYear() {return year;}
	
	@Override
	public String toString()
	{
		return getId() + ": " + year;
	}
}
