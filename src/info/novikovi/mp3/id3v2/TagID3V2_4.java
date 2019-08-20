package info.novikovi.mp3.id3v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.Utils;

/**
 * <p>Читает тэг ID3 версии 2.4.x.</p>
 */
public class TagID3V2_4
{
	/**
	 * размер заголовка id3 v2
	 */
	private static final int ID3V2_LENGTH = 10;
	/**
	 * размер подвала id3 v2
	 */
	private static final int ID3V2_FOOTER_LENGTH = 10;
	/**
	 * сигнатура блока id3 v2
	 */
	private static final String ID3V2_SIGNATURE = "ID3";
	/**
	 * сигнатура подвала блока id3 v2
	 */
	private static final String ID3V2_FOOTER_SIGNATURE = "3DI";
	/**
	 * корректная старшая версия
	 */
	private static final int ID3V2_MAJOR = 4;
	/**
	 * длина идентификатора
	 */
	private static final int ID_LENGTH = 3;
	/**
	 * биты флагов
	 */
	private static final int FLAG_UNSYNCHRONISATION = 0x80;
	private static final int FLAG_EXTENDED = 0x40;
	private static final int FLAG_EXPERIMENTAL = 0x20;
	private static final int FLAG_FOOTER = 0x10;
	
	private static final int FLAG_UPDATE = 0x40;
	private static final int FLAG_CRC = 0x20;
	private static final int FLAG_RESTRICTONS = 0x10;
	
	/**
	 * биты ограничений
	 */
	private static final int RESTRICTON_TAG_SIZE = 0xC0;
	private static final int RESTRICTON_ENCODING = 0x20;
	private static final int RESTRICTON_FIELD_SIZE = 0x18;
	private static final int RESTRICTON_IMAGE_ENC = 0x04;
	private static final int RESTRICTON_IMAGE_SIZE = 0x03;
	
	/**
	 * наличие тэга в файле
	 */
	private boolean has_tag;
	
	/**
	 * флаги
	 */
	private boolean flag_unsynchronisation;
	private boolean flag_extended;
	private boolean flag_experimental;
	private boolean flag_footer;
	
	private boolean flag_update;
	private boolean flag_crc;
	private boolean flag_restrictions;
	
	/**
	 * ограничения
	 */
	private int restrictions_tag_size;
	private boolean restrictions_encoding;
	private int restrictions_field_size;
	private boolean restrictions_image_enc;
	private int restrictions_image_size;
	
	/**
	 * версия заголовка
	 */
	private int major_version;
	private int minor_version;
	
	/**
	 * размер тэга в исходном файле
	 */
	private int original_size;
	
	/**
	 * размер резерва
	 */
	private int padding_size;
	
	/**
	 * список фреймов
	 */
	private List<CommonFrame> frames = new ArrayList<>();
	
	/**
	 * <p>Конструктор.</p>
	 * @param mp3_file файл mp3
	 * @throws FileNotFoundException 
	 * @throws IOException
	 * @throws UnsupportedFlag 
	 * @throws WrongDataSize 
	 * @throws UnknownTextEncoding 
	 */
	public TagID3V2_4(File mp3_file) throws FileNotFoundException, IOException, UnsupportedFlag, WrongDataSize, UnknownTextEncoding
	{
		// слишком маленький файл
		if (mp3_file.length() > ID3V2_LENGTH)
			try
			(InputStream is = new FileInputStream(mp3_file);)
			{
				readTag(is);
			}
	}
	
	/**
	 * <p>Конструктор.</p>
	 * @param is поток данных
	 * @throws IOException
	 * @throws UnsupportedFlag
	 * @throws WrongDataSize
	 * @throws UnknownTextEncoding
	 */
	public TagID3V2_4(InputStream is) throws IOException, UnsupportedFlag, WrongDataSize, UnknownTextEncoding
	{
		readTag(is);
	}
	
