import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TableTester {

    public static void main(String[] args) {
        // Запуск Swing-додатку в правильному потоці обробки подій
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // 1. Створення головного вікна
        JFrame frame = new JFrame("Тест компоненту таблиці (DatasetTable)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // Вікно по центру екрану
        frame.setLayout(new BorderLayout());

        // 2. Ініціалізація нашого JavaBean компоненту
        DatasetTable tableComponent = new DatasetTable();

        // Створення фіктивних даних для перевірки
        List<BabyName> dummyData = new ArrayList<>();
        dummyData.add(new BabyName("Liam", "MALE", 703, 1));
        dummyData.add(new BabyName("Olivia", "FEMALE", 534, 1));
        dummyData.add(new BabyName("Noah", "MALE", 698, 2));
        dummyData.add(new BabyName("Emma", "FEMALE", 501, 2));
        
        // Завантаження даних у компонент
        tableComponent.setDataset(dummyData);

        // Додаємо таблицю в центр вікна
        frame.add(tableComponent, BorderLayout.CENTER);

        // 3. Створення панелі з кнопками для перевірки API компоненту
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Додати новий запис");
        JButton btnDelete = new JButton("Видалити обране");

        // Обробник для кнопки додавання
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Додаємо порожній або дефолтний запис, який користувач потім зможе відредагувати
                BabyName newRecord = new BabyName("Нове Ім'я", "UNKNOWN", 0, 999);
                tableComponent.addRecord(newRecord);
            }
        });

        // Обробник для кнопки видалення
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableComponent.deleteSelectedRecord();
            }
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        // Додаємо панель з кнопками вниз
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 4. Відображення вікна
        frame.setVisible(true);
    }
}