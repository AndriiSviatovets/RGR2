import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BabyNamesTableModel extends AbstractTableModel implements Serializable {
    
    private final String[] columnNames = {"Ім'я", "Стать", "Кількість", "Рейтинг"};
    private List<BabyName> dataList;

    public BabyNamesTableModel() {
        this.dataList = new ArrayList<>();
    }

    public void setData(List<BabyName> dataList) {
        this.dataList = dataList;
        fireTableDataChanged(); // Оновлює таблицю при завантаженні нових даних
    }

    public List<BabyName> getData() {
        return dataList;
    }

    // Методи для додавання та видалення рядків
    public void addRow(BabyName babyName) {
        dataList.add(babyName);
        fireTableRowsInserted(dataList.size() - 1, dataList.size() - 1);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < dataList.size()) {
            dataList.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    @Override
    public int getRowCount() {
        return dataList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Дозволяємо редагувати всі комірки
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; 
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BabyName record = dataList.get(rowIndex);
        switch (columnIndex) {
            case 0: return record.getName();
            case 1: return record.getGender();
            case 2: return record.getCount();
            case 3: return record.getRank();
            default: return null;
        }
    }

    // Логіка збереження змін після редагування комірки користувачем
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        BabyName record = dataList.get(rowIndex);
        try {
            switch (columnIndex) {
                case 0: record.setName(aValue.toString()); break;
                case 1: record.setGender(aValue.toString()); break;
                case 2: record.setCount(Integer.parseInt(aValue.toString())); break;
                case 3: record.setRank(Integer.parseInt(aValue.toString())); break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (NumberFormatException e) {
            // Ігноруємо неправильний ввід тексту замість чисел
        }
    }
}