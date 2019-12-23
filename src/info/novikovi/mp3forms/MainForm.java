package info.novikovi.mp3forms;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import info.novikovi.components.CellChecker;
import info.novikovi.components.HeadedTableCellRenderer;
import info.novikovi.components.ObjectTableModel;
import info.novikovi.components.StoredFrame;
import info.novikovi.components.StrictedTable;
import info.novikovi.components.TableCellRenderer;
import info.novikovi.errors.UnsupportedLanguage;
import info.novikovi.mp3.MP3;
import info.novikovi.mp3.TextEncoding.ENCODINGS;
import info.novikovi.mp3.UnknownTextEncoding;
import info.novikovi.mp3.id3v2.AlbumFrame;
import info.novikovi.mp3.id3v2.ArtistFrame;
import info.novikovi.mp3.id3v2.FrameFactory;
import info.novikovi.mp3.id3v2.TitleFrame;
import info.novikovi.mp3.id3v2.TrackNumberFrame;
import info.novikovi.mp3.id3v2.UnsupportedFlag;
import info.novikovi.mp3.id3v2.WrongDataSize;
import info.novikovi.utils.MessageDialog;
import info.novikovi.utils.Parameters;
import info.novikovi.utils.Resources;

/*
 * <p>Главная форма приложения.</p>
 */
public class MainForm extends StoredFrame implements CellChecker
{
	private static final long serialVersionUID = 2062760208030859011L;
	
	/**
	 * язык интерфейса
	 */
	private static final String APPLICATION_LANGUAGE = Parameters.APPLICATION_LANGUAGE;
	/**
	 * название окна
	 */
	private static final String FRAME_NAME = "main_frame";
	/**
	 * заголовок окна
	 */
	private static final String FRAME_TITLE_ID = "/frames/main/title";
	/**
	 * иконка главной формы
	 */
	private static final String MAIN_FORM_ICON = "note.jpg";
	/**
	 * тексты
	 */
	private static final String LABEL_FILE_LIST = "/frames/main/load_list";
	private static final String TABLE_COLUMN_FILE = "/frames/main/table/column_file";
	private static final String TABLE_COLUMN_NAME = "/frames/main/table/column_name";
	private static final String TABLE_COLUMN_ARTIST = "/frames/main/table/column_artist";
	private static final String TABLE_COLUMN_ALBUM = "/frames/main/table/column_album";
	private static final String TABLE_COLUMN_NUMBER = "/frames/main/table/column_number";
	private static final String DIALOG_OPEN_TITLE = "/dialog/open_file/title";
	private static final String DIALOG_OPEN_OK = "/dialog/open_file/ok_button";
	/**
	 * параметры
	 */
	private static final String PARAM_OPEN_PATH = "open_path";
	/**
	 * команды
	 */
	private static final String CMD_LOAD_LIST = "load list";
	/**
	 * сообщения
	 */
	private static final String MESSAGE_FILE_NOT_FOUND = "/message/file_doesnt_exists";
	private static final String MESSAGE_ERROR = "/message/error_title";
	/**
	 * отступ
	 */
	private static final int GAP = 3;
	
	/**
	 * имена столбцов в таблице
	 */
	private String[] tableHeaders;
	
	/**
	 * модель данных
	 */
	private ObjectTableModel model;
	
	/**
	 * таблица
	 */
	private StrictedTable table;
	
	/**
	 * главное окно
	 */
	private StoredFrame main_form;
	
