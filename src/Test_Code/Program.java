package Test_Code;
import Generator_CFG.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Program {

    private JFrame mainFrame;
    private JTextArea textArea;
    private JScrollPane textScrollPane;

    private JButton inputButton;
    
    private final int MAIN_FRAME_WIDTH = 1200;
    private final int MAIN_FRAME_HEIGHT = 700;
    private final int SCROLL_WIDTH = 500;
    private final int SCROLL_HEIGHT = 500;
    private final int BUTTON_WIDTH = 150;
    private final int BUTTON_HEIGHT = 50;

    public Program() {
        prepareGUI();
        addActionListener();
    }

    private void prepareGUI() {
        // Get the mainframe of GUI
        mainFrame = new JFrame("Animation Simulator View");

        // Set the text area
        textArea = new JTextArea();
        textArea.setEditable(true);
        // Set the size of text
        textArea.setFont(textArea.getFont().deriveFont(18f));
        // Insert textarea in a scrollpane
        textScrollPane = new JScrollPane(textArea);
        // Set size and where the scrollpane in the mainframe
        textScrollPane.setBounds(350, 50, SCROLL_WIDTH, SCROLL_HEIGHT);
        // Set how to use scrollbar in scrollpane
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set the "Back" button
        inputButton = new JButton("Input Code");
        inputButton.setBounds(500, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Add all component in mainFrame
        mainFrame.add(textScrollPane);
        mainFrame.add(inputButton);
        mainFrame.setSize(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);  
        mainFrame.setLayout(null);  
        mainFrame.setVisible(true);

        // Add listener for event in mainFrame
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    /**
     * This method to add listener for all event get by all buttons
     */
    private void addActionListener() {
        inputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get input code to file
                inputCode();
                // Generator cfg-graph
                CFG_Generator.main(null);
                // Close this frame
                mainFrame.dispose();
                // Get display data
                new View();
            }
        });
    }

    private void inputCode() {
        // Get path file to input code
        String path = "testdata/Generator_CFG/Test2.txt";
        // Get output to write data in file
        File output = new File(path);

        try {
            // Get filewriter to write code into file
			FileWriter fileWriter = new FileWriter(output);
            // Write text in textArea to file
			fileWriter.write(textArea.getText());
            // Close filewriter
			fileWriter.close();
		} catch (IOException e) {
            // Print error trace
			e.printStackTrace();
		}
    }

    public static void main(String args[]) {
        new Program();
    }
}
