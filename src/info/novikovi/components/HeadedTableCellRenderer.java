package info.novikovi.components;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import sun.swing.table.DefaultTableCellHeaderRenderer;

/**
 * <p>Переопределяем метод DefaultTableCellRenderer чтоб раскрашивать строки</p> 
 */
public class HeadedTableCellRenderer extends DefaultTableCellHeaderRenderer
{
	private static final long serialVersionUID = 538118547706750041L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
        // Получаем текущую ячейку
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        return l;
	}
}
	