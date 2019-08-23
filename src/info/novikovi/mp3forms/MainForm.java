package info.novikovi.mp3forms;

import info.novikovi.components.StoredFrame;
import info.novikovi.utils.Parameters;
import info.novikovi.utils.Resources;

/*
 * <p>Главная форма приложения.</p>
 */
public class MainForm extends StoredFrame
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

	public MainForm()
	{
		super(null, FRAME_NAME, Resources.getString(FRAME_TITLE_ID, Resources.UNDEFINED_RESOURCE));
	}

}
