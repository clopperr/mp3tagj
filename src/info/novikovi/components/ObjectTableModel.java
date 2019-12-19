package info.novikovi.components;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * <p>Класс управляет отображением информации о представлениях ClearCase.</p>
 */
public class ObjectTableModel implements TableModel
{
	/**
	 * представления
	 */
	private ArrayList<Object[]> objects = null;
	/**
	 * количество строк
	 */
	private int row_count;
	/**
	 * количество столбцов
	 */
	private int col_count;
	/**
	 * заголовки
	 */
	private String[] titles;
	/**
	 * признак разрешения редактировать колонки
	 */
	private boolean[] editable;
	/**
	 * номер поля для поиска
	 */
	private int srch_column = 0;
	/**
	 * идентификатор модели
	 */
	private int id = 0;
	
	/**
	 * слушатели событий
	 */
	private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();

	/**
	 * <p>Конструктор.</p>
	 * @param objects - данные
	 * @param titles - заголовки
	 * @param all_editable - true, чтобы разрешить редактирование всех столбцов
	 */
	public ObjectTableModel(Object[][] objects, String[] titles, boolean all_editable)
	{
		// список баз
		ArrayList<Object[]> data = new ArrayList<Object[]>(objects.length);
		for (Object[] part: objects) data.add(part);
		//
		init(data, titles, all_editable);
	}	
		
	/**
	 * <p>Конструктор.</p>
	 * @param objects - данные
	 * @param titles - заголовки
	 * @param all_editable - true, чтобы разрешить редактирование всех столбцов
	 */
	public ObjectTableModel(ArrayList<Object[]> objects, String[] titles, boolean all_editable)
	{
		init(objects, titles, all_editable);
	}

	/**
	 * <p>Конструктор. Запрещает редактирование столбцов.</p>
	 * @param objects - данные
	 * @param titles - заголовки
	 */
	public ObjectTableModel(Object[][] objects, String[] titles)
	{
		this(objects, titles, false);
	}
	
	/**
	 * <p>Конструктор. Запрещает редактирование столбцов.</p>
	 * @param objects - данные
	 * @param titles - заголовки
	 */
	public ObjectTableModel(ArrayList<Object[]> objects, String[] titles)
	{
		this(objects, titles, false);
	}
	
	/**
	 * <p>Инициализирует экземпляр объекта.</p>
	 * @param objects - данные
	 * @param titles - заголовки
	 * @param all_editable - true, чтобы разрешить редактирование всех столбцов
	 */
	private void init(ArrayList<Object[]> objects, String[] titles, boolean all_editable)
	{
		// данные
		this.objects = objects;
		// количество строк
		row_count = objects.size();
		// количество столбцов
		col_count = row_count == 0 ? 0 : objects.get(0).length;
		// заголовки
		this.titles = titles;
		// редактирование
		editable = new boolean[col_count];
		Arrays.fill(editable, all_editable);
	}
	
	@Override
	public int getRowCount() {return row_count;}

	@Override
	public int getColumnCount() {return col_count;}

	@Override
	public String getColumnName(int columnIndex) {return titles[columnIndex];}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		// при выходе за границы данных возвращаем false
		if (columnIndex < 0 || columnIndex >= col_count) return false;
		return editable[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		// при выходе за границы данных возвращаем null
		if (columnIndex < 0 || columnIndex >= col_count || rowIndex < 0 || rowIndex >= row_count)
			return null;
		return objects.get(rowIndex)[columnIndex];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (columnIndex >= 0 && columnIndex < col_count && rowIndex >= 0 && rowIndex < row_count)
		{
			objects.get(rowIndex)[columnIndex] = aValue;
			// событие
			TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, columnIndex);
			// уведомляем подписчиков
			for (TableModelListener listener: listeners) listener.tableChanged(event);
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l)
	{
		if (!listeners.contains(l)) listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{
		listeners.remove(l);
	}

	/**
	 * <p>Запоминает номер поля для поиска.</p>
	 * @param node_type номер поля для поиска
	 * @return этот объект
	 */
	public ObjectTableModel setSearchColumn(int srch_column)
	{
		this.srch_column = srch_column;
		return this;
	}

	/**
	 * <p>Возвращает номер поля для поиска.</p>
	 * @return номер поля для поиска
	 */
	public int getSearchColumn() {return srch_column;}

	/**
	 * <p>Устанавливает идентификатор модели.</p>
	 * @param id - идентификатор модели
	 */
	public ObjectTableModel setId(int id)
	{
		this.id = id;
		return this;
	}
	
	/**
	 * <p>Возвращает идентификатор модели.</p>
	 * @return идентификатор модели
	 */
	public int getId() {return id;}
	
	@Override
	public Class<? extends Object> getColumnClass(int c)
    {
        return getValueAt(0, c).getClass();
    }
	
	/**
	 * <p>Разрешает или запрещает редактирование колонки.</p>
	 * @param columnIndex - номер колонки
	 * @param is_editable - true, если нужно разрешить редактирование
	 */
	public void setColumnEditable(int columnIndex, boolean is_editable)
	{
		if (columnIndex >= 0 && columnIndex < col_count) 
			editable[columnIndex] = is_editable;
	}
	
	/**
	 * <p>Добавляет строку данных.</p>
	 * @param row - строка данных
	 */
	public void addDataRow(Object[] row)
	{
		// добавляем строку
		objects.add(row);
		row_count++;
		// событие
		TableModelEvent event = new TableModelEvent(this);
		// уведомляем подписчиков
		for (TableModelListener listener: listeners) listener.tableChanged(event);
	}
}