	{
		// язык интерфейса
		try
		{
			Resources.setLanguage(parameters.getString(APPLICATION_LANGUAGE, Resources.getLanguage()));
			// для первого раза язык надо запомнить
			parameters.setString(APPLICATION_LANGUAGE, Resources.getLanguage());
		} 
		catch (UnsupportedLanguage e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * обработчик событий окна
	 */
	private WindowListener win_listener = new WindowListener()
	{
		@Override
		public void windowOpened(WindowEvent e) {}
		
		@Override
		public void windowIconified(WindowEvent e) {}
		
		@Override
		public void windowDeiconified(WindowEvent e) {}
		
		@Override
		public void windowDeactivated(WindowEvent e) {}
		
		@Override
		public void windowClosed(WindowEvent e)
		{
			// сохраняем параметры
			parameters.close();
		}
		
		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowClosing(WindowEvent arg0) {}
	};
	
	/**
	 * нажатие на кнопки
	 */
	private ActionListener button_listener = e -> makeButtonAction(((JButton)e.getSource()).getActionCommand());
	
	/**
	 * список неверных кодировок (название, исполнитель, альбом)
	 */
	private List<Boolean[]> wrongEncodings = new ArrayList<>(); 

	/**
	 * <p>Конструктор.</p>
	 */
	public MainForm()
	{
		super(null, FRAME_NAME, Resources.getString(FRAME_TITLE_ID, Resources.UNDEFINED_RESOURCE));
		// заголовки столбцов
		tableHeaders = new String[] {
				Resources.getString(TABLE_COLUMN_FILE, Resources.UNDEFINED_RESOURCE),
				Resources.getString(TABLE_COLUMN_NAME, Resources.UNDEFINED_RESOURCE),
				Resources.getString(TABLE_COLUMN_ARTIST, Resources.UNDEFINED_RESOURCE),
				Resources.getString(TABLE_COLUMN_ALBUM, Resources.UNDEFINED_RESOURCE),
				Resources.getString(TABLE_COLUMN_NUMBER, Resources.UNDEFINED_RESOURCE)
		};
	}

	/**
	 * Инициализирует форму.
	 */
	private void createForm()
	{
		// создаем окно 
		main_form = new StoredFrame(null, FRAME_NAME, Resources.getString(FRAME_TITLE_ID, Resources.UNDEFINED_RESOURCE));
		// реакция на закрытие окна
		main_form.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
		// ловим закрытие окна
		main_form.addWindowListener(win_listener);
		// размеры окна
		main_form.setMinimumSize(new Dimension(300, 200));
		// иконка
		main_form.setIconImage(Resources.getImage(MAIN_FORM_ICON));
		// наполняем компонентами
		fillForm();
		// открываем форму
		main_form.open();
		// упаковку делаем после открытия формы
		main_form.pack();
	}
	
	/**
	 * <p>Наполняет форму компонентами. Используется при создании формы и после
	 * редактирования настроек.</p>
	 */
	private void fillForm()
	{
		// убираем старую подложку
		main_form.getContentPane().removeAll();
		// панель кнопок
		JPanel panel_button = new JPanel();
		// панель данных
		JPanel panel_data = new JPanel();
		// кладём на форму
		GroupLayout layout = new GroupLayout(main_form.getContentPane());
		main_form.setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(panel_button)
				.addComponent(panel_data));
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(panel_button)
				.addComponent(panel_data));
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(false);
		// кнопка формы настроек
		JButton button_files = new JButton(Resources.getString(LABEL_FILE_LIST, Resources.UNDEFINED_RESOURCE));
		button_files.setActionCommand(CMD_LOAD_LIST);
		button_files.addActionListener(button_listener);
		// на форму
		layout = new GroupLayout(panel_button);
		panel_button.setLayout(layout);
		layout.setVerticalGroup(layout.createParallelGroup()
				.addComponent(button_files));
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(button_files));
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		// модель
		model = new ObjectTableModel(new String[][] {{" ", " ", " ", " ", " ", }}, tableHeaders);
		// таблица
		table = new StrictedTable(model);
		// отключаем авторасчёт ширин
		table.setAutoResizeMode(StrictedTable.AUTO_RESIZE_OFF);
		// пересчитываем ширины
		table.ApplyBestFit();
		// сортировка
		table.setAutoCreateRowSorter(true);
		// отрисовка
		table.setDefaultRenderer(Object.class, new TableCellRenderer(this));
		// отрисовка заголовка
		table.getTableHeader().setDefaultRenderer(new HeadedTableCellRenderer());
//		TableCellRenderer r = table.getTableHeader().getDefaultRenderer();
		// скроллер таблицы
		JScrollPane scroller = new JScrollPane(table);
		// на форму
		layout = new GroupLayout(panel_data);
		panel_data.setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGap(GAP)
				.addComponent(scroller)
				.addGap(GAP));
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(GAP)
				.addComponent(scroller)
				.addGap(GAP));
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(false);
	}

	/**
	 * <p>Обрабатывает нажатия на кнопки.</p>
	 * @param command команда, нажатой кнопки
	 */
	private void makeButtonAction(String command)
	{
		// загрузить список файлов
		if (command == CMD_LOAD_LIST)
		{
			readFileList();
		}
	}
	
	/**
	 * <p>Заполняет таблицу списком файлов.</p>
	 */
	private void readFileList()
	{
		// TODO wait
		// выбираем папку
		File folder = selectFolder();
		// пользователь отказался от операции
		if (folder == null) return;
		// перебираем файлы
		ArrayList<Object[]> files = new ArrayList<>();
		wrongEncodings.clear();
		for (File f: folder.listFiles())
			if (f.getName().endsWith(".mp3"))
			{
				try
				{
					MP3 mp3 = new MP3(f);
					if (mp3.hasV2Tag())
					{
						// название трека
						TitleFrame title = (TitleFrame)mp3.getV2Frame(FrameFactory.FRAME_TITLE);
						boolean wrongTitleEncoding = title.getEncoding() == ENCODINGS.ISO8859;
						// исполнитель
						ArtistFrame artist = (ArtistFrame)mp3.getV2Frame(FrameFactory.FRAME_ARTIST);
						boolean wrongArtistEncoding = artist.getEncoding() == ENCODINGS.ISO8859;
						// альбом
						AlbumFrame album = (AlbumFrame)mp3.getV2Frame(FrameFactory.FRAME_ALBUM);
						boolean wrongAlbumEncoding = album.getEncoding() == ENCODINGS.ISO8859;
						// номер трека
						TrackNumberFrame number = (TrackNumberFrame)mp3.getV2Frame(FrameFactory.FRAME_TRACK_NUM);
						// добавляем в модель
						files.add(new String[] {f.getName(), 
												title == null ? "" : title.getTitle(), 
												artist  == null ? "" : artist.getArtist(),
												album  == null ? "" : album.getAlbum(),
												number  == null ? "" : "" + number.getTrackNumber()
											});
						// запоминаем проблемы с кодировками
						wrongEncodings.add(new Boolean[] {wrongTitleEncoding, wrongArtistEncoding, wrongAlbumEncoding});
					}
				}
				catch (IOException | UnsupportedFlag | WrongDataSize | UnknownTextEncoding e)
				{
					e.printStackTrace();
					files.add(new String[] {f.getName(), "! error !", "", "", ""});
				}
			}
		// новая модель
		model = new ObjectTableModel(files, tableHeaders);
		table.setModel(model);
		table.ApplyBestFit();
	}
	
	/**
	 * <p>Открывает диалог для выбора папки с музыкой.</p>
	 * @return выбранная папка; null в случае отмены операции
	 */
	private File selectFolder()
	{
		// диалог
		JFileChooser fc = new JFileChooser();
		// заголовок
		fc.setDialogTitle(Resources.getString(DIALOG_OPEN_TITLE, Resources.UNDEFINED_RESOURCE));
		// кнопка Открыть
		fc.setApproveButtonText(Resources.getString(DIALOG_OPEN_OK, Resources.UNDEFINED_RESOURCE));
		// режим выбора файлов
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// выбираем только один файл
		fc.setMultiSelectionEnabled(false);
		// каталог
		fc.setCurrentDirectory(new File(parameters.getString(PARAM_OPEN_PATH, ".")));
		// открываем диалог
		int res = fc.showOpenDialog(main_form);
		// выбранный файл
		File file = null;
		// открываем
		if (res == JFileChooser.APPROVE_OPTION)
		{
			// выбранный файл
			file = fc.getSelectedFile();
			// должен существовать
			if (!file.exists())
			{
				new MessageDialog(main_form, Resources.getString(MESSAGE_ERROR, Resources.UNDEFINED_RESOURCE), 
						String.format(Resources.getString(MESSAGE_FILE_NOT_FOUND, Resources.UNDEFINED_RESOURCE), file.getPath()), 
							MessageDialog.OK);
				return null;
			}
			// каталог
			String folder = file.getAbsolutePath();
			// запоминаем
			parameters.setString(PARAM_OPEN_PATH, folder);
		}
		return file;
	}
	
	@Override
	public boolean isValidCell(int row, int column)
	{
		if (row >= wrongEncodings.size()) return true;
		// три колонки с контролем кодировки
		if (column > 0 && column < 4) return !wrongEncodings.get(row)[column - 1];
		// остальные корректны
		return true;
	}
	
	/**
	 * <p>Стартовый метод.</p>
	 * @param args
	 */
	public static void main(String[] args)
	{
		// выглядим, как система
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e)
		{
			// игнорируем ошибки
			e.printStackTrace();
		}
		// запускаем форму
		SwingUtilities.invokeLater(() -> new MainForm().createForm());
	}
	
}
