package com.example.ui;

import com.example.model.Train;
import com.example.service.TicketService;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    // Core components
    private final TicketService ticketService;
    private JTextPane outputPane;
    private JTextField inputField;
    private String currentCommand;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPasswordField passwordField;
    private JPanel inputPanel;
    private JButton backToMenuButton;
    private JButton submitButton;
    private JFrame floatingOutputFrame;
    private JCheckBoxMenuItem toggleOutputMenuItem;
    
    // Text styling components
    private JComboBox<String> fontStyleCombo;
    private JComboBox<Integer> fontSizeCombo;
    private JButton boldButton, italicButton;
    private Color currentColor = Color.BLACK;
    private boolean isBold = false;
    private boolean isItalic = false;
    
    // Constants
    private static final Color SUCCESS_COLOR = new Color(0, 100, 0);
    private static final Color ERROR_COLOR = new Color(200, 0, 0);
    private static final Color INFO_COLOR = new Color(0, 0, 139);
    private static final Color WARNING_COLOR = new Color(255, 165, 0);
    private static final Color HIGHLIGHT_COLOR = new Color(70, 130, 180);
    
    // Command constants
    private static final String CMD_SEARCH_TRAIN = "SearchByTrain";
    private static final String CMD_SEARCH_DEST = "SearchByDestination";
    private static final String CMD_CHECK_SEAT = "CheckSeatAvailability";
    private static final String CMD_CHECK_PLATFORM = "CheckPlatformAvailability";
    private static final String CMD_BOOK_PLATFORM = "BookPlatformTicket";
    private static final String CMD_QUERY_PLATFORM = "QueryPlatformTickets";
    private static final String CMD_MODIFY_TRAIN = "ModifyTrain";
    private static final String CMD_DELETE_PASS = "DeletePassenger";
    private static final String CMD_BOOK_PASS = "BookPassengerTicket";
    
    // Authentication credentials
    private static final String[] ADMIN_PASSWORDS = {"sou123", "deep12", "prak12"};
    
    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MainFrame() {
        this.ticketService = new TicketService();
        initializeUI();
    }

    private void initializeUI() {
        setupMainWindowProperties();
        initializeComponents();
        setupEventHandlers();
        setupOutputWindow();
        setupMainContentPane();
    }

    private void setupMainWindowProperties() {
        setTitle("Railway Ticket Booking System - " + getCurrentDateTime());
        setSize(1000, 750);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(null);
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(dateFormatter);
    }

    private void initializeComponents() {
        // Background setup
        ImageIcon backgroundIcon = new ImageIcon();
        try {
            backgroundIcon = new ImageIcon(System.getProperty("user.home") + "/Desktop/gif/Train/RailwayTicketSystem/new.gif");
        } catch (Exception e) {
            appendToOutput("⚠️ Could not load background image: " + e.getMessage() + "\n", WARNING_COLOR, true, false);
        }

        JLabel backgroundLabel = new JLabel(backgroundIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 128));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Color.GRAY);
                g2.drawString("Railway Ticket System", 20, getHeight() - 20);
                g2.dispose();
            }
        };
        backgroundLabel.setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout(15, 15));
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 2)
        ));
        mainContainer.setOpaque(false);

        setupMenuSystem();
        setupCardNavigationSystem();
        setupInputSystem();
        
        mainContainer.add(cardPanel, BorderLayout.CENTER);
        mainContainer.add(inputPanel, BorderLayout.SOUTH);
        
        backgroundLabel.add(mainContainer);
        setContentPane(backgroundLabel);
    }

    private void setupMenuSystem() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(HIGHLIGHT_COLOR);
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem refreshItem = new JMenuItem("Refresh", KeyEvent.VK_R);
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        refreshItem.addActionListener(e -> {
            appendToOutput("\n♻️ Refreshing application...\n", INFO_COLOR, true, false);
            appendToOutput("✅ Application refreshed successfully at " + getCurrentDateTime() + "\n", SUCCESS_COLOR, true, false);
        });
        
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu viewMenu = new JMenu("View");
        toggleOutputMenuItem = new JCheckBoxMenuItem("Show Output Window", true);
        toggleOutputMenuItem.setMnemonic(KeyEvent.VK_O);
        toggleOutputMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        toggleOutputMenuItem.addActionListener(e -> toggleOutputWindow());
        
        viewMenu.add(toggleOutputMenuItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutItem.addActionListener(e -> showAboutDialog());
        
        JMenuItem helpItem = new JMenuItem("Help Contents", KeyEvent.VK_H);
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpItem.addActionListener(e -> showHelpDialog());
        
        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }

    private void showHelpDialog() {
        String helpText = "<html><body style='width: 400px; padding: 10px;'>" +
            "<h2>Railway Ticket System Help</h2>" +
            "<p><b>Main Features:</b></p>" +
            "<ul>" +
            "<li>Search trains by number or destination</li>" +
            "<li>Check seat and platform ticket availability</li>" +
            "<li>Book passenger and platform tickets</li>" +
            "<li>Admin functions for system management</li>" +
            "</ul>" +
            "<p><b>Shortcuts:</b></p>" +
            "<ul>" +
            "<li>Ctrl+O: Toggle output window</li>" +
            "<li>F1: Show this help</li>" +
            "<li>Ctrl+R: Refresh data</li>" +
            "</ul>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Help Contents", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupCardNavigationSystem() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
        cardPanel.add(createMainMenuPanel(), "MainMenu");
        cardPanel.add(createPasswordPanel(), "Password");
        cardPanel.add(createAdminPanel(), "Admin");
    }

    private void setupInputSystem() {
        inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 1), 
            "Input",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            HIGHLIGHT_COLOR
        ));
        inputPanel.setOpaque(false);
        inputPanel.setVisible(false);
        
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        submitButton = createStyledButton("Submit", new Color(70, 130, 180));
        submitButton.setMnemonic(KeyEvent.VK_ENTER);
        
        backToMenuButton = createStyledButton("Go Back to Menu", new Color(178, 34, 34));
        backToMenuButton.setMnemonic(KeyEvent.VK_ESCAPE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.add(submitButton);
        buttonPanel.add(backToMenuButton);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
    }

    private void setupEventHandlers() {
        inputField.addActionListener(e -> processInput());
        submitButton.addActionListener(e -> processInput());
        backToMenuButton.addActionListener(e -> returnToMainMenu());
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                updateWindowTitle();
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void updateWindowTitle() {
        setTitle("Railway Ticket Booking System - " + getCurrentDateTime());
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void setupOutputWindow() {
        floatingOutputFrame = new JFrame("Output Console - Railway Ticket System");
        floatingOutputFrame.setSize(850, 650);
        floatingOutputFrame.setMinimumSize(new Dimension(600, 400));
        floatingOutputFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        floatingOutputFrame.setLayout(new BorderLayout(5, 5));
        
        outputPane = new JTextPane() {
            private Image backgroundImage;
            
            {
                try {
                    backgroundImage = new ImageIcon(System.getProperty("user.home") + "/Desktop/a/Train/RailwayTicketSystem/output.jpg").getImage();
                } catch (Exception e) {
                    appendToOutput("⚠️ Could not load background image: " + e.getMessage() + "\n", WARNING_COLOR, true, false);
                    backgroundImage = null;
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        outputPane.setOpaque(false);
        outputPane.setEditable(false);
        outputPane.setBackground(new Color(245, 245, 245, 150));
        outputPane.setMargin(new Insets(15, 20, 15, 20));
        outputPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JToolBar toolBar = createOutputToolbar();
        
        JScrollPane scrollPane = new JScrollPane(outputPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        floatingOutputFrame.add(toolBar, BorderLayout.NORTH);
        floatingOutputFrame.add(scrollPane, BorderLayout.CENTER);
        
        floatingOutputFrame.setLocationByPlatform(true);
        
        floatingOutputFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                toggleOutputMenuItem.setSelected(false);
            }
            
            @Override
            public void windowOpened(WindowEvent e) {
                toggleOutputMenuItem.setSelected(true);
            }
        });
        
        floatingOutputFrame.setVisible(true);
    }

    private JToolBar createOutputToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        toolBar.setBackground(new Color(50, 50, 80));
        toolBar.setForeground(Color.WHITE);
        
        JLabel fontLabel = new JLabel("Font:");
        fontLabel.setForeground(Color.WHITE);
        fontLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontStyleCombo = new JComboBox<>(fontFamilies);
        fontStyleCombo.setSelectedItem("Arial");
        fontStyleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font(value.toString(), Font.PLAIN, 12));
                return this;
            }
        });
        
        JLabel sizeLabel = new JLabel("Size:");
        sizeLabel.setForeground(Color.WHITE);
        sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        
        Integer[] fontSizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36};
        fontSizeCombo = new JComboBox<>(fontSizes);
        fontSizeCombo.setSelectedItem(14);
        
        boldButton = new JButton("B");
        boldButton.setFont(new Font("Arial", Font.BOLD, 14));
        boldButton.setToolTipText("Bold");
        boldButton.setMargin(new Insets(2, 5, 2, 5));
        boldButton.setBackground(new Color(70, 70, 100));
        boldButton.setForeground(Color.WHITE);
        
        italicButton = new JButton("I");
        italicButton.setFont(new Font("Arial", Font.ITALIC, 14));
        italicButton.setToolTipText("Italic");
        italicButton.setMargin(new Insets(2, 5, 2, 5));
        italicButton.setBackground(new Color(70, 70, 100));
        italicButton.setForeground(Color.WHITE);
        
        JButton colorButton = new JButton("Color");
        colorButton.setToolTipText("Text Color");
        colorButton.setBackground(new Color(70, 70, 100));
        colorButton.setForeground(Color.WHITE);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear Output");
        clearButton.setBackground(new Color(200, 50, 50));
        clearButton.setForeground(Color.WHITE);
        
        ActionListener fontUpdater = e -> updateOutputFont();
        fontStyleCombo.addActionListener(fontUpdater);
        fontSizeCombo.addActionListener(fontUpdater);
        
        boldButton.addActionListener(e -> {
            isBold = !isBold;
            updateOutputFont();
        });
        
        italicButton.addActionListener(e -> {
            isItalic = !isItalic;
            updateOutputFont();
        });
        
        colorButton.addActionListener(e -> {
            currentColor = JColorChooser.showDialog(floatingOutputFrame, "Choose Text Color", currentColor);
            updateOutputFont();
        });
        
        clearButton.addActionListener(e -> outputPane.setText(""));
        
        toolBar.add(fontLabel);
        toolBar.add(fontStyleCombo);
        toolBar.add(sizeLabel);
        toolBar.add(fontSizeCombo);
        toolBar.addSeparator();
        toolBar.add(boldButton);
        toolBar.add(italicButton);
        toolBar.add(colorButton);
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(clearButton);
        
        return toolBar;
    }

    private void updateOutputFont() {
        String fontFamily = (String) fontStyleCombo.getSelectedItem();
        int fontSize = (Integer) fontSizeCombo.getSelectedItem();
        int style = Font.PLAIN;
        if (isBold) style |= Font.BOLD;
        if (isItalic) style |= Font.ITALIC;
        
        Font newFont = new Font(fontFamily, style, fontSize);
        outputPane.setFont(newFont);
        
        boldButton.setFont(new Font("Arial", isBold ? Font.BOLD : Font.PLAIN, 14));
        italicButton.setFont(new Font("Arial", isItalic ? Font.ITALIC : Font.PLAIN, 14));
    }

    private void setupMainContentPane() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusPanel.setBackground(new Color(240, 240, 240));
        
        JLabel statusLabel = new JLabel(" Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel versionLabel = new JLabel("Version 2.0 ", SwingConstants.RIGHT);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(versionLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void showAboutDialog() {
        String aboutText = "<html><body style='width: 400px; padding: 10px;'>" +
            "<h2 style='color: #4682B4;'>Railway Ticket Booking System</h2>" +
            "<p><b>Version:</b> 2.0</p>" +
            "<p><b>Developed by:</b> Railway Corporation</p>" +
            "<p><b>Copyright:</b> © 2023 All Rights Reserved</p>" +
            "<p style='margin-top: 20px; font-size: 10px; color: #666;'>" +
            "This software is protected by copyright law and international treaties.</p>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleOutputWindow() {
        if (toggleOutputMenuItem.isSelected()) {
            floatingOutputFrame.setVisible(true);
            floatingOutputFrame.toFront();
        } else {
            floatingOutputFrame.setVisible(false);
        }
    }

    private void focusOutputWindow() {
        floatingOutputFrame.setVisible(true);
        floatingOutputFrame.toFront();
        toggleOutputMenuItem.setSelected(true);
    }

    private void appendToOutput(String text, Color color, boolean bold, boolean italic) {
        try {
            int style = Font.PLAIN;
            if (bold) style |= Font.BOLD;
            if (italic) style |= Font.ITALIC;
            
            Font currentFont = outputPane.getFont();
            Font newFont = new Font(currentFont.getFamily(), style, currentFont.getSize());
            
            outputPane.setEditable(true);
            
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attributes, currentFont.getFamily());
            StyleConstants.setFontSize(attributes, currentFont.getSize());
            StyleConstants.setBold(attributes, bold);
            StyleConstants.setItalic(attributes, italic);
            StyleConstants.setForeground(attributes, color);
            
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    StyledDocument doc = outputPane.getStyledDocument();
                    Style styleButton = outputPane.addStyle("ButtonStyle", null);
                    StyleConstants.setComponent(styleButton, createTextButton(line, color, bold, italic));
                    doc.insertString(doc.getLength(), "\n", attributes);
                    doc.insertString(doc.getLength(), " ", styleButton);
                }
            }
            
            outputPane.setEditable(false);
            outputPane.setCaretPosition(outputPane.getDocument().getLength());
            
            focusOutputWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JButton createTextButton(String text, Color color, boolean bold, boolean italic) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, color.brighter(), 0, getHeight(), color.darker());
                g2.setPaint(getModel().isRollover() ? color.darker() : gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                if (!getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
                } else {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
                }

                super.paintComponent(g);
                g2.dispose();
            }
        };

        int style = Font.PLAIN;
        if (bold) style |= Font.BOLD;
        if (italic) style |= Font.ITALIC;
        
        button.setFont(new Font(outputPane.getFont().getFamily(), style, outputPane.getFont().getSize()));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(2, 5, 2, 5));
        
        button.setEnabled(false);
        
        FontMetrics fm = button.getFontMetrics(button.getFont());
        int width = fm.stringWidth(button.getText()) + 20;
        int height = fm.getHeight() + 10;
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        
        return button;
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 1),
            "Main Menu",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            HIGHLIGHT_COLOR
        ));
        panel.setOpaque(false);

        String[] buttonLabels = {
            "Search by Train Number", 
            "Search by Destination Station",
            "Check Seat Availability", 
            "Check Platform Ticket Availability",
            "Book Platform Ticket", 
            "Hourly Report",
            "Query Platform Tickets", 
            "Admin Functions",
            "Book Passenger Ticket", 
            "Exit"
        };

        Color[] colors = {
            new Color(70, 130, 180),
            new Color(60, 179, 113),
            new Color(255, 165, 0),
            new Color(147, 112, 219),
            new Color(220, 20, 60),
            new Color(46, 139, 87),
            new Color(218, 165, 32),
            new Color(199, 21, 133),
            new Color(0, 139, 139),
            new Color(178, 34, 34)
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createStyledButton(buttonLabels[i], colors[i]);
            final String label = buttonLabels[i];
            button.addActionListener(e -> {
                handleMainMenuChoice(label);
                focusOutputWindow();
            });
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 1),
            "Admin Login",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            HIGHLIGHT_COLOR
        ));
        panel.setOpaque(false);

        JLabel label = new JLabel("Enter Admin Password:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(HIGHLIGHT_COLOR);
        
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton submitButton = createStyledButton("Submit", new Color(70, 130, 180));
        submitButton.setMnemonic(KeyEvent.VK_ENTER);
        
        JButton backButton = createStyledButton("Back", new Color(178, 34, 34));
        backButton.setMnemonic(KeyEvent.VK_ESCAPE);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        
        submitButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            if (authenticate(password)) {
                cardLayout.show(cardPanel, "Admin");
                appendToOutput("✔️ Admin access granted.\n", SUCCESS_COLOR, true, false);
                focusOutputWindow();
            } else {
                appendToOutput("❌ Incorrect password! Access denied.\n", ERROR_COLOR, true, false);
                returnToMainMenu();
            }
            passwordField.setText("");
        });
        
        backButton.addActionListener(e -> returnToMainMenu());
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(submitButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(buttonPanel);
        
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 1),
            "Admin Functions",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            HIGHLIGHT_COLOR
        ));
        panel.setOpaque(false);

        String[] buttonLabels = {
            "Generate Detailed Report", 
            "Modify Train Data",
            "Delete Passenger Data", 
            "Reset Ticket Counters",
            "Add New Train", 
            "View All Trains",
            "View All Passengers", 
            "View All Platform Tickets",
            "Back to Main Menu"
        };

        Color[] colors = {
            new Color(70, 130, 180),
            new Color(60, 179, 113),
            new Color(255, 165, 0),
            new Color(147, 112, 219),
            new Color(220, 20, 60),
            new Color(46, 139, 87),
            new Color(218, 165, 32),
            new Color(199, 21, 133),
            new Color(178, 34, 34)
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createStyledButton(buttonLabels[i], colors[i]);
            final String label = buttonLabels[i];
            button.addActionListener(e -> {
                handleAdminChoice(label);
                focusOutputWindow();
            });
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            private boolean isPressed = false;
            private int rippleRadius = 0;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, bgColor.brighter(), 0, getHeight(), bgColor.darker());
                g2.setPaint(isPressed ? bgColor.darker().darker() : (getModel().isRollover() ? bgColor.darker().darker() : gp));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                if (!isPressed) {
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
                } else {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
                }

                if (rippleRadius > 0) {
                    g2.setColor(new Color(255, 255, 255, 100 - rippleRadius * 2));
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    g2.fill(new Ellipse2D.Double(centerX - rippleRadius, centerY - rippleRadius, rippleRadius * 2, rippleRadius * 2));
                }

                super.paintComponent(g);
                g2.dispose();
            }
        };

        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.getModel().setPressed(true);
                btn.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.getModel().setPressed(false);
                startRippleEffect(btn);
                btn.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }

    private void startRippleEffect(JButton button) {
        Timer rippleTimer = new Timer(20, null);
        final int[] rippleRadius = {0};
        rippleTimer.addActionListener(e -> {
            rippleRadius[0] += 5;
            button.putClientProperty("rippleRadius", rippleRadius[0]);
            button.repaint();
            if (rippleRadius[0] >= Math.max(button.getWidth(), button.getHeight()) / 2) {
                rippleTimer.stop();
                button.putClientProperty("rippleRadius", 0);
                button.repaint();
            }
        });
        rippleTimer.start();
    }

        private void handleMainMenuChoice(String choice) {
        appendToOutput("\n", currentColor, false, false);
        switch (choice) {
            case "Search by Train Number":
                appendToOutput("Enter Train Number: ", currentColor, false, false);
                currentCommand = CMD_SEARCH_TRAIN;
                inputPanel.setVisible(true);
                inputField.requestFocus();
                break;
            case "Search by Destination Station":
                appendToOutput("Enter Destination Station: ", currentColor, false, false);
                currentCommand = CMD_SEARCH_DEST;
                inputPanel.setVisible(true);
                inputField.requestFocus();
                break;
            case "Check Seat Availability":
                appendToOutput("Enter Train Number to check seat availability: ", currentColor, false, false);
                currentCommand = CMD_CHECK_SEAT;
                inputPanel.setVisible(true);
                inputField.requestFocus();
                break;
            case "Check Platform Ticket Availability":
                appendToOutput("Enter Train Number to check platform ticket availability: ", currentColor, false, false);
                currentCommand = CMD_CHECK_PLATFORM;
                inputPanel.setVisible(true);
                inputField.requestFocus();
                break;
            case "Book Platform Ticket":
                appendToOutput("Enter Train Number for Platform Ticket: ", currentColor, false, false);
                currentCommand = CMD_BOOK_PLATFORM;
                inputPanel.setVisible(true);
                inputField.requestFocus();
                break;
            case "Hourly Report":
                try {
                    appendToOutput(ticketService.generateHourlyReport() + "\n", currentColor, false, false);
                } catch (Exception e) {
                    appendToOutput("❌ Error generating report: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
                }
                break;
            case "Query Platform Tickets":
                appendToOutput("Enter Train Number to check platform tickets: ", currentColor, false, false);
                currentCommand = CMD_QUERY_PLATFORM;
                inputPanel.setVisible(true);
                inputField.requestFocus();
                break;
            case "Admin Functions":
                cardLayout.show(cardPanel, "Password");
                break;
            case "Book Passenger Ticket":
                appendToOutput("Enter Train Number to book passenger ticket: ", currentColor, false, false);
                currentCommand = CMD_BOOK_PASS;
                inputPanel.setVisible(true);
                inputField.requestFocus();
                break;
            case "Exit":
                confirmExit();
                break;
        }
    }

    private void handleAdminChoice(String choice) {
        appendToOutput("\n", currentColor, false, false);
        try {
            switch (choice) {
                case "Generate Detailed Report":
                    appendToOutput(ticketService.generateDetailedReport(), currentColor, false, false);
                    break;
                case "Modify Train Data":
                    appendToOutput("Enter Train Number to modify: ", currentColor, false, false);
                    currentCommand = CMD_MODIFY_TRAIN;
                    inputPanel.setVisible(true);
                    inputField.requestFocus();
                    break;
                case "Delete Passenger Data":
                    appendToOutput("Enter Passenger Serial Number to delete: ", currentColor, false, false);
                    currentCommand = CMD_DELETE_PASS;
                    inputPanel.setVisible(true);
                    inputField.requestFocus();
                    break;
                case "Reset Ticket Counters":
                    ticketService.resetCounters();
                    appendToOutput("✅ Ticket counters reset successfully!\n", SUCCESS_COLOR, true, false);
                    break;
                case "Add New Train":
                    showAddTrainDialog();
                    break;
                case "View All Trains":
                    viewAllTrains(true); // Admin view
                    break;
                case "View All Passengers":
                    viewAllPassengers();
                    break;
                case "View All Platform Tickets":
                    viewAllPlatformTickets();
                    break;
                case "Back to Main Menu":
                    outputPane.setText("");
                    appendToOutput("🔒 Admin session cleared for security.\n", INFO_COLOR, true, false);
                    returnToMainMenu();
                    break;
            }
        } catch (Exception e) {
            appendToOutput("❌ Error: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
        }
    }

    private void returnToMainMenu() {
        cardLayout.show(cardPanel, "MainMenu");
        inputPanel.setVisible(false);
        appendToOutput("\n=== Returned to Main Menu ===\n", SUCCESS_COLOR, true, false);
        focusOutputWindow();
    }

    private void processInput() {
        String input = inputField.getText().trim();
        inputField.setText("");
        
        if (input.isEmpty()) {
            appendToOutput("❌ Please enter a valid input!\n", ERROR_COLOR, true, false);
            return;
        }
        
        try {
            switch (currentCommand) {
                case CMD_SEARCH_TRAIN:
                    searchByTrainNumber(input);
                    break;
                case CMD_SEARCH_DEST:
                    searchByDestination(input);
                    break;
                case CMD_CHECK_SEAT:
                    checkSeatAvailability(input);
                    break;
                case CMD_CHECK_PLATFORM:
                    checkPlatformTicketAvailability(input);
                    break;
                case CMD_BOOK_PLATFORM:
                    bookPlatformTicket(input);
                    break;
                case CMD_QUERY_PLATFORM:
                    queryPlatformTickets(input);
                    break;
                case CMD_MODIFY_TRAIN:
                    modifyTrainData(input);
                    break;
                case CMD_DELETE_PASS:
                    deletePassengerData(input);
                    break;
                case CMD_BOOK_PASS:
                    bookPassengerTicket(input);
                    break;
                default:
                    appendToOutput("❌ No command selected. Please choose an option from the menu.\n", ERROR_COLOR, true, false);
            }
        } catch (Exception e) {
            appendToOutput("❌ Error processing input: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
        }
    }

    private void searchByTrainNumber(String trainNumber) throws IOException {
        Train train = ticketService.getTrainByNumber(trainNumber);
        if (train != null) {
            String details = String.format(
                "\n=== TRAIN DETAILS ===\n" +
                "Number:    %s\n" +
                "From:      %s\n" +
                "To:        %s\n" +
                "Departure: %s\n" +
                "Arrival:   %s\n" +
                "Type:      %s\n" +
                "Seats:     %s\n\n",
                train.getNumber(),
                train.getFrom(),
                train.getTo(),
                train.getDeparture(),
                train.getArrival(),
                train.getType(),
                train.getAvailableSeats()
            );
            appendToOutput(details, INFO_COLOR, false, false);
            
            int option = JOptionPane.showConfirmDialog(this, 
                "Do you want to book a ticket for this train?", 
                "Book Ticket", JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                bookPassengerTicket(train.getNumber());
            }
        } else {
            appendToOutput("❌ Train not found!\n", ERROR_COLOR, true, false);
        }
    }

    private void checkSeatAvailability(String trainNumber) throws IOException {
        Train train = ticketService.getTrainByNumber(trainNumber);
        if (train != null) {
            appendToOutput("\nAvailable Seats for Train " + trainNumber + ": " + train.getAvailableSeats() + "\n", 
                INFO_COLOR, false, false);
        } else {
            appendToOutput("❌ Train not found!\n", ERROR_COLOR, true, false);
        }
    }

    private void viewAllTrains(boolean isAdmin) throws IOException {
        List<Train> trains = ticketService.getAllTrains();
        if (trains.isEmpty()) {
            appendToOutput("No trains found in the database.\n", currentColor, false, false);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== ALL TRAINS ===\n\n");
        
        String[] headers = {"Number", "From", "To", "Departure", "Arrival", "Type", "Seats"};
        int[] columnWidths = {8, 10, 10, 10, 10, 20, 6};
        
        for (int i = 0; i < headers.length; i++) {
            sb.append(String.format("%-" + columnWidths[i] + "s ", headers[i]));
        }
        sb.append("\n");
        sb.append("-".repeat(80)).append("\n");
        
        appendToOutput(sb.toString(), INFO_COLOR, false, false);
        
        for (Train train : trains) {
            String trainInfo = String.format("%-8s %-10s %-10s %-10s %-10s %-20s %-6d",
                train.getNumber(),
                train.getFrom(),
                train.getTo(),
                train.getDeparture(),
                train.getArrival(),
                train.getType(),
                train.getAvailableSeats());
            
            JButton trainButton = createStyledButton(trainInfo, INFO_COLOR);
            if (!isAdmin) {
                trainButton.setEnabled(true);
                trainButton.addActionListener(e -> showWeeklyAvailability(train.getNumber(), false));
            }
            StyledDocument doc = outputPane.getStyledDocument();
            Style styleButton = outputPane.addStyle("TrainButton" + train.getNumber(), null);
            StyleConstants.setComponent(styleButton, trainButton);
            try {
                doc.insertString(doc.getLength(), "\n", null);
                doc.insertString(doc.getLength(), " ", styleButton);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        outputPane.setCaretPosition(outputPane.getDocument().getLength());
    }

    private void showWeeklyAvailability(String trainNumber, boolean isAdmin) {
        try {
            Train train = ticketService.getTrainByNumber(trainNumber);
            if (train == null) {
                appendToOutput("❌ Train not found!\n", ERROR_COLOR, true, false);
                return;
            }

            // Simulate weekly availability from Excel (replace with actual logic)
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            boolean[] availability = {true, false, true, true, false, true, false}; // Example data
            
            appendToOutput("\n=== Weekly Availability for Train " + trainNumber + " ===\n", INFO_COLOR, true, false);
            
            for (int i = 0; i < days.length; i++) {
                if (availability[i]) {
                    JButton dayButton = createStyledButton(days[i], SUCCESS_COLOR);
                    if (!isAdmin) {
                        dayButton.setEnabled(true);
                        dayButton.addActionListener(e -> bookPassengerTicket(trainNumber));
                    }
                    StyledDocument doc = outputPane.getStyledDocument();
                    Style styleButton = outputPane.addStyle("DayButton" + days[i] + trainNumber, null);
                    StyleConstants.setComponent(styleButton, dayButton);
                    try {
                        doc.insertString(doc.getLength(), " ", styleButton);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            appendToOutput("\n", INFO_COLOR, false, false);
            outputPane.setCaretPosition(outputPane.getDocument().getLength());
        } catch (Exception e) {
            appendToOutput("❌ Error fetching availability: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
        }
    }

    private void viewAllPassengers() throws IOException {
        List<String> passengers = ticketService.getAllPassengers();
        if (passengers.isEmpty()) {
            appendToOutput("No passenger records found.\n", currentColor, false, false);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== ALL PASSENGERS ===\n\n");
        for (String passenger : passengers) {
            sb.append(passenger).append("\n");
        }
        appendToOutput(sb.toString(), new Color(139, 0, 0), false, false);
    }

    private void viewAllPlatformTickets() throws IOException {
        List<String> tickets = ticketService.getAllPlatformTickets();
        if (tickets.isEmpty()) {
            appendToOutput("No platform tickets sold yet.\n", currentColor, false, false);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== ALL PLATFORM TICKETS ===\n\n");
        for (String ticket : tickets) {
            sb.append(ticket).append("\n");
        }
        appendToOutput(sb.toString(), new Color(139, 0, 139), false, false);
    }
private boolean authenticate(String password) {
        for (String validPass : ADMIN_PASSWORDS) {
            if (validPass.equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void showAddTrainDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        JTextField trainNumberField = new JTextField();
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();
        JTextField departureField = new JTextField();
        JTextField arrivalField = new JTextField();
        JComboBox<String> typeField = new JComboBox<>(new String[]{"Superfast Express", "Mail", "Local"});
        JTextField seatsField = new JTextField();
        
        panel.add(new JLabel("Train Number:"));
        panel.add(trainNumberField);
        panel.add(new JLabel("From Station:"));
        panel.add(fromField);
        panel.add(new JLabel("To Station:"));
        panel.add(toField);
        panel.add(new JLabel("Departure Time (HH:MM):"));
        panel.add(departureField);
        panel.add(new JLabel("Arrival Time (HH:MM):"));
        panel.add(arrivalField);
        panel.add(new JLabel("Train Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Number of Seats:"));
        panel.add(seatsField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Train", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String trainNumber = trainNumberField.getText().trim();
                String from = fromField.getText().trim();
                String to = toField.getText().trim();
                String departureTime = departureField.getText().trim();
                String arrivalTime = arrivalField.getText().trim();
                String trainType = (String) typeField.getSelectedItem();
                int seats = Integer.parseInt(seatsField.getText().trim());
                
                if (trainNumber.isEmpty() || from.isEmpty() || to.isEmpty()) {
                    appendToOutput("❌ Required fields cannot be empty!\n", ERROR_COLOR, true, false);
                    return;
                }
                
                if (seats <= 0) {
                    appendToOutput("❌ Number of seats must be positive!\n", ERROR_COLOR, true, false);
                    return;
                }
                
                ticketService.addNewTrain(trainNumber, from, to, departureTime, arrivalTime, trainType, seats);
                appendToOutput("✅ New train added successfully!\n", SUCCESS_COLOR, true, false);
            } catch (NumberFormatException e) {
                appendToOutput("❌ Invalid number of seats entered! Please enter a valid positive integer.\n", ERROR_COLOR, true, false);
            } catch (Exception e) {
                appendToOutput("❌ Error adding new train: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
            }
        }
    }

    private void searchByDestination(String destination) throws IOException {
        List<Train> trains = ticketService.searchByDestination(destination);
        if (trains.isEmpty()) {
            appendToOutput("❌ No trains found for destination: " + destination + "\n", ERROR_COLOR, true, false);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== TRAINS TO ").append(destination.toUpperCase()).append(" ===\n\n");
        
        String[] headers = {"Number", "From", "To", "Departure", "Arrival", "Type", "Seats"};
        int[] columnWidths = {8, 10, 10, 10, 10, 20, 6};
        
        for (int i = 0; i < headers.length; i++) {
            sb.append(String.format("%-" + columnWidths[i] + "s ", headers[i]));
        }
        sb.append("\n");
        sb.append("-".repeat(80)).append("\n");
        
        appendToOutput(sb.toString(), INFO_COLOR, false, false);
        
        for (Train train : trains) {
            String trainInfo = String.format("%-8s %-10s %-10s %-10s %-10s %-20s %-6d",
                train.getNumber(),
                train.getFrom(),
                train.getTo(),
                train.getDeparture(),
                train.getArrival(),
                train.getType(),
                train.getAvailableSeats());
            
            JButton trainButton = createStyledButton(trainInfo, INFO_COLOR);
            trainButton.setEnabled(true);
            trainButton.addActionListener(e -> showWeeklyAvailability(train.getNumber(), false));
            
            StyledDocument doc = outputPane.getStyledDocument();
            Style styleButton = outputPane.addStyle("TrainButton" + train.getNumber(), null);
            StyleConstants.setComponent(styleButton, trainButton);
            try {
                doc.insertString(doc.getLength(), "\n", null);
                doc.insertString(doc.getLength(), " ", styleButton);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        outputPane.setCaretPosition(outputPane.getDocument().getLength());
    }

    private void checkPlatformTicketAvailability(String trainNumber) throws IOException {
        int count = ticketService.getPlatformTicketCount(trainNumber);
        appendToOutput("\nPlatform tickets available for train " + trainNumber + ": " + count + "\n", 
            INFO_COLOR, false, false);
    }

    private void bookPlatformTicket(String trainNumber) {
        try {
            String quantityStr = JOptionPane.showInputDialog(this, "Enter number of platform tickets:");
            if (quantityStr == null || quantityStr.isEmpty()) {
                appendToOutput("❌ Booking cancelled.\n", ERROR_COLOR, true, false);
                return;
            }
            
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    appendToOutput("❌ Invalid quantity! Must be at least 1.\n", ERROR_COLOR, true, false);
                    return;
                }
            } catch (NumberFormatException e) {
                appendToOutput("❌ Please enter a valid number!\n", ERROR_COLOR, true, false);
                return;
            }

            int pricePerTicket = ticketService.calculatePlatformTicketPrice();
            int totalAmount = pricePerTicket * quantity;
            appendToOutput("\nTotal Amount: ₹" + totalAmount + "\n", SUCCESS_COLOR, true, false);

            String amountPaidStr = JOptionPane.showInputDialog(this, 
                "Enter amount to pay (Total: ₹" + totalAmount + "):");
            if (amountPaidStr == null || amountPaidStr.isEmpty()) {
                appendToOutput("❌ Payment cancelled.\n", ERROR_COLOR, true, false);
                return;
            }
            
            int amountPaid;
            try {
                amountPaid = Integer.parseInt(amountPaidStr);
                if (amountPaid <= 0) {
                    appendToOutput("❌ Invalid amount entered!\n", ERROR_COLOR, true, false);
                    return;
                }
            } catch (NumberFormatException e) {
                appendToOutput("❌ Invalid amount entered!\n", ERROR_COLOR, true, false);
                return;
            }

            if (amountPaid < totalAmount) {
                appendToOutput("❌ Insufficient amount! Payment failed.\n", ERROR_COLOR, true, false);
                return;
            }

            for (int i = 0; i < quantity; i++) {
                ticketService.bookPlatformTicket(trainNumber, pricePerTicket);
            }

            if (amountPaid > totalAmount) {
                appendToOutput("\n✅ Payment successful! Please take your change: ₹" + 
                    (amountPaid - totalAmount) + "\n", SUCCESS_COLOR, true, false);
            } else {
                appendToOutput("\n✅ Payment successful!\n", SUCCESS_COLOR, true, false);
            }

            appendToOutput("✅ " + quantity + " platform ticket(s) booked successfully!\n", 
                SUCCESS_COLOR, true, false);
            appendToOutput("Price per ticket: ₹" + pricePerTicket + "\n", 
                SUCCESS_COLOR, false, false);
        } catch (Exception e) {
            appendToOutput("❌ Error booking platform tickets: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
        }
    }

    private void queryPlatformTickets(String trainNumber) throws IOException {
        List<String> tickets = ticketService.getPlatformTickets(trainNumber);
        if (tickets.isEmpty()) {
            appendToOutput("No platform tickets found for this train.\n", currentColor, false, false);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== PLATFORM TICKETS FOR TRAIN ").append(trainNumber).append(" ===\n\n");
        for (String ticket : tickets) {
            sb.append(ticket).append("\n");
        }
        appendToOutput(sb.toString(), new Color(139, 0, 139), false, false);
    }

    private void modifyTrainData(String trainNumber) {
        try {
            Train train = ticketService.getTrainByNumber(trainNumber);
            if (train == null) {
                appendToOutput("❌ Train not found!\n", ERROR_COLOR, true, false);
                return;
            }

            String newSeats = JOptionPane.showInputDialog(this, "Enter new number of seats:");
            if (newSeats != null && !newSeats.isEmpty()) {
                try {
                    int seats = Integer.parseInt(newSeats);
                    if (seats < 0) {
                        appendToOutput("❌ Number of seats cannot be negative!\n", ERROR_COLOR, true, false);
                        return;
                    }
                    ticketService.updateTrainSeats(trainNumber, seats);
                    appendToOutput("✅ Train data modified successfully!\n", SUCCESS_COLOR, true, false);
                } catch (NumberFormatException e) {
                    appendToOutput("❌ Invalid number entered! Please enter a valid positive integer.\n", ERROR_COLOR, true, false);
                }
            }
        } catch (Exception e) {
            appendToOutput("❌ Error modifying train data: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
        }
    }

    private void deletePassengerData(String serialNumber) {
        try {
            if (!serialNumber.matches("\\d+")) {
                appendToOutput("❌ Invalid serial number! Please enter a number.\n", ERROR_COLOR, true, false);
                return;
            }
            ticketService.deletePassenger(Integer.parseInt(serialNumber));
            appendToOutput("✅ Passenger data deleted successfully!\n", SUCCESS_COLOR, true, false);
        } catch (Exception e) {
            appendToOutput("❌ Error deleting passenger data: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
        }
    }
    private void bookPassengerTicket(String trainNumber) {
    try {
        Train train = ticketService.getTrainByNumber(trainNumber);
        if (train == null) {
            appendToOutput("❌ Train not found!\n", ERROR_COLOR, true, false);
            return;
        }

        // Get base price based on train type
        int basePrice = ticketService.calculatePassengerTicketPrice(trainNumber);
        if (basePrice == 0) {
            appendToOutput("❌ Invalid train number or type!\n", ERROR_COLOR, true, false);
            return;
        }

        String classType = "General";
        int price = basePrice;
        
        // Only show class options for Superfast Express trains
        if (train.getType().equalsIgnoreCase("Superfast Express")) {
            Object[] options = {"General", "Sleeper", "AC 2nd Tier", "AC 1st Tier", "First Class"};
            int classChoice = JOptionPane.showOptionDialog(this,
                "Select class type:",
                "Class Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (classChoice == JOptionPane.CLOSED_OPTION) {
                appendToOutput("❌ Booking cancelled.\n", ERROR_COLOR, true, false);
                return;
            }
            
            // Fix: Explicitly cast the selected option to String
            classType = (String) options[classChoice];
            
            // Apply multipliers based on class selection
            switch (classChoice) {
                case 0: // General
                    price = basePrice;
                    break;
                case 1: // Sleeper
                    price = (int)(basePrice * 1.5);
                    break;
                case 2: // AC 2nd Tier
                    price = basePrice * 2;
                    break;
                case 3: // AC 1st Tier
                    price = basePrice * 3;
                    break;
                case 4: // First Class
                    price = basePrice * 4;
                    break;
            }
        }

        appendToOutput("\nTicket Price (" + classType + "): ₹" + price + "\n", SUCCESS_COLOR, false, false);
        
        String passengerCountStr = JOptionPane.showInputDialog(this, "Enter number of passengers:");
        if (passengerCountStr == null || passengerCountStr.isEmpty()) {
            appendToOutput("❌ Booking cancelled.\n", ERROR_COLOR, true, false);
            return;
        }
        
        int passengerCount;
        try {
            passengerCount = Integer.parseInt(passengerCountStr);
            if (passengerCount <= 0) {
                appendToOutput("❌ Invalid number of passengers! Must be at least 1.\n", ERROR_COLOR, true, false);
                return;
            }
        } catch (NumberFormatException e) {
            appendToOutput("❌ Please enter a valid number!\n", ERROR_COLOR, true, false);
            return;
        }

        if (train.getAvailableSeats() < passengerCount) {
            appendToOutput("❌ Not enough seats available!\n", ERROR_COLOR, true, false);
            return;
        }

        JPanel passengerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        List<JTextField> nameFields = new ArrayList<>();
        List<JTextField> ageFields = new ArrayList<>();
        List<JComboBox<String>> genderFields = new ArrayList<>();

        for (int i = 0; i < passengerCount; i++) {
            passengerPanel.add(new JLabel("Passenger " + (i + 1) + " Name:"));
            JTextField nameField = new JTextField();
            nameFields.add(nameField);
            passengerPanel.add(nameField);

            passengerPanel.add(new JLabel("Age:"));
            JTextField ageField = new JTextField();
            ageFields.add(ageField);
            passengerPanel.add(ageField);

            passengerPanel.add(new JLabel("Gender:"));
            JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            genderFields.add(genderBox);
            passengerPanel.add(genderBox);
        }

        int result = JOptionPane.showConfirmDialog(this, passengerPanel, 
            "Enter Passenger Details", JOptionPane.OK_CANCEL_OPTION);
        
        if (result != JOptionPane.OK_OPTION) {
            appendToOutput("❌ Booking cancelled.\n", ERROR_COLOR, true, false);
            return;
        }

        int totalAmount = price * passengerCount;
        appendToOutput("\nTotal Amount: ₹" + totalAmount + "\n", SUCCESS_COLOR, true, false);

        String amountPaidStr = JOptionPane.showInputDialog(this, 
            "Enter amount to pay (Total: ₹" + totalAmount + "):");
        if (amountPaidStr == null || amountPaidStr.isEmpty()) {
            appendToOutput("❌ Payment cancelled.\n", ERROR_COLOR, true, false);
            return;
        }
        
        int amountPaid;
        try {
            amountPaid = Integer.parseInt(amountPaidStr);
            if (amountPaid <= 0) {
                appendToOutput("❌ Invalid amount entered!\n", ERROR_COLOR, true, false);
                return;
            }
        } catch (NumberFormatException e) {
            appendToOutput("❌ Invalid amount entered!\n", ERROR_COLOR, true, false);
            return;
        }

        if (amountPaid < totalAmount) {
            appendToOutput("❌ Insufficient amount! Payment failed.\n", ERROR_COLOR, true, false);
            return;
        }

        for (int i = 0; i < passengerCount; i++) {
            String name = nameFields.get(i).getText().trim();
            if (name.isEmpty()) {
                appendToOutput("❌ Name cannot be empty for passenger " + (i + 1) + "!\n", ERROR_COLOR, true, false);
                return;
            }
            
            int age;
            try {
                age = Integer.parseInt(ageFields.get(i).getText());
                if (age <= 0 || age > 120) {
                    appendToOutput("⚠️ Invalid age for passenger " + (i + 1) + "! Using default age 25.\n", WARNING_COLOR, true, false);
                    age = 25;
                }
            } catch (NumberFormatException e) {
                appendToOutput("⚠️ Invalid age for passenger " + (i + 1) + "! Using default age 25.\n", WARNING_COLOR, true, false);
                age = 25;
            }
            
            String gender = (String) genderFields.get(i).getSelectedItem();
            ticketService.bookPassengerTicket(trainNumber, name, age, gender.substring(0, 1), classType, price);
        }

        if (amountPaid > totalAmount) {
            appendToOutput("\n✅ Payment successful! Please take your change: ₹" + 
                (amountPaid - totalAmount) + "\n", SUCCESS_COLOR, true, false);
        } else {
            appendToOutput("\n✅ Payment successful!\n", SUCCESS_COLOR, true, false);
        }

        appendToOutput("✅ Tickets booked successfully for " + passengerCount + " passenger(s)!\n", 
            SUCCESS_COLOR, true, false);
        
        // Update the train's available seats
        ticketService.updateTrainSeats(trainNumber, train.getAvailableSeats() - passengerCount);
    } catch (Exception e) {
        appendToOutput("❌ Error booking tickets: " + e.getMessage() + "\n", ERROR_COLOR, true, false);
    }
}
   
}
