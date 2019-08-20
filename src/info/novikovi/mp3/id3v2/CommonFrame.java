package info.novikovi.mp3.id3v2;

import java.nio.charset.Charset;

import info.novikovi.mp3.Utils;

/**
 * <p>Общее описание фрейма.</p>
 */
public class CommonFrame
{
	/**
	 * длина идентификатора тэга
	 */
	private final static int FRAME_ID_LENGTH = 4;
	/**
	 * размер заголовка фреймов
	 */
	protected final static int FRAME_HEADER_LENGTH = FRAME_ID_LENGTH + Utils.SYNC_SAFE_INT_LENGTH + 2;
	/**
	 * флаги фрейма
	 */
	private final static int FLAG_TAG_ALTER_PRESERVATION = 0x4000;
	private final static int FLAG_FILE_ALTER_PRESERVATION = 0x2000;
	private final static int FLAG_READ_ONLY = 0x1000;
	private final static int FLAG_GROUPING_IDENTITY = 0x0040;
	private final static int FLAG_COMPRESSION = 0x0008;
	private final static int FLAG_ENCRYPTION = 0x0004;
	private final static int FLAG_UNSYNC = 0x0002;
	private final static int FLAG_DATA_LENGTH = 0x0001;
	
	/**
	 * идентификатор фрейма
	 */
	private String id;
	
	/**
	 * размер фрейма
	 */
	private int size;
	
	/**
	 * размер данных
	 */
	private int data_size;
	
	/**
	 * флаг сохранения неизвестных фреймов при изменении тэга
	 */
	private boolean flag_tag_alter_preservation;
	/**
	 * флаг сохранения неизвестных фреймов при изменении файла
	 */
	private boolean flag_file_alter_preservation;
	/**
	 * фрейм помечен только для чтения
	 */
	private boolean flag_read_only;
	/**
	 * флаг группировки фрейма
	 */
	private boolean flag_grouping_identity;
	/**
	 * флаг сжатого фрейма
	 */
	private boolean flag_compression;
	/**
	 * флаг шифрованного фрейма
	 */
	private boolean flag_encryption;
	/**
	 * флаг десинхронизации фрейма
	 */
	private boolean flag_unsync;
	/**
	 * флаг наличия сведений о длине данных
	 */
	private boolean flag_data_length;
	
	/**
	 * <p>Конструктор читает заголовок фрейма.</p>
	 * @param buf буфер данных
	 * @param offset смещение к началу фрейма
	 * @throws UnsupportedFlag 
	 */
	protected CommonFrame(byte[] buf, int offset) throws UnsupportedFlag
	{
		// идентификатор
		id = new String(buf, offset, FRAME_ID_LENGTH, Charset.forName("utf-8"));
		offset += FRAME_ID_LENGTH;
		// размер данных
		data_size = Utils.synchSafeSize2Int(buf, offset);
		offset += Utils.SYNC_SAFE_INT_LENGTH;
		// флаги
		int flags = ((buf[offset++] & 0xFF) << 8) + (buf[offset++] & 0xFF); 
		// флаг сохранения неизвестных фреймов при изменении тэга
		flag_tag_alter_preservation = (flags & FLAG_TAG_ALTER_PRESERVATION) != 0;
		// флаг сохранения неизвестных фреймов при изменении файла
		flag_file_alter_preservation = (flags & FLAG_FILE_ALTER_PRESERVATION) != 0;
		// фрейм помечен только для чтения
		flag_read_only = (flags & FLAG_READ_ONLY) != 0;
		// флаг группировки фрейма
		flag_grouping_identity = (flags & FLAG_GROUPING_IDENTITY) != 0;
		// флаг сжатого фрейма
		flag_compression = (flags & FLAG_COMPRESSION) != 0;
		// флаг шифрованного фрейма
		flag_encryption = (flags & FLAG_ENCRYPTION) != 0;
		// флаг десинхронизации фрейма
		flag_unsync = (flags & FLAG_UNSYNC) != 0;
		// флаг наличия сведений о длине данных
		flag_data_length = (flags & FLAG_DATA_LENGTH) != 0;
		// полный размер фрейма
		size = data_size + FRAME_ID_LENGTH + Utils.SYNC_SAFE_INT_LENGTH + 2;
		// не со всякой фигнёй мы умеем работать
		if (flag_unsync)
			throw new UnsupportedFlag("flag Unsynchronization found in frame " + id);
	}
	
	/**
	 * <p>Размер данных.</p>
	 * @return размер данных
	 */
	public int getSize() {return size;}
	
	/**
	 * <p>Размер фрейма.</p>
	 * @return размер фрейма
	 */
	public int getDataSize() {return data_size;}
	
	public boolean hasFlagTagAlterPreservation() {return flag_tag_alter_preservation;}
	public boolean hasFlagFileAlterPreservation() {return flag_file_alter_preservation;}
	public boolean hasFlagReadOnly() {return flag_read_only;}
	public boolean hasFlagGroupingIdentity() {return flag_grouping_identity;}
	public boolean hasFlagCompression() {return flag_compression;}
	public boolean hasFlagEncryption() {return flag_encryption;}
	public boolean hasFlagUnsynchronization() {return flag_unsync;}
	public boolean hasFlagDataLength() {return flag_data_length;}
	
	@Override
	public String toString()
	{
		return id;
	}
	
	/**
	 * <p>Проверяет, нужного ли типа этот фрейм.</p>
	 * @param type искомый тип фрейма
	 * @return true, если тип фрейма совпадает с запрошенным
	 */
	public boolean isFrameOfType(String type)
	{
		return id.equals(type);
	}
	
	public String getId() {return id;}
}
