package info.novikovi.mp3.id3v2;

import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;

public class TrackNumberFrame extends CommonFrame
{
	/**
	 * номер трека
	 */
	private int number;
	
	/**
	 * всего треков
	 */
	private Integer number_of_tracks;

	/**
	 * <p>Конструктор.</p>
	 * @param buf буфер данных
	 * @param offset смещение к началу фрейма
	 * @throws UnsupportedFlag
	 * @throws UnknownTextEncoding
	 */
	protected TrackNumberFrame(byte[] buf, int offset) throws UnsupportedFlag, UnknownTextEncoding
	{
		super(buf, offset);
		// смещаемся к данным
		offset += FRAME_HEADER_LENGTH;
		// текст
		String num = Utils.readFrameText(buf, offset, getDataSize()).getString();
		// ищем слеш
		String[] nums = num.split("/");
		if (nums.length == 1)
			number = Integer.parseInt(num);
		else
		{
			number = Integer.parseInt(nums[0]);
			number_of_tracks = Integer.valueOf(nums[1]);
		}
	}
	
	public int getTrackNumber() {return number;}
	public Integer getNumberOfTracks() {return number_of_tracks;}

	@Override
	public String toString()
	{
		return getId() + ": " + number + (number_of_tracks == null ? "" : "/" + number_of_tracks);
	}
}
