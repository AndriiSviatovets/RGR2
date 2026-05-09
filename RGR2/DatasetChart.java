import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class DatasetChart extends JPanel implements Serializable, TableModelListener {

    // Змінюємо тип на TableXML
    private TableXML tableModel;

    public DatasetChart() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 400));
    }

    // Змінюємо тип в аргументах методу на TableXML
    public void setTableModel(TableXML model) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(this);
        }
        this.tableModel = model;
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(this);
        }
        repaint();
     // Оновлюємо графік при підключенні нових даних
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        // Якщо дані в таблиці змінилися - перемальовуємо графік
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        if (tableModel == null || tableModel.getData().isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 40;

        // Малюємо осі (X - Rank, Y - Count)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // Вісь X
        g2d.drawLine(padding, height - padding, padding, padding); // Вісь Y

        // Підписи осей
        g2d.drawString("Рейтинг (Rank)", width / 2, height - 10);
        
        Graphics2D g2dRotated = (Graphics2D) g2d.create();
        g2dRotated.rotate(-Math.PI / 2);
        g2dRotated.drawString("Кількість (Count)", -height / 2 - 20, 20);
        g2dRotated.dispose();

        // Знаходимо максимальні значення для масштабування графіка
        List<BabyName> data = tableModel.getData();
        int maxRank = data.stream().mapToInt(BabyName::getRank).max().orElse(1);
        int maxCount = data.stream().mapToInt(BabyName::getCount).max().orElse(1);

        if (maxRank == 0) maxRank = 1;
        if (maxCount == 0) maxCount = 1;

        // Малюємо точки (червоні кружечки, як на скріншоті)
        g2d.setColor(Color.RED);
        for (BabyName record : data) {
            // Вираховуємо координати з урахуванням відступів та масштабу
            int x = padding + (int) ((double) record.getRank() / maxRank * (width - 2 * padding));
            int y = height - padding - (int) ((double) record.getCount() / maxCount * (height - 2 * padding));
            
            g2d.fillOval(x - 4, y - 4, 8, 8); // Малюємо точку
        }
    }
}