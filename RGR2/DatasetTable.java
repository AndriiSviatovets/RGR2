import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class DatasetTable extends JPanel implements Serializable {
    
    private JTable table;
    private TableXML tableModel;

    // Обов'язковий конструктор без параметрів
    public DatasetTable() {
        initializeComponent();
    }

    private void initializeComponent() {
        setLayout(new BorderLayout());
        
        tableModel = new TableXML();
        table = new JTable(tableModel);
        
        // Додаємо скролл, щоб працювали заголовки і прокрутка великих обсягів даних
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- API нашого JavaBean ---

    public void setDataset(List<BabyName> data) {
        tableModel.setData(data);
    }

    public List<BabyName> getDataset() {
        return tableModel.getData();
    }

    public void addRecord(BabyName record) {
        tableModel.addRow(record);
    }

    public void deleteSelectedRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Конвертуємо індекс візуальної таблиці в індекс моделі (якщо застосовано сортування стовпців)
            int modelRow = table.convertRowIndexToModel(selectedRow);
            tableModel.removeRow(modelRow);
        } else {
            JOptionPane.showMessageDialog(this, "Оберіть рядок для видалення", "Увага", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Метод для підключення графіка на наступних етапах (передача подій)
    public TableModel getTableModel() {
        return tableModel;
    }
}