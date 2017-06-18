import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Zachary on 6/18/2017.
 *
 * See: http://forum.codecall.net/topic/49721-simple-text-editor/
 */
class TextEditor extends JFrame {
    private JTextArea area = new JTextArea(20, 120);

    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));

    private String currentFile = "Untitled";

    private boolean changed = false;

    private KeyListener anyKeyPressed = new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
            changed = true;
            Save.setEnabled(true);
            SaveAs.setEnabled(true);
        }
    };

    Action New = new AbstractAction("New", new ImageIcon("new.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveOld();
            area.setText("");
            setTitle(currentFile);
            changed = false;
            Save.setEnabled(false);
            SaveAs.setEnabled(false);
        }
    };

    Action Open = new AbstractAction("Open", new ImageIcon("open.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveOld();
            if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                readInFile(dialog.getSelectedFile().getAbsolutePath());
            }
            SaveAs.setEnabled(true);
        }
    };

    Action Save = new AbstractAction("Save", new ImageIcon("save.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentFile.equals("Untitled")) {
                saveFileAs();
            }
            else {
                saveFile(currentFile);
            }
        }
    };

    Action SaveAs = new AbstractAction("Save as...") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFileAs();
        }
    };

    Action Quit = new AbstractAction("Quit") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveOld();
            System.exit(0);
        }
    };

    ActionMap actionMap = area.getActionMap();
    Action Cut = actionMap.get(DefaultEditorKit.cutAction);
    Action Copy = actionMap.get(DefaultEditorKit.copyAction);
    Action Paste = actionMap.get(DefaultEditorKit.pasteAction);

    private void saveFile(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            area.write(fileWriter);
            fileWriter.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
            Save.setEnabled(false);
        }
        catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Error occurred while writing to: " + fileName);
        }
    }

    private void saveFileAs() {
        if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            saveFile(dialog.getSelectedFile().getAbsolutePath());
        }
    }

    private void saveOld() {
        if (changed) {
            if (JOptionPane.showConfirmDialog(this, "Would you like to save" +
            currentFile + "?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                saveFile(currentFile);
            }
        }
    }

    private void readInFile(String fileName) {
        try {
            FileReader fileReader = new FileReader(fileName);
            area.read(fileReader, null);
            fileReader.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
        }
        catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Editor can't find the file: " + fileName);
        }
    }

    public TextEditor() {
        // Set default font
        area.setFont(new Font("Monospaced",Font.PLAIN,12));

        // Allow vertical and horizontal scrolling in text area
        JScrollPane scrollPane = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Create toolbar at the top of the editor
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        menuBar.add(file);
        menuBar.add(edit);

        file.add(New);
        file.add(Open);
        file.add(Save);
        file.add(Quit);
        file.add(SaveAs);
        file.addSeparator();

        for (int i = 0; i < file.getItemCount() - 1; i++) {
            file.getItem(i).setIcon(null);
        }

        edit.add(Cut);
        edit.add(Copy);
        edit.add(Paste);

        edit.getItem(0).setText("Cut out");
        edit.getItem(1).setText("Copy");
        edit.getItem(2).setText("Paste");

        JToolBar toolBar = new JToolBar();
        add(toolBar,BorderLayout.NORTH);
        toolBar.add(New);
        toolBar.add(Open);
        toolBar.add(Save);
        toolBar.addSeparator();
        JButton cut = toolBar.add(Cut);
        JButton copy = toolBar.add(Copy);
        JButton paste = toolBar.add(Paste);

        Save.setEnabled(false);
        SaveAs.setEnabled(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        area.addKeyListener(anyKeyPressed);
        setTitle(currentFile);
        setVisible(true);
    }

    public static void main(String[] args) {
        new TextEditor();
    }
}
