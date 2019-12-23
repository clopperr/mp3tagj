package info.novikovi.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * <p>Переопределяем метод DefaultTableCellRenderer чтоб раскрашивать строки</p> 
 */
public class TableCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 538118547706750041L;

	private CellChecker checker;
	
	public TableCellRenderer(CellChecker checker)
	{
		if (checker == null) throw new IllegalArgumentException("null checker");
		this.checker = checker;
	}
	
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
        // Получаем текущую ячейку
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        // выбираем цвет
        l.setForeground(checker.isValidCell(row, col) ? Color.BLACK : Color.RED);
        return l;
	}
}
	