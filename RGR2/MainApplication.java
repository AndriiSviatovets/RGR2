import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainApplication extends JFrame {

    private DatasetTable tableComponent;
    private DatasetChart chartComponent;

    public MainApplication() {
        setTitle("Приклад JavaBeans - Baby Names Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        tableComponent = new DatasetTable();
        chartComponent = new DatasetChart();

        chartComponent.setTableModel((TableXML) tableComponent.getTableModel());

        // --- ЛІВА ПАНЕЛЬ ---
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(tableComponent, BorderLayout.CENTER);

        JPanel addDelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAdd = new JButton("Add (+)");
        JButton btnDel = new JButton("Del (-)");
        addDelPanel.add(btnAdd);
        addDelPanel.add(btnDel);
        leftPanel.add(addDelPanel, BorderLayout.SOUTH);

        // --- ПРАВА ПАНЕЛЬ ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, chartComponent);
        splitPane.setDividerLocation(450);
        add(splitPane, BorderLayout.CENTER);

        // --- НИЖНЯ ПАНЕЛЬ ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnImport = new JButton("Створити та відфільтрувати (XML)");
        JButton btnOpen = new JButton("Відкрити готовий XML файл");
        JButton btnSave = new JButton("Зберегти");
        JButton btnClear = new JButton("Очистити");
        JButton btnExit = new JButton("Завершити");

        bottomPanel.add(btnImport);
        bottomPanel.add(btnOpen);
        bottomPanel.add(btnSave);
        bottomPanel.add(btnClear);
        bottomPanel.add(btnExit);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- ОБРОБНИКИ ПОДІЙ ---
        btnAdd.addActionListener(e -> tableComponent.addRecord(new BabyName("Нове Ім'я", "UNKNOWN", 10, 1)));
        btnDel.addActionListener(e -> tableComponent.deleteSelectedRecord());
        btnClear.addActionListener(e -> tableComponent.setDataset(new ArrayList<>()));
        btnExit.addActionListener(e -> System.exit(0));

        // 1. ІМПОРТ (SAX Парсер - витягування даних з великого файлу)
        btnImport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Оберіть великий датасет (Popular_Baby_Names_NY.xml)");
            
            if (fileChooser.showOpenDialog(MainApplication.this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                // Діалогові вікна для вводу параметрів фільтрації
                String targetEthnicity = JOptionPane.showInputDialog(MainApplication.this, 
                        "Введіть етнічну групу (напр. HISPANIC):", "HISPANIC");
                if (targetEthnicity == null || targetEthnicity.trim().isEmpty()) return;

                String limitStr = JOptionPane.showInputDialog(MainApplication.this, 
                        "Введіть максимальну кількість записів:", "15");
                if (limitStr == null || limitStr.trim().isEmpty()) return;

                int maxRecords;
                try {
                    maxRecords = Integer.parseInt(limitStr.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainApplication.this, "Некоректне число!", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Запуск SAX парсера
                try {
                    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                    saxFactory.setValidating(false);
                    SAXParser saxParser = saxFactory.newSAXParser();
                    
                    List<BabyName> filteredList = new ArrayList<>();
                    
                    // Створюємо обробник подій SAX прямо тут
                    DefaultHandler handler = new DefaultHandler() {
                        String tempName, tempGender, tempEthnicity;
                        int tempCount, tempRank;
                        StringBuilder textBuffer = new StringBuilder();

                        @Override
                        public void startElement(String uri, String localName, String qName, Attributes attributes) {
                            textBuffer.setLength(0);
                        }

                        @Override
                        public void characters(char[] ch, int start, int length) {
                            textBuffer.append(ch, start, length);
                        }

                        @Override
                        public void endElement(String uri, String localName, String qName) {
                            String text = textBuffer.toString().trim();
                            switch (qName.toLowerCase()) {
                                case "ethcty": tempEthnicity = text; break;
                                case "nm": tempName = text; break;
                                case "gndr": tempGender = text; break;
                                case "cnt": if (!text.isEmpty()) tempCount = Integer.parseInt(text); break;
                                case "rnk": if (!text.isEmpty()) tempRank = Integer.parseInt(text); break;
                                case "row":
                                    if (tempName != null && tempEthnicity != null) {
                                        if (tempEthnicity.equalsIgnoreCase(targetEthnicity.trim())) {
                                            filteredList.add(new BabyName(tempName, tempGender, tempCount, tempRank));
                                        }
                                        tempName = null; tempEthnicity = null;
                                    }
                                    break;
                            }
                        }
                    };

                    saxParser.parse(selectedFile, handler);

                    // Сортування та обрізка списку
                    Collections.sort(filteredList);
                    int actualLimit = Math.min(maxRecords, filteredList.size());
                    List<BabyName> topNames = new ArrayList<>(filteredList.subList(0, actualLimit));

                    tableComponent.setDataset(topNames);
                    JOptionPane.showMessageDialog(MainApplication.this, 
                            "Успішно відфільтровано " + topNames.size() + " записів.", "Імпорт завершено", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainApplication.this, "Помилка SAX парсингу: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 2. ВІДКРИТТЯ (DOM Парсер - читання раніше збережених відредагованих файлів)
        btnOpen.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Оберіть збережений XML файл для відкриття");
            
            if (fileChooser.showOpenDialog(MainApplication.this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(selectedFile);
                    document.getDocumentElement().normalize();

                    NodeList nodeList = document.getElementsByTagName("name_record");
                    List<BabyName> loadedData = new ArrayList<>();

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String name = element.getElementsByTagName("name").item(0).getTextContent();
                            String gender = element.getElementsByTagName("gender").item(0).getTextContent();
                            int count = Integer.parseInt(element.getElementsByTagName("count").item(0).getTextContent());
                            int rank = Integer.parseInt(element.getElementsByTagName("rank").item(0).getTextContent());

                            loadedData.add(new BabyName(name, gender, count, rank));
                        }
                    }

                    tableComponent.setDataset(loadedData);
                    JOptionPane.showMessageDialog(MainApplication.this, "Дані успішно завантажено!", "Успіх", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainApplication.this, "Помилка читання файлу: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 3. ЗБЕРЕЖЕННЯ (DOM Парсер - збереження поточного стану таблиці у файл)
        btnSave.addActionListener(e -> {
            List<BabyName> currentData = tableComponent.getDataset();
            if (currentData == null || currentData.isEmpty()) {
                JOptionPane.showMessageDialog(MainApplication.this, "Немає даних для збереження!", "Увага", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Зберегти як XML");
            fileChooser.setSelectedFile(new File("Edited_Names.xml"));

            if (fileChooser.showSaveDialog(MainApplication.this) == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                    Document doc = docBuilder.newDocument();

                    Element rootElement = doc.createElement("popular_names");
                    doc.appendChild(rootElement);

                    for (BabyName bn : currentData) {
                        Element recordElement = doc.createElement("name_record");
                        rootElement.appendChild(recordElement);

                        Element name = doc.createElement("name");
                        name.appendChild(doc.createTextNode(bn.getName()));
                        recordElement.appendChild(name);

                        Element gender = doc.createElement("gender");
                        gender.appendChild(doc.createTextNode(bn.getGender()));
                        recordElement.appendChild(gender);

                        Element count = doc.createElement("count");
                        count.appendChild(doc.createTextNode(String.valueOf(bn.getCount())));
                        recordElement.appendChild(count);

                        Element rank = doc.createElement("rank");
                        rank.appendChild(doc.createTextNode(String.valueOf(bn.getRank())));
                        recordElement.appendChild(rank);
                    }

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(fileToSave);
                    transformer.transform(source, result);

                    JOptionPane.showMessageDialog(MainApplication.this, "Файл успішно збережено:\n" + fileToSave.getAbsolutePath(), "Успіх", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainApplication.this, "Помилка при збереженні: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new MainApplication().setVisible(true));
    }
}