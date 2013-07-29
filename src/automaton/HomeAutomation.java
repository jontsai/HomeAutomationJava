package automaton;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


/**
 * A simple HelloWorld demo showing a simple speech application built using Sphinx-4. This application uses the Sphinx-4
 * endpointer, which automatically segments incoming audio into utterances and silences.
 */
public class HomeAutomation implements ActionListener, ItemListener, FocusListener, AppConstants {

    private Automaton automaton;
    private JLabel automatonStatus;
    private JButton buttonSpeakTime;
    private JButton buttonRunCommand;
    private JCheckBox checkboxHybridMode;
    private JTextField textfieldEnterCommand;
    
    public HomeAutomation() {
        automaton = new Automaton(this);
    }
    
    public void createAndShowGUI() {
        // create the window
        JFrame jframe = new JFrame(APP_TITLE);
        
        // set the behavior for when the window is closed
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // set the jframe size and location, and make it visible
        jframe.setPreferredSize(DIM_APP);

        Container contentPane = jframe.getContentPane();
        
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        

        borderPanel.add(createCenterPanel(), BorderLayout.CENTER);
        borderPanel.add(createNorthPanel(), BorderLayout.NORTH);
        borderPanel.add(createSouthPanel(), BorderLayout.SOUTH);
        
        contentPane.add(borderPanel, BorderLayout.CENTER);
        
        buttonRunCommand = new JButton(_RUN_COMMAND);
        contentPane.add(buttonRunCommand, BorderLayout.SOUTH);
        buttonRunCommand.setMnemonic(MN_RUN_COMMAND);
        buttonRunCommand.addActionListener(this);

        toggleHybridMode(false);
        
        jframe.pack(); // arrange components inside the window
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true); // not visible by default
        
        automaton.startAutomaton();
    }

    public JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        automatonStatus = new JLabel(_AWAITING_COMMAND, JLabel.CENTER);
        Font oldFont = automatonStatus.getFont();
        automatonStatus.setFont(new Font(oldFont.getFontName(), oldFont.getStyle(), FONT_SIZE));
        centerPanel.add(automatonStatus, BorderLayout.CENTER);
        return centerPanel;
    }
    
    public JPanel createNorthPanel() {
        JPanel northPanel = new JPanel(new FlowLayout());
        northPanel.add(new JLabel(_NAME + _AUTOMATON_NAME + "."));
        northPanel.add(new JLabel(_CREATED_BY + _CREATOR + "."));
        northPanel.add(new JLabel(_REQUEST_COMMAND));
        return northPanel;
    }
    
    public JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout());
        checkboxHybridMode = new JCheckBox(_HYBRID_MODE, true);
        checkboxHybridMode.setMnemonic(MN_HYBRID_MODE);
        checkboxHybridMode.addItemListener(this);
        
        textfieldEnterCommand = new JTextField(_REQUEST_COMMAND_TEXT);
        textfieldEnterCommand.setMaximumSize(DIM_TEXT_FIELD_COMMAND);
        textfieldEnterCommand.addActionListener(this);
        textfieldEnterCommand.addFocusListener(this);

        buttonSpeakTime = new JButton(_SPEAK_TIME);
        buttonSpeakTime.addActionListener(this);
        
        southPanel.add(checkboxHybridMode);
        southPanel.add(buttonSpeakTime);
        southPanel.add(textfieldEnterCommand);
        
        return southPanel;
    }
    
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == buttonRunCommand || source == textfieldEnterCommand) {
            String command = textfieldEnterCommand.getText();
            updateStatus("Command received: " + command);
            automaton.getRoutines().echo(command);
        } else if (source == buttonSpeakTime) {
            updateStatus("Request received: Speak time");
            automaton.getRoutines().speakTime();
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        Object source = ie.getSource();
        if (source == checkboxHybridMode) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                toggleHybridMode(true);
            } else {
                toggleHybridMode(false);
            }
        }
    }

    public void focusGained(FocusEvent fe) {
        Object source = fe.getSource();
        if (source == textfieldEnterCommand) {
            textfieldEnterCommand.setText("");
        }
    }

    public void focusLost(FocusEvent fe) {
        Object source = fe.getSource();
        if (source == textfieldEnterCommand) {
            String text = textfieldEnterCommand.getText();
            if (text == "") {
                textfieldEnterCommand.setText(_REQUEST_COMMAND_TEXT);    
            }
        }
    }
    
    public void updateStatus(String status) {
        automatonStatus.setText(status);
        System.out.println(status);
    }
    
    public void toggleHybridMode(boolean hybridOn) {
        if (hybridOn) {
            checkboxHybridMode.setSelected(true);
            buttonSpeakTime.setEnabled(true);
            buttonRunCommand.setEnabled(true);
            textfieldEnterCommand.setEnabled(true);
        } else {
            checkboxHybridMode.setSelected(false);
            buttonSpeakTime.setEnabled(false);
            buttonRunCommand.setEnabled(false);
            textfieldEnterCommand.setEnabled(false);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HomeAutomation().createAndShowGUI();
            }
        });
    }
}
