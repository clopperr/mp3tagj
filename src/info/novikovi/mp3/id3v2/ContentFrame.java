package info.novikovi.mp3.id3v2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import info.novikovi.mp3.Genre;
import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;
import info.novikovi.mp3.Utils.ExtString;

/**
 * <p>Тип содержимого трека.</p>
 */
public class ContentFrame extends SimpleTextFrame
{
	/**
	 * название альбома
	 */
	private String content;
	
	private List<Genre> genres = new ArrayList<>();

	/**
	 * <p>Конструктор.</p>
	 * @param buf буфер данных
	 * @param offset смещение к началу фрейма
	 * @throws UnsupportedFlag
	 * @throws UnknownTextEncoding
	 */
	protected ContentFrame(byte[] buf, int offset) throws UnsupportedFlag, UnknownTextEncoding
	{
		super(buf, offset);
		// смещаемся к данным
		offset += FRAME_HEADER_LENGTH;
		// текст
		ExtString str = Utils.readFrameText(buf, offset, getDataSize());
		content = str.getString();
		// выявляем ссылки на фиксированные жанры 
		Pattern p = Pattern.compile("\\((\\d+)\\)");
		Matcher m = p.matcher(content);
		int last_match = 0;
		while(m.find())
		{
			int genre_index = Integer.parseInt(m.group(1));
			genres.add(Genre.values()[genre_index]);
			last_match = m.end();
		}
		// убираем ссылки и сдваивание скобок
		content = content.substring(last_match).replaceAll("\\(\\(", "(");
	}
	
	public String getContent() {return content;}
	
	public int getGenresCount() {return genres.size();}
	public Genre getGenre(int index) {return genres.get(index);}

	@Override
	public String toString()
	{
		if (genres.size() > 0)
		{
			String list = genres.stream().map(Genre::getName).collect(Collectors.joining(", "));
			return getId() + ": " + content + "; " + list;
		}
		else
			return getId() + ": " + content;
	}
}
