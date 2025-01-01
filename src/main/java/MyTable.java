import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.util.ArrayList;
import java.util.List;

public class MyTable {
    private List<List<Object>> data = new ArrayList<>();
    private List<Object> columnNames = !data.isEmpty() ? data.get(0) : new ArrayList<>();
    private UndoManager undoManager;

    public MyTable() {
        this.undoManager = new UndoManager();
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }

    public void clear () {
        data.clear();
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Object oldValue = getValueAt(rowIndex, columnIndex);
        if (oldValue.equals(value)) return;
        data.get(rowIndex).set(columnIndex, value);

        undoManager.addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() {
                try {
                    super.undo();
                    data.get(rowIndex).set(columnIndex, oldValue);
                } catch(CannotUndoException ignored) {}
            }

            @Override
            public void redo() {
                try {
                    super.redo();
                    data.get(rowIndex).set(columnIndex, value);
                } catch(CannotRedoException ignored) {}
            }
        });
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public void addRow(List<Object> rowData)  {
        List<List<Object>> oldData = new ArrayList<>();
        for (List<Object> row : data) {
            oldData.add(new ArrayList<>(row));
        }
        if (data.isEmpty()) {
            this.columnNames = new ArrayList<>(rowData);
            data.add(rowData);
        }
        else if (rowData.size() == columnNames.size()) {
            data.add(rowData);
        } else {
            List<Object> newData = new ArrayList<>(rowData);
            while (newData.size() < columnNames.size()) {
                newData.add("");
            }
            data.add(newData);
        }

        undoManager.addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() {
                try {
                    super.undo();
                    data = oldData;
                } catch(CannotUndoException ignored) {}
            }

            @Override
            public void redo() {
                try {
                    super.redo();
                    List<Object> row = new ArrayList<>();
                    for (int i = 0; i < columnNames.size(); i++) {
                        row.add("");
                    }
                    data.add(row);
                } catch(CannotRedoException ignored) {}
            }
        });
    }

    public void addColumn() {
        List<List<Object>> oldData = new ArrayList<>();
        for (List<Object> row : data) {
            oldData.add(new ArrayList<>(row));
        }
        List<List<Object>> res = new ArrayList<>();
        for(List<Object> row : data) {
            List<Object> newData = new ArrayList<>(row);
            newData.add("");
            res.add(newData);
        }
        data = res;
        columnNames = new ArrayList<>(data.get(0));

        undoManager.addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() {
                try {
                    super.undo();
                    data = oldData;
                    columnNames = new ArrayList<>(data.get(0));
                } catch(CannotUndoException ignored) {}
            }

            @Override
            public void redo() {
                try {
                    super.redo();
                    data = res;
                    columnNames = new ArrayList<>(data.get(0));
                } catch(CannotRedoException ignored) {}
            }
        });
    }

    public void removeRow(int rowIndex) {
        data.remove(rowIndex);
    }

}
