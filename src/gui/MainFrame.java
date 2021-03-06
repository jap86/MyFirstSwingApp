package gui;

import controller.PersonController;
import gui.listeners.PersonTableListenerImpl;
import gui.listeners.ToolbarListenerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    private Toolbar toolbar;
    private FormPanel formPanel;
    private JFileChooser fileChooser;
    private TablePanel tablePanel;
    private PreferenceDialog preferenceDialog;
    private PersonController personController;
    private Preferences preferences;
    private JTabbedPane tabPane;
    private MessagePanel messagePanel;
    private JSplitPane splitPane;

    public MainFrame() {
        super("Hello World");

        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());

        toolbar = new Toolbar();
        formPanel = new FormPanel();
        tablePanel = new TablePanel();
        preferenceDialog = new PreferenceDialog(this);
        personController = new PersonController();
        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new PersonFileFilter());
        preferences = Preferences.userRoot().node("db");
        tabPane = new JTabbedPane();
        messagePanel = new MessagePanel(this);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tabPane);

        tabPane.addTab(TabOverview.TAB_PERSON_DATABASE_NAME, tablePanel);
        tabPane.addTab(TabOverview.TAB_MESSAGES_NAME, messagePanel);

        tabPane.addChangeListener(event -> {
            int tabIndex = tabPane.getSelectedIndex();
            if (tabIndex == TabOverview.TAB_MESSAGES_NR) {
                messagePanel.refresh();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                exitApplication();
            }
        });

        addTableListener();
        addToolbarListener();
        addFormListener();
        addPreferenceListener();

        setDefaultsPreferenceDialogue();

        add(toolbar, BorderLayout.NORTH);
        add(splitPane);

        setSize(600, 500);
        setMinimumSize(new Dimension(500, 400));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    private void exitApplication() {
        int action = JOptionPane.showConfirmDialog(
                MainFrame.this,
                "Do you really want to exit?",
                "Confirm exit",
                JOptionPane.OK_CANCEL_OPTION);

        if (action == JOptionPane.OK_OPTION){
            dispose(); // Quits automatically.
            try {
                personController.disconnectDatabase();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            System.out.println("Window closing");
        }
    }

    private void addToolbarListener() {
        toolbar.setToolbarListener(new ToolbarListenerImpl(
                personController,
                tablePanel,
                messagePanel,
                toolbar
        ) {
        });
    }

    private void addTableListener() {
        tablePanel.setPersonTableListener(new PersonTableListenerImpl(personController, tablePanel, toolbar));
    }

    private void addFormListener() {
        formPanel.setFormListener(formEvent -> {
            personController.addPerson(createFormPerson(formEvent));
            tablePanel.fillTable(personController.getFormPersonList());
            tablePanel.refresh();
        });
    }

    private void addPreferenceListener() {
        preferenceDialog.setPreferencesListener(
                (String user, String password, Integer port) -> {
                    preferences.put("user", user);
                    preferences.put("password", password);
                    preferences.putInt("port", port);
                }
        );
    }

    private void setDefaultsPreferenceDialogue() {
        preferenceDialog.setDefaults(
                preferences.get("user", ""),
                preferences.get("password", ""),
                preferences.getInt("port", 3306)
        );
    }

    private FormPerson createFormPerson(FormEvent formEvent) {
        FormPerson formPerson = new FormPerson();
        formPerson.name = formEvent.getName();
        formPerson.occupation = formEvent.getOccupation();
        formPerson.ageCategory = formEvent.getAgeCategory();
        formPerson.maritalStatus = formEvent.getMaritalStatus();
        formPerson.gender = formEvent.getGender();
        formPerson.isClubMember = formEvent.isClubMember();
        formPerson.memberId = formEvent.getMemberID();

        return formPerson;
    }

    private JMenuBar createMenuBar() {
        // File menu.
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportDataItem = new JMenuItem("Export data..");
        JMenuItem importDataItem = new JMenuItem("Import data..");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(exportDataItem);
        fileMenu.add(importDataItem);
        fileMenu.add(exitItem);

        // Window menu.
        JMenu windowMenu = new JMenu("Window");
        JMenu showMenu = new JMenu("Show");
        JCheckBoxMenuItem showFormItem = new JCheckBoxMenuItem("Person form");
        showFormItem.setSelected(true);
        JMenuItem preferencesItem = new JMenuItem("Preferences...");


        showMenu.add(showFormItem);
        windowMenu.add(showMenu);
        windowMenu.add(preferencesItem);

        // Action listeners.
        preferencesItem.addActionListener(
                event -> preferenceDialog.setVisible(true)
        );

        importDataItem.addActionListener(
                event -> {
                    if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                        try {
                            tablePanel.fillTable(personController.loadFromFile(fileChooser.getSelectedFile()));
                            tablePanel.refresh();
                        } catch (IOException | ClassNotFoundException e) {
                            JOptionPane.showMessageDialog(
                                    MainFrame.this,
                                    "Could not load data from file",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
        );

        exportDataItem.addActionListener(
                event -> {
                    if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                        try {
                            personController.savePersonsToFile(fileChooser.getSelectedFile());
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(
                                    MainFrame.this,
                                    "Could not save data from file",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
        );

        showFormItem.addActionListener(
                event -> {
                    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();

                    if (menuItem.isSelected()) {
                        int location = (int) formPanel.getMinimumSize().getWidth();
                        // Use an int to get the pixel width and not the relative width of a double.
                        splitPane.setDividerLocation(location);
                    }
                    formPanel.setVisible(menuItem.isSelected());
                }
        );

        exitItem.addActionListener(
                event -> exitApplication()
        );

        // Mnemonics
        fileMenu.setMnemonic(KeyEvent.VK_F);
        exitItem.setMnemonic(KeyEvent.VK_E);
        windowMenu.setMnemonic(KeyEvent.VK_W);

        // Accelerators.
        importDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        exportDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        preferencesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));

        // Menu Bar.
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);

        return menuBar;
    }
}
