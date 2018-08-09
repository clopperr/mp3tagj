package info.novikovi.mp3.id3v2;

import java.nio.charset.Charset;

public class YearFrame extends CommonFrame
{
	/**
	 * размер данных во фрейме
	 */
	private static final int YEAR_SIZE = 4;
	
	/**
	 * год записи
	 */
	private int year;
	
	public YearFrame(byte[] buf, int offset) throws UnsupportedFlag, WrongDataSize
	{
		super(buf, offset);
		// размер данных должен быть точно 4 байта
		if (getDataSize() != YEAR_SIZE)
			throw new WrongDataSize("wrong size of year frame: " + getDataSize());
		// смещаемся к данным
		offset += FRAME_HEADER_LENGTH;
		// читаем год
		year = Integer.parseInt(new String(buf, offset, YEAR_SIZE, Charset.forName("utf-8")));
	}

	public int getYear() {return year;}
	
	@Override
	public String toString()
	{
		return getId() + ": " + year;
	}
}
