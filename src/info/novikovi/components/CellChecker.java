package info.novikovi.components;

/**
 * <p>Интерфейс для проверки качества данных в ячейке.</p>
 */
public interface CellChecker
{
	/**
	 * <p>Проверяет корректность данных в ячейке. Если ячейка не существует, данные считаются корректными.</p>
	 * @param row номер строки
	 * @param column номер столбца
	 * @return true, если данные корректны
	 */
	public boolean isValidCell(int row, int column);
}
