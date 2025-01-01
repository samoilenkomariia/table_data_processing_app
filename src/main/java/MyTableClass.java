import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

public class MyTableClass extends JComponent {

    private static MyTableClass instance;
    private static MyTable tableModel;
    private int rowHeight = 20;
    private int columnWidth = 100;
    private JTextField editorField;
    private int selectedRow = -1;
    private int selectedColumn = -1;
    private final File defaultFile = new File("table.txt");
    private File file = defaultFile;

    private MyTableClass() {
        tableModel = new MyTable();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / rowHeight;
                int column = e.getX() / columnWidth;
                startEditingCell(row, column);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                columnWidth = getWidth() / tableModel.getColumnCount();
                repaint();
            }
        });

        {
            editorField = new JTextField();
            editorField.setVisible(false);
            editorField.addActionListener(e -> stopEditingCell(true));
            editorField.addFocusListener((new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent focusEvent) {
                    stopEditingCell(false);
                }
            }));
            setLayout(null);
            add(editorField);

            InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = getActionMap();

            inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancelEditing");
            actionMap.put("cancelEditing", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stopEditingCell(false);
                }
            });

            inputMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
            actionMap.put("undo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        tableModel.getUndoManager().undo();
                        columnWidth = getWidth() / tableModel.getColumnCount();
                        repaint();
                    } catch (CannotUndoException ignored) {}
                }
            });

            inputMap.put(KeyStroke.getKeyStroke("control Y"), "redo");
            actionMap.put("redo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        tableModel.getUndoManager().redo();
                        columnWidth = getWidth() / tableModel.getColumnCount();
                        repaint();
                    } catch (Exception ignored) {}
                }
            });
        }
    }

    private void startEditingCell(int row, int column) {
        if (row < 0 || row >= tableModel.getRowCount() ||
        column < 0 || column >= tableModel.getColumnCount()) {
            stopEditingCell(false);
            return;
        }
        selectedRow = row;
        selectedColumn = column;

        int x = column * columnWidth;
        int y = row * rowHeight;
        editorField.setBounds(x, y, columnWidth, rowHeight);

        Object value = tableModel.getValueAt(row, column);
        editorField.setText(value != null ? value.toString() : "");
        editorField.setVisible(true);
        editorField.requestFocus();
    }

    private void stopEditingCell(boolean saveValue) {
        if (selectedRow == -1 || selectedColumn == -1) return;

        if (saveValue) {
            String newValue = editorField.getText();
            tableModel.setValueAt(newValue, selectedRow, selectedColumn); //error here
        }

        editorField.setVisible(false);
        selectedRow = -1;
        selectedColumn = -1;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int width = getWidth() == 0 ? tableModel.getColumnCount() * columnWidth : columnWidth;
        if (width < 100*tableModel.getColumnCount()) width = 100*tableModel.getColumnCount();
        int height = tableModel.getRowCount() * rowHeight;
        return new Dimension(width, height);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                int x = col * columnWidth;
                int y = row * rowHeight;
                g2d.drawRect(x, y, columnWidth, rowHeight);
                Object value = tableModel.getValueAt(row, col);
                if (value != null) g2d.drawString(value.toString(), x+5, y+rowHeight-5);
            }
        }
    }

    public void writeFile(File file) {
        try(BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, false))) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    Object value = tableModel.getValueAt(row, col);
                    writer.write(value != null ? value.toString() : "");
                    if (col < tableModel.getColumnCount() - 1) {
                        writer.write("\t");
                    }
                }
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFile(File file) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while((line = reader.readLine()) != null) {
                if(line.contains("\t")) {
                    String[] parts = line.split("\t");
                    tableModel.addRow(Arrays.asList(parts));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTable() {
        tableModel.clear();
        repaint();
    }

    public void addRow() {
        tableModel.addRow(Arrays.asList());
        columnWidth = getWidth() / tableModel.getColumnCount();
        repaint();
    }

    public void addColumn() {
        tableModel.addColumn();
        columnWidth = getWidth() / tableModel.getColumnCount();
        repaint();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static MyTableClass getInstance() {
        if (instance == null) {
            instance = new MyTableClass();
        }
        return instance;
    }

}
