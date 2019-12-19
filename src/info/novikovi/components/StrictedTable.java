package info.novikovi.components;

import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * <p>Обёртка JTable с ограничением реакций на нажатие клавиш.</p>
 */
public class StrictedTable extends JTable 
{
	private static final long serialVersionUID = 6369058296962280192L;
	/**
	 * максимальная задержка между нажатиями клавиш при инкрементальном поиске, мс
	 */
	private static final int MAX_INC_SEARCH_INTERVAL = 1000;

	/**
	 * уведомление об изменении поисковой строки
	 */
	IncSearchListener inc_srch_listener = null;
	
	/**
	 * реализация инкрементального поиска
	 */
	private KeyListener inc_search = new KeyListener()
    {
		/**
		 * время последнего ввода
		 */
		long last_type = Calendar.getInstance().getTime().getTime();
		/**
		 * набранная строка
		 */
		StringBuffer input = new StringBuffer();
		
		@Override
		public void keyTyped(KeyEvent event)
		{
			// таблица
			JTable table = (JTable)event.getSource();
			// обрабатываем отображаемые клавиши
			if (event.getKeyChar() >= ' ')
			{
				long now_type = Calendar.getInstance().getTime().getTime();
				// если прошло слишком много времени, сбрасываем ввод
				if (now_type - last_type > MAX_INC_SEARCH_INTERVAL)
					input.setLength(0);
				last_type = now_type;
				// добавляем символ во ввод
				input.append(event.getKeyChar());			
				// ищем строку
				inc_srch_listener.NewSearchValue(input.toString(), table);
			}
		}
		
		@Override
		public void keyReleased(KeyEvent arg0) {}
		
		@Override
		public void keyPressed(KeyEvent arg0) {}
	};
	
	/**
	 * вставка между столбцами
	 */
	private int value_space = 6;
	
	public StrictedTable()
	{
		super();		
	}

	public StrictedTable(TableModel dm)
	{
		super(dm);
	}

	public StrictedTable(TableModel dm, TableColumnModel cm)
	{
		super(dm, cm);
	}

	public StrictedTable(int numRows, int numColumns)
	{
		super(numRows, numColumns);
	}

	public StrictedTable(Vector<?> rowData, Vector<?> columnNames)
	{
		super(rowData, columnNames);
	}

	public StrictedTable(Object[][] rowData, Object[] columnNames)
	{
		super(rowData, columnNames);
	}

	public StrictedTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
	{
		super(dm, cm, sm);
	}
	
	/**
	 * <p>Включает инкрементальный поиск.</p>
	 * @param listener подписка на уведомление об изменении поисковой строки
	 */
	public void setIncSearchOn(IncSearchListener listener)
	{
		// запоминаем подписчика
		inc_srch_listener = listener;
		// добавляем подписку на нажатие клавиш
		addKeyListener(inc_search);
	}
	
	/**
	 * <p>Отключает инкрементальный поиск.</p>
	 */
	public void setIncSearchOff()
	{
		// сбрасываем подписки
		inc_srch_listener = null;
		removeKeyListener(inc_search);
	}
	
	/**
	 * <p>Устанавливает правильные размеры колонок.</p>
	 */
	public void ApplyBestFit()
	{
		// обрабатываем колонки по одной
		for (int i = 0; i < getColumnModel().getColumnCount(); i++)
		{
			// размер колонки
			adjustColumn(i);
		}
	}
	
	/**
	 * <p>Устанавливает размер колонки у учётом содержимого.</>
	 * @param column номер колонки
	 */
	public void adjustColumn(final int column)
	{
		// если колонка не должна изменяться, ничего не делаем
		if (!getColumnModel().getColumn(column).getResizable()) return;
		// ширина заголовка
		int columnHeaderWidth = getColumnHeaderWidth(column);
		// ширина данных
		int columnDataWidth = getColumnDataWidth(column);
		// выбираем большую
		if (columnDataWidth < columnHeaderWidth) columnDataWidth = columnHeaderWidth;
		// устанавливаем новую ширину
		updateColumn(column, columnDataWidth + value_space);
	}
	
	/**
	 * <p>Устанавливает новую ширину колонки</p>
	 * @param column - номер колонки
	 * @param width - новая ширина
	 */
	private void updateColumn(int column, int width)
	{
		// колонка
		final TableColumn tableColumn = getColumnModel().getColumn(column);
		// если нельзя менять, ничего не делаем
		if (! tableColumn.getResizable()) return;
		// колдунство, без которого ширина не меняется
		getTableHeader().setResizingColumn(tableColumn);
		// устанавливаем ширину
		tableColumn.setWidth(width);
	}
	
	/**
	 * <p>Рассчитывает ширину заголовка колонки.</p>
	 * @param column номер колонки
	 * @return ширина заголовка
	 */
	private int getColumnHeaderWidth(int column)
	{
		// колонка
		TableColumn tableColumn = getColumnModel().getColumn(column);
		// заголовок
		Object value = tableColumn.getHeaderValue();
		// если заголовок не задан, его ширина 0
		if (value == null) return 0;
		// отрисовщик
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();
		if (renderer == null) renderer = getTableHeader().getDefaultRenderer();
		// отрисовщик ячейки
		Component c = renderer.getTableCellRendererComponent(this, value, false, false, -1, column);
		// возвращаем ширину
		return c.getPreferredSize().width;
	}
	
	/**
	 * <p>Рассчитывает максимальную ширину ячейки в колонке.</p>
	 * @param column - номер колонки
	 * @return ширина ячейки
	 */
	private int getColumnDataWidth(int column)
	{
		// предпочитаемая ширина
		int preferredWidth = 0;
		// максимальная допустимая ширина
		int maxWidth = getColumnModel().getColumn(column).getMaxWidth();
		// перебираем данные
		for (int row = 0; row < getRowCount(); row++)
		{
			// новая ширина
    		preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));
			// если достигли максимальной, останавливаемся
			if (preferredWidth >= maxWidth) break;
		}
		// возвращаем ширину
		return preferredWidth;
	}
	
	/**
	 * <p>Рассчитывает ширину ячейки с учётом содержимого.</p>
	 * @param row - номер строки
	 * @param column - номер колонки
	 * @return ширина ячейки
	 */
	private int getCellDataWidth(int row, int column)
	{
		// отрисовщик
		Component c = prepareRenderer(getCellRenderer(row, column), row, column);
		// ширина
		return c.getPreferredSize().width + getIntercellSpacing().width;
	}
	
	/**
	 * <p>Callback для реализации инкрементального поиска.</p>
	 */
	public interface IncSearchListener
	{
		/**
		 * <p>Уведомляет подписчика о новом поисковом значении.</p>
		 * @param new_value новое значения для поска
		 * @param table таблица
		 */
		public void NewSearchValue(String new_value, JTable table);
	}
	
	/**
	 * <p>Устанавливает расстояние между столбцами для выравнивания ширин столбцов.</p>
	 * @param space - расстояние между столбцами
	 */
	public void setValueSpace(int space) {value_space = space;}
	
	/**
	 * <p>Возвращает расстояние между столбцами для выравнивания ширин столбцов.</p>
	 * @return расстояние между столбцами
	 */
	public int getValueSpace() {return value_space;}
}
