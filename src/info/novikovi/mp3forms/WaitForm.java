package info.novikovi.mp3forms;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.GroupLayout.Group;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import info.novikovi.components.StoredDialog;
import info.novikovi.utils.Resources;

public class WaitForm extends StoredDialog
{
	private static final long serialVersionUID = -4915291121294909929L;
	/**
	 * заголовок
	 */
	private static final String TITLE_WAIT = "/dialog/wait/title";
	/**
	 * название формы
	 */
	private static final String DIALOG_NAME = "wait_form";
	/**
	 * картинка для ожидания
	 */
	private static final String IMAGE_WAIT = "wait.gif";
	/**
	 * кнопка Отмена
	 */
	private static final String BUTTON_CANCEL = "/dialog/buttons/cancel";
	
	/**
	 * панель для сообщений
	 */
	private JPanel message_panel;
	
	/**
	 * результат работы
	 */
	private int modal_result = RESULT_OK;
	
	/**
	 * <p>Конструктор.</p>
	 * @param owner - компонент-владелец
	 */
	public WaitForm(Frame owner)
	{
		this(owner, null, false);
	}
	
	/**
	 * <p>Конструктор.</p>
	 * @param owner - компонент-владелец
	 */
	public WaitForm(Dialog owner)
	{
		this(owner, null, false);
	}
	
	/**
	 * <p>Конструктор.</p>
	 * @param owner - компонент-владелец
	 */
	public WaitForm(Window owner)
	{
		this(owner, null, false);
	}
	
	/**
	 * <p>Конструктор.</p>
	 * @param owner - форма-владелец
	 * @param message - сообщение
	 */
	public WaitForm(Frame owner, String message)
	{
		this(owner, message, false);
	}
	
	/**
	 * <p>Конструктор.</p>
	 * @param owner - форма-владелец
	 * @param message - сообщение
	 */
	public WaitForm(Dialog owner, String message)
	{
		this(owner, message, false);
	}
	
	/**
	 * <p>Конструктор.</p>
	 * @param owner - форма-владелец
	 * @param message - сообщение
	 */
	public WaitForm(Window owner, String message)
	{
		this(owner, message, false);
	}
	
	/**
	 * <p>Конструктор</p>
	 * @param owner - форма-владелец
	 * @param message - сообщение
	 * @param cancelable - true, если форма может быть закрыта пользователем
	 */
	public WaitForm(Frame owner, String message, boolean cancelable)
	{
		super(owner, DIALOG_NAME, Resources.getString(TITLE_WAIT, Resources.UNDEFINED_RESOURCE));
		// инициируем форму
		CreateForm(message, cancelable);
		// центруем
		setLocationRelativeTo(main_form);
	}
	
	/**
	 * <p>Конструктор</p>
	 * @param owner - диалог-владелец
	 * @param message - сообщение
	 * @param cancelable - true, если форма может быть закрыта пользователем
	 */
	public WaitForm(Dialog owner, String message, boolean cancelable)
	{
		super(owner, DIALOG_NAME, Resources.getString(TITLE_WAIT, Resources.UNDEFINED_RESOURCE));
		// инициируем форму
		CreateForm(message, cancelable);
		// центруем
		setLocationRelativeTo(main_dialog);
	}
	
	/**
	 * <p>Конструктор</p>
	 * @param owner - окно-владелец
	 * @param message - сообщение
	 * @param cancelable - true, если форма может быть закрыта пользователем
	 */
	public WaitForm(Window owner, String message, boolean cancelable)
	{
		super(owner, DIALOG_NAME, Resources.getString(TITLE_WAIT, Resources.UNDEFINED_RESOURCE));
		// инициируем форму
		CreateForm(message, cancelable);
		// центруем
		setLocationRelativeTo(main_window);
	}
	
	private void CreateForm(String message, boolean cancelable)
	{
		// панель для сообщений
		message_panel = new JPanel();
		GroupLayout layout = new GroupLayout(message_panel);
		message_panel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		Group vert_group = layout.createSequentialGroup();
		Group horz_group = layout.createParallelGroup();
		layout.setVerticalGroup(vert_group);
		layout.setHorizontalGroup(horz_group);
		// картинка
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(new JLabel(Resources.getIcon(IMAGE_WAIT), JLabel.CENTER));
		// кнопка
		JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		if (cancelable)
		{
			JButton button = new JButton(Resources.getString(BUTTON_CANCEL, Resources.UNDEFINED_RESOURCE));
			// реакция на нажатие
			button.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						modal_result = RESULT_CANCEL;
						Close();						
					}
				});
			button_panel.add(button);
		}
		// подложка
		layout = new GroupLayout(getContentPane());
		setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(message_panel)
				.addComponent(panel)
				.addComponent(button_panel));
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(message_panel)
				.addComponent(panel)
				.addComponent(button_panel));
		// минимальные размеры
		setMinimumSize(new Dimension(200, 100));
		// перехват крестика
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// сообщение
		setMessage(message);		
	}
	
	/**
	 * <p>Открывает форму.</p>
	 * @return RESULT_CANCEL, если пользователь нажал кнопку Отмена; RESULT_OK, в противном случае
	 */
	public int Show()
	{
		setVisible(true);
		return modal_result;
	}
	
	/**
	 * <p>Закрывает форму и уничтожает её.</p>
	 */
	public void Close()
	{
		dispose();
	}
	
	/**
	 * <p>Добавляет сообщение на форму.</p>
	 * @param message - сообщение
	 */
	public void setMessage(String message)
	{
		// удаляем старое
		message_panel.removeAll();
		// подложка
		GroupLayout layout = (GroupLayout)message_panel.getLayout();
		// группы подложки
		Group vert_group = layout.createSequentialGroup();
		Group horz_group = layout.createParallelGroup();
		layout.setVerticalGroup(vert_group);
		layout.setHorizontalGroup(horz_group); 
		// разбиваем на строки
		if (message != null)
			for (String line: message.split("\\n"))
			{
				// новая метка
				JLabel label = new JLabel(line, JLabel.CENTER);
				// дабавляем в группы
				vert_group.addComponent(label);
				horz_group.addComponent(label);
			}
		// пересчёт размеров
		pack();
		// центруем
		setLocationRelativeTo(main_form);
	}
}
