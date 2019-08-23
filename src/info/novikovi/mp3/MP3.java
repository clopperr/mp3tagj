package info.novikovi.mp3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import info.novikovi.mp3.id3v1.TagID3V1;
import info.novikovi.mp3.id3v2.CommonFrame;
import info.novikovi.mp3.id3v2.SimpleTextFrame;
import info.novikovi.mp3.id3v2.TagID3V2;
import info.novikovi.mp3.id3v2.UnsupportedFlag;
import info.novikovi.mp3.id3v2.WrongDataSize;

/**
 * <p>Обработчик файла mp3.</p>
 */
public class MP3
{
	/**
	 * mp3-файл
	 */
	private File mp3_file;
	
	/**
	 * тэг id3 версии 1
	 */
	private TagID3V1 id3v1_tag;
	
	/**
	 * тэг id3 версии 2
	 */
	private TagID3V2 id3v2_tag;
	
	/**
	 * <p>Конструктор.</p>
	 * @param mp3 mp3-файл
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedFlag 
	 * @throws WrongDataSize 
	 * @throws UnknownTextEncoding 
	 */
	public MP3(File mp3) throws FileNotFoundException, IOException, UnsupportedFlag, WrongDataSize, UnknownTextEncoding
	{
		mp3_file = mp3;
		// в качестве ANSI используем кодировку cp1251
		Utils.setAnsiCharset("cp1251");
		// читаем тэги версии 1
		id3v1_tag = new TagID3V1(mp3_file);
		// читаем тэги версии 2
		id3v2_tag = new TagID3V2(mp3_file);
	}
	
	/**
	 * <p>Конструктор.</p>
	 * @param file_name имя mp3-файла
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedFlag 
	 * @throws WrongDataSize 
	 * @throws UnknownTextEncoding 
	 */
	public MP3(String file_name) throws FileNotFoundException, IOException, UnsupportedFlag, WrongDataSize, UnknownTextEncoding
	{
		this(new File(file_name));
	}
	
	@Override
	public String toString()
	{
		return mp3_file + "; id3 v1: " + (hasV1Tag() ? "yes" : "no")
						+ "; id3 v2: " + (hasV2Tag() ? "yes" : "no");
	}
	
	/**
	 * <p>Проверяет наличие тэга версии 1.</p>
	 * @return true, если объект содержит тэг версии 1
	 */
	public boolean hasV1Tag() {return id3v1_tag.hasTag();}
	
	/**
	 * <p>Проверяет наличие тэга версии 2.</p>
	 * @return true, если объект содержит тэг версии 2
	 */
	public boolean hasV2Tag() {return id3v2_tag.hasTag();}
	
	/**
	 * <p>Возвращает название трека.</p>
	 * @return название трека
	 */
	public String getV1Name() {return id3v1_tag.getName();}
	
	/**
	 * <p>Возвращает исполнителя</p>
	 * @return исполнитель
	 */
	public String getV1Singer() {return id3v1_tag.getSinger();}
	
	/**
	 * <p>Возвращает название альбома.</p>
	 * @return название альбома
	 */
	public String getV1Album() {return id3v1_tag.getAlbum();}
	
	/**
	 * <p>Возвращает комментарий.</p>
	 * @return комментарий
	 */
	public String getV1Comment() {return id3v1_tag.getComment();}
	
	/**
	 * <p>Возвращает год.</p>
	 * @return год
	 */
	public int getV1Year() {return id3v1_tag.getYear();}
	
	/**
	 * <p>Возвращает номер трека.</p>
	 * @return номер трека или null, если номер трека не задан
	 */
	public Integer getV1TrackNumber() {return id3v1_tag.getTrackNumber();}
	
	/**
	 * <p>Возвращает номер жанра.</p>
	 * @return номер жанра
	 */
	public Genre getV1Genre()
	{
		return id3v1_tag.getV1Genre();
	}
	
	public static void main(String[] args) throws Exception
	{
		MP3 mp3 = new MP3("c:\\TEMP\\a\\mp3\\kongos_-_autocorrect_(topicmusic.net).mp3");
		
		for (int i = 0; i < mp3.id3v2_tag.getFrameCount(); i++)
		{
			CommonFrame frame = mp3.id3v2_tag.getFrame(i);
			String s = "" + frame.toString();
			if (frame instanceof SimpleTextFrame)
				System.out.println(frame + " " + ((SimpleTextFrame)frame).getEncoding());
			else
				System.out.println(frame);
		}
		
		System.out.println(mp3);
	}

}