	/**
	 * <p>Читает содержимое тэга из потока.</p>
	 * @param is поток данных
	 * @throws IOException
	 * @throws UnsupportedFlag
	 * @throws WrongDataSize
	 * @throws UnknownTextEncoding
	 */
	private void readTag(InputStream is) throws IOException, UnsupportedFlag, WrongDataSize, UnknownTextEncoding
	{
		// читаем заголовок
		byte[] header = Utils.readBlock(is, ID3V2_LENGTH);
		int offset = 0;
		// проверяем сигнатуру
		String sign = new String(header, offset, ID_LENGTH, Charset.forName("utf-8"));
		offset += ID_LENGTH;
		if (sign.equals(ID3V2_SIGNATURE))
		{	
			// версия заголовка
			major_version = header[offset++] & 0xFF;
			minor_version = header[offset++] & 0xFF;
			if (major_version == ID3V2_MAJOR)
			{
				// следует игнорировать версии тэга, отличные от четвёртой
				has_tag = true;
				// флаги
				int flags = header[offset++] & 0xFF;
				flag_unsynchronisation = (flags & FLAG_UNSYNCHRONISATION) != 0;
				flag_extended = (flags & FLAG_EXTENDED) != 0;
				flag_experimental = (flags & FLAG_EXPERIMENTAL) != 0;
				flag_footer = (flags & FLAG_FOOTER) != 0;
				// не со всякой фигнёй мы умеем работать
				if (flag_unsynchronisation)
					throw new UnsupportedFlag("flag Unsynchronization found in tag id3 v. 2.4");
				// размер заголовка
				int size = Utils.synchSafeSize2Int(header, offset);
				// полный размер тэга в исходном файле
				original_size = ID3V2_LENGTH + size + (flag_footer ? ID3V2_FOOTER_LENGTH : 0);
				// начинаем вычислять размер резерва
				padding_size = size;
				// данные тэга
				byte[] tag_data = Utils.readBlock(is, size);
				offset = 0;
				// дополнительный заголовок
				if (flag_extended)
				{
					System.out.println("[WARNING] extended header found!");
					// размер заголовка
					padding_size -= Utils.synchSafeSize2Int(tag_data, offset);
					offset += Utils.SYNC_SAFE_INT_LENGTH;
					// количество байтов для флагов
					int flag_bytes = tag_data[offset++] & 0xFF;
					// флаги
					flags = tag_data[offset] & 0xFF;
					offset += flag_bytes;
					flag_update = (flags & FLAG_UPDATE) != 0;
					flag_crc = (flags & FLAG_CRC) != 0;
					flag_restrictions = (flags & FLAG_RESTRICTONS) != 0;
					// данных для флага update быть не должно
					if (flag_update)
					{
						int data_size = tag_data[offset++] & 0xFF;
						offset += data_size;
					}
					// CRC пропустим
					if (flag_crc)
					{
						int data_size = tag_data[offset++] & 0xFF;
						offset += data_size;
					}
					// ограничения
					if (flag_restrictions)
					{
						int data_size = tag_data[offset++] & 0xFF;
						if (data_size == 1)
						{
							int restrictions = tag_data[offset++] & 0xFF;
							restrictions_tag_size = restrictions & RESTRICTON_TAG_SIZE;
							restrictions_encoding = (restrictions & RESTRICTON_ENCODING) != 0;
							restrictions_field_size = restrictions & RESTRICTON_FIELD_SIZE;
							restrictions_image_enc = (restrictions & RESTRICTON_IMAGE_ENC) != 0;
							restrictions_image_size = restrictions & RESTRICTON_IMAGE_SIZE;
						}
						offset += data_size;
					}
				}
				// дальше идут фреймы
				while (offset < size && (tag_data[offset] & 0xFF) >= 48)
				{
					CommonFrame frame = FrameFactory.getFrame(tag_data, offset);
					frames.add(frame);
					offset += frame.getSize();
					padding_size -= frame.getSize();
				}
				// уменьшаем резерв на величину подвала
				if (flag_footer)
				{
					padding_size -= ID3V2_FOOTER_LENGTH;
					// резерва быть не должно
					if (padding_size != 0)
					{
						System.out.println("[WARNING] non zero padding with footer!");
						padding_size = 0;
					}
				}
			}
		}
	}
	
