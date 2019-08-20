package info.novikovi.mp3.id3v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import info.novikovi.mp3.Genre;
import info.novikovi.mp3.Utils;

/**
 * <p>Читает тэг ID3 версии 1.</p>
 */
public class TagID3V1
{
	/**
	 * размер блока id3 v1
	 */
	private static final int ID3V1_LENGTH = 128;
	/**
	 * длина идентификатора
	 */
	private static final int ID_LENGTH = 3;
	/**
	 * сигнатура блока id3 v1
	 */
	private static final String ID3V1_SIGNATURE = "TAG";
	/**
	 * длина блоков названия, исполнителя, альбома и комментария
	 */
	private static final int ID3V1_BLOCK_LENGTH = 30;
	/**
	 * длина блока года
	 */
	private static final int ID3V1_YEAR_LENGTH = 4;
	/**
	 * кодировка для тэга версии 1
	 */
	private static final Charset V1_CHARSET = Charset.forName("cp1251");
	
	/**
	 * данные тэга версии 1
	 * название трека
	 */
	private String name;
	/**
	 * данные тэга версии 1
	 * исполнитель
	 */
	private String singer;
	/**
	 * данные тэга версии 1
	 * альбом
	 */
	private String album;
	/**
	 * данные тэга версии 1
	 * год
	 */
	private int year;
	/**
	 * данные тэга версии 1
	 * комментарий
	 */
	private String comment;
	/**
	 * данные тэга версии 1
	 * номер трека
	 */
	private Integer track_num;
	/**
	 * данные тэга версии 1
	 * номер жанра
	 */
	private int genre;
	
	/**
	 * наличие тэга в файле
	 */
	private boolean has_tag;
	
	/**
	 * <p>Конструктор.</p>
	 * @param mp3_file файл mp3
	 * @throws IOException
	 */
	public TagID3V1(File mp3_file) throws IOException
	{
		try
		(FileInputStream fis = new FileInputStream(mp3_file);)
		{
			// размер файла
			long file_size = mp3_file.length();
			// если размер достаточно велик
			if (file_size > ID3V1_LENGTH)
			{
				// переходим к хвосту файла
				fis.skip(file_size - ID3V1_LENGTH);
				// читаем блок
				byte[] buf = Utils.readBlock(fis, ID3V1_LENGTH);
				// проверяем сигнатуру
				String sign = new String(buf, 0, ID_LENGTH, Charset.forName("utf-8"));
				int offset = ID_LENGTH;
				if (sign.equals(ID3V1_SIGNATURE))
				{
					has_tag = true;
					// название трека
					name = Utils.makeASCIIZ(buf, offset, ID3V1_BLOCK_LENGTH, V1_CHARSET);
					offset += ID3V1_BLOCK_LENGTH;
					// исполнитель
					singer = Utils.makeASCIIZ(buf, offset, ID3V1_BLOCK_LENGTH, V1_CHARSET);
					offset += ID3V1_BLOCK_LENGTH;
					// альбом
					album = Utils.makeASCIIZ(buf, offset, ID3V1_BLOCK_LENGTH, V1_CHARSET);
					offset += ID3V1_BLOCK_LENGTH;
					// год
					String syear = Utils.makeASCIIZ(buf, offset, ID3V1_YEAR_LENGTH, V1_CHARSET);
					offset += ID3V1_YEAR_LENGTH;
					year = Integer.parseInt(syear);
					// комментарий; обрабатываем полный блок; если есть номер трека, он отделён нулём
					comment = Utils.makeASCIIZ(buf, offset, ID3V1_BLOCK_LENGTH, V1_CHARSET);
					offset += ID3V1_BLOCK_LENGTH;
					// номер трека; отделён нулём от комментария
					if (buf[offset - 2] == 0)
						track_num = buf[offset - 1] & 0xFF;
					// жанр
					genre = buf[offset] & 0xFF;
				}
			}
		}
	}
	
	/**
	 * <p>Возвращает название трека.</p>
	 * @return название трека
	 */
	public String getName() {return name;}
	
	/**
	 * <p>Возвращает исполнителя</p>
	 * @return исполнитель
	 */
	public String getSinger() {return singer;}
	
	/**
	 * <p>Возвращает название альбома.</p>
	 * @return название альбома
	 */
	public String getAlbum() {return album;}
	
	/**
	 * <p>Возвращает комментарий.</p>
	 * @return комментарий
	 */
	public String getComment() {return comment;}
	
	/**
	 * <p>Возвращает год.</p>
	 * @return год
	 */
	public int getYear() {return year;}
	
	/**
	 * <p>Возвращает номер трека.</p>
	 * @return номер трека или null, если номер трека не задан
	 */
	public Integer getTrackNumber() {return track_num;}
	
	/**
	 * <p>Возвращает жанр из списка.</p>
	 * @return жанр или null, если не указано
	 */
	public Genre getV1Genre()
	{
		return genre < Genre.values().length ? Genre.values()[genre] : null;
	}
	
	/**
	 * <p>Наличие тэга версии 1 в файле.</p>
	 * @return true, если тэг есть
	 */
	public boolean hasTag() {return has_tag;}
}
