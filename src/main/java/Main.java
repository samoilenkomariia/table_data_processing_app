import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class Main extends JFrame {
    MyTableClass table = MyTableClass.getInstance();
    private final JMenuItem openFileItem, saveFileItem, saveFileAsItem, newRowItem, newColumnItem;

    public Main() {
        setTitle("Таблиця");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        openFileItem = new JMenuItem("Відкрити...");
        saveFileItem = new JMenuItem("Зберегти");
        saveFileAsItem = new JMenuItem("Зберегти як...");
        fileMenu.add(openFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.add(saveFileAsItem);

        JMenu editMenu = new JMenu("Редагування");
        menuBar.add(editMenu);

        newRowItem = new JMenuItem("Новий рядок");
        newColumnItem = new JMenuItem("Новий стовпець");
        editMenu.add(newRowItem);
        editMenu.add(newColumnItem);


        table.readFile(new File("table.txt"));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        pack();
        setVisible(true);

        manageMenuItems();
    }

    private void manageMenuItems() {
        openFileItem.addActionListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Оберіть файл");
            fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
            int userSelection = fileChooser.showOpenDialog(this);

            if(userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    processSelectedFile(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Оберіть файл з розширенням .txt",
                            "Неправильний формат файлу",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        saveFileItem.addActionListener(event -> {
            table.writeFile(table.getFile());
        });

        saveFileAsItem.addActionListener(event -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Зберегти файл");
                fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
                int userSelection = fileChooser.showSaveDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                        fileToSave = new File(fileToSave.getPath() + ".txt");
                    }
                    table.setFile(fileToSave);
                    table.writeFile(fileToSave);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        newRowItem.addActionListener(event -> {
            table.addRow();
        });

        newColumnItem.addActionListener(event -> {
            table.addColumn();
        });
    }

    private void processSelectedFile(File file) {
        table.clearTable();
        table.setFile(file);
        table.readFile(file);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