	/**
	 * <p>Наличие тэга версии 2.4.x в файле.</p>
	 * @return true, если тэг есть
	 */
	public boolean hasTag() {return has_tag;}
	
	public boolean isFlagUnsynchronisationSet() {return flag_unsynchronisation;}
	public boolean isFlagExtendedSet() {return flag_extended;}
	public boolean isFlagExperimentalSet() {return flag_experimental;}
	public boolean isFlagFooterSet() {return flag_footer;}
	public boolean isFlagUpdateSet() {return flag_update;}
	public boolean isFlagCRCSet() {return flag_crc;}
	public boolean isFlagRestrictionsSet() {return flag_restrictions;}
	
	public int getRestrictionTagSize() {return restrictions_tag_size;}
	public boolean isRestrictionsEncodingSet() {return restrictions_encoding;}
	public int getRestrictionsFieldSize() {return restrictions_field_size;}
	public boolean isRestrictionsImageEncSet() {return restrictions_image_enc;}
	public int getRestrictionsImageSize() {return restrictions_image_size;}
	
	public int getMajorVersion() {return major_version;}
	public int getMinorVersion() {return minor_version;}
	
	/**
	 * <p>Возвращает полный размер тэга в исходном файле</p>
	 * @return полный размер тэга
	 */
	public int getOriginalSize() {return original_size;}
	
	/**
	 * <p>Возвращает количество фреймов.</p>
	 * @return количество фреймов
	 */
	public int getFrameCount() {return frames.size();}
	
	/**
	 * <p>Возвращает фрейм с указанным индексом.</p>
	 * @param index номер фрейма
	 * @return фрейм
	 */
	public CommonFrame getFrame(int index) {return frames.get(index);}
	
	/**
	 * <p>Возвращает фрейм с указанным идентификатором.</p>
	 * @param frame_id идентификатор фрейма
	 * @return фрейм или null, если такого фрейма нет
	 */
	public CommonFrame getFrameByName(String frame_id)
	{
		// ищем фрейм по идентификатору
		for (CommonFrame frame: frames)
			if (frame.getId().equals(frame_id)) return frame;
		// не нашли
		return null;
	}
	
	/**
	 * <p>Устанавливает желаемый размер резерва. При наличии подвала резерв должен быть равен 0.</p>
	 * @param padding_size размер резерва в байтах
	 * @throws WrongPaddingSize ненулевой резерв при наличии подвала
	 */
	public void setPaddingSize(int padding_size) throws WrongPaddingSize
	{
		if (padding_size != 0 && isFlagFooterSet()) throw new WrongPaddingSize("non zero padding with footer present");
		this.padding_size = padding_size;
	}
	
	/**
	 * <p>Устанавливает или сбрасывает флаг подвала. При установленном флаге резерв обнуляется.</p>
	 * @param footer true, если нужно установить флаг
	 */
	public void setFlagFooter(boolean footer)
	{
		// если нужен подвал, резерв должен быть обнулён
		if (footer) padding_size = 0;
		flag_footer = footer;
	}
	
	/**
	 * <p>Сохраняет тэг в файл. Содержимое файла полностью затирается.</p>
	 * @param f файл
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveTag(File f) throws FileNotFoundException, IOException
	{
		try(FileOutputStream fos = new FileOutputStream(f);)
		{
			saveTag(fos);
		}
	}
	
	/**
	 * <p>Сохраняет тэг в поток.</p>
	 * @param os поток данных
	 */
	public void saveTag(OutputStream os)
	{
		// TODO
	}
}
