package Test_Code;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class View {
    
    private JFrame mainFrame;
    private JTextArea textArea;
    private JScrollPane textScrollPane;
    private JLabel imageLabel;
    private JScrollPane imageScrollPane;

    private JButton backButton;
    private JButton nextButton;
    private JButton restartOrInputPathButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton fitImageButton;

    private final int MAIN_FRAME_WIDTH = 1200;
    private final int MAIN_FRAME_HEIGHT = 700;
    private final int SCROLL_WIDTH = 500;
    private final int SCROLL_HEIGHT = 500;
    private final int BUTTON_WIDTH = 150;
    private final int BUTTON_HEIGHT = 50;
    private final int MAX_IMAGE_SIZE = 2000;
    private final int MIN_IMAGE_SIZE = 100;
    private final double ZOOM_IN_PARAMETER = 1.25;
    private final double ZOOM_OUT_PARAMETER = 0.8;

    private String pathname = "src/Test_Code/Test_Image.png";
    private BufferedImage image;
    private int width;
    private int height;
    private boolean inputPath = false;
    private List<String> pathCode = null;

    /**
     * This method to create new object of class (contructor)
     */
    public View(){
        preparedGUI();
        Simulator.setUp();
        getImage();
        addActionListener();
    }

    /**
     * This method to set up component in the mainframe
     */
    private void preparedGUI() {
        // Get the mainframe of GUI
        mainFrame = new JFrame("Animation Simulator View");

        // Set the text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        // Set the size of text
        textArea.setFont(textArea.getFont().deriveFont(18f));
        // Insert textarea in a scrollpane
        textScrollPane = new JScrollPane(textArea);
        // Set size and where the scrollpane in the mainframe
        textScrollPane.setBounds(50, 50, SCROLL_WIDTH, SCROLL_HEIGHT);
        // Set how to use scrollbar in scrollpane
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // A label to display image of graph
        imageLabel = new JLabel();
        // Insert label in a scrollpane
        imageScrollPane = new JScrollPane(imageLabel);
        // Set size and where the image scrollpane in the mainframe
        imageScrollPane.setBounds(650, 50, SCROLL_WIDTH, SCROLL_HEIGHT);
        // Set how to use scrollbar in scrollpane
        imageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set the "Back" button
        backButton = new JButton("Back");
        backButton.setBounds(50, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Set the "Next" button
        nextButton = new JButton("Next");
        nextButton.setBounds(400, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Set the "Restart Or Input_Path" button
        restartOrInputPathButton = new JButton("Input Path");
        restartOrInputPathButton.setBounds(225, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Set the "Zoom In" button
        zoomInButton = new JButton("Zoom In");
        zoomInButton.setBounds(650, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Set the "Zoom Out" button
        zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.setBounds(1000, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Set the "Fit Image" button
        fitImageButton = new JButton("Fit Image");
        fitImageButton.setBounds(825, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Add all component in mainFrame
        mainFrame.add(textScrollPane);
        mainFrame.add(imageScrollPane);
        mainFrame.add(backButton);
        mainFrame.add(nextButton);
        mainFrame.add(restartOrInputPathButton);
        mainFrame.add(zoomInButton);
        mainFrame.add(zoomOutButton);
        mainFrame.add(fitImageButton);
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
     * This method get image of graph to display in screen
     */
    private void getImage() {
        try {
            // Read the image from path
            File file = new File(pathname);
            image = ImageIO.read(file);
            ImageIcon icon = new ImageIcon(image);
            // Get the size of this image
            width = icon.getIconWidth();
            height = icon.getIconHeight();
            // Set image in screen
            imageLabel.setIcon(icon);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method use to add a value in an array of object
     * 
     * @param obj : the old array
     * @param newObj : object need to add into the array
     * @return : the new array has object newObj
     */
    private Object[] appendValue(Object[] obj, Object newObj) {
        // Change array of object to ArrayList
        ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
        // Add new object into the temp arraylist
        temp.add(newObj);
        // Return the result after append an object into the list
        return temp.toArray();
    }
    
    /**
     * This method to add listener for all event get by all buttons
     */
    private void addActionListener() {
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Check if user want to back
                if(JOptionPane.showConfirmDialog(null, "Do you want to go back?\nBack steps available: " + Simulator.getBackStep(), "Message", JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
                    // Check if can go back
                    if (Simulator.isAbleToBackStep()) {
                        // Back a step 
                        Simulator.backImage();
                        // Update image in view
                        getImage();
                        // Remove a step in textArea
                        textArea.setText(textArea.getText().substring(0, textArea.getText().lastIndexOf('\n', textArea.getText().length() - 2) + 1));
                        // Inform back a step successfully
                        JOptionPane.showMessageDialog(null, "Back step successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } 
                    else {
                        // Inform back step failed
                        JOptionPane.showMessageDialog(null, "Can't go back", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // If don't use input path
                if (!inputPath) {
                    int currentNode = Simulator.getCurrentNode();
                    List<Integer> neighbor = Simulator.getNeighbor(currentNode);
                    if ((neighbor == null) || (neighbor.isEmpty())) {
                        JOptionPane.showMessageDialog(null, "This node has no next node", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    else if (neighbor.size() == 1) {
                        String currentLine = Simulator.getNode(currentNode).GetSrcLine();
                        int nextNode = neighbor.get(0);
                        String nextLine = Simulator.getNode(nextNode).GetSrcLine();
                        String edgeLabel = Simulator.getEdge(currentNode, nextNode).GetLabel();
                        String message = "Current node: " + currentLine;
                        message += "\nNext node: " + nextLine;
                        JOptionPane.showMessageDialog(null, "This node has one next node\n" + message, "Title", JOptionPane.INFORMATION_MESSAGE);
                        textArea.append("Step " + Simulator.getCurrentState() + ": " + currentLine + " -> \"" + edgeLabel + "\" -> " + nextLine + ".\n");
                        currentNode = nextNode;
                        Simulator.updateImage(currentNode);
                    }
                    else {
                        Object[] answers = new Object[] {};
                        for (int i : neighbor) {
                            answers = appendValue(answers, Simulator.getEdge(currentNode, i).GetLabel());
                        }
                        String currentLine = Simulator.getNode(currentNode).GetSrcLine();
                        String message = "Current node: " + currentLine;
                        int response = JOptionPane.showOptionDialog(null, message, "Title", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                                                                null, answers, answers[0]);
                        if (response >= 0) {
                            int nextNode = neighbor.get(response);
                            String nextLine = Simulator.getNode(nextNode).GetSrcLine();
                            String edgeLabel = Simulator.getEdge(currentNode, nextNode).GetLabel();
                            textArea.append("Step " + Simulator.getCurrentState() + ": " + currentLine + " -> \"" + edgeLabel + "\" -> " + nextLine + ".\n");
                            currentNode = nextNode;
                            Simulator.updateImage(currentNode);
                        }
                    }
                    getImage();
                    restartOrInputPathButton.setText("Restart");
                }
                // If use input path
                else {
                    // Get current node
                    int currentNode = Simulator.getCurrentNode();
                    // Get list of neighbors of current nod
                    List<Integer> neighbor = Simulator.getNeighbor(currentNode);
                    // Get step
                    int step = Simulator.getCurrentState();
                    // Check if list of path is end
                    if (pathCode.size() >= step) {
                        // Get line of step
                        String line = pathCode.get(step - 1);
                        // Remove first and last character
                        line = line.substring(1, line.length() - 1);
                        // String to token
                        String token = "\" \"";
                        // Tokenize in line
                        String[] tokens = line.split(token);
                        // First tokens is srcLine of currentNode
                        String currentSrcLine = tokens[0];
                        // Second tokens is label of this edge
                        String edgeLabel = tokens[1];
                        // Last tokens is srcLine of nextNode
                        String nextSrcLine = tokens[2];
                        // If current node is not identical to start node in step
                        if (!Simulator.getNode(currentNode).GetSrcLine().equals(currentSrcLine)) {
                            // Annouce wrong current node
                            JOptionPane.showMessageDialog(null, "Current node wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                            // Decrese current step
                            Simulator.decreaseCurrentStep();
                        }
                        else {
                            // Variable to check label end endNode
                            boolean found = false;
                            int nextNode = -1;
                            // All the neighbor
                            for (int next: neighbor) {
                                // Check if endNode of path is indentical to the neighbor node
                                if (Simulator.getNode(next).GetSrcLine().equals(nextSrcLine)) {
                                    // Check if label of edge is true
                                    if (Simulator.getEdge(currentNode, next).GetLabel().equals(edgeLabel)) {
                                        found = true;
                                        nextNode = next;
                                    }
                                }
                            }
                            // When found the next node
                            if (found) {
                                // Print path to textArea
                                textArea.append("Step " + step + ": " + currentSrcLine + " -> \"" + edgeLabel + "\" -> " + nextSrcLine + ".\n");
                                // Get current node
                                currentNode = nextNode;
                                // Update the image
                                Simulator.updateImage(currentNode);
                                // Represent the image in view
                                getImage();
                            }
                            // When not found
                            else {
                                // Annouce wrong path
                                JOptionPane.showMessageDialog(null, "Next path not found.", "Error", JOptionPane.ERROR_MESSAGE);
                                // Decrease current step
                                Simulator.decreaseCurrentStep();
                            }
                        }
                    }
                    // When input path end
                    else {
                        // End of input path
                        JOptionPane.showMessageDialog(null, "End of input path.", "Error", JOptionPane.ERROR_MESSAGE);
                        // Decrese current step
                        Simulator.decreaseCurrentStep();
                    }
                }
            }
        });
        
        restartOrInputPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (restartOrInputPathButton.getText().equals("Restart")) { 
                    Simulator.setUp();
                    textArea.setText("");
                    getImage();
                    inputPath = false;
                    restartOrInputPathButton.setText("Input Path");
                }
                else if (restartOrInputPathButton.getText().equals("Input Path")) {
                    restartOrInputPathButton.setText("OK");
                    textArea.setEditable(true);
                    String message = "Copy input path into text area in the left.\n";
                    message += "After that, please click button \"OK\"\n";
                    message += "One step in one line.";
                    JOptionPane.showMessageDialog(null, message, "Note", JOptionPane.INFORMATION_MESSAGE);
                    inputPath = true;
                }
                else if (restartOrInputPathButton.getText().equals("OK")) {
                    restartOrInputPathButton.setText("Restart");
                    getInputPath();
                    textArea.setEditable(false);
                    textArea.setText("");
                    String message = "Input path ok.";
                    JOptionPane.showMessageDialog(null, message, "Note", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (width < MAX_IMAGE_SIZE && height < MAX_IMAGE_SIZE) {
                    width = (int) (width * ZOOM_IN_PARAMETER);
                    height = (int) (height * ZOOM_IN_PARAMETER);
                    ImageIcon icon = new ImageIcon(ZoomImage(width, height, image));
                    imageLabel.setIcon(icon);
                }       
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (width > MIN_IMAGE_SIZE && height > MIN_IMAGE_SIZE) {
                    width = (int) (width * ZOOM_OUT_PARAMETER);
                    height = (int) (height * ZOOM_OUT_PARAMETER);
                    ImageIcon icon = new ImageIcon(ZoomImage(width, height, image));
                    imageLabel.setIcon(icon);
                }  
            }
        });

        fitImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                float a = (float) SCROLL_WIDTH / width;
                float b = (float) SCROLL_HEIGHT / height;
                ImageIcon icon = null;
                if (a > b) {
                    width = SCROLL_WIDTH;
                    height = (int)(a * height);
                } else {
                    width = (int)(b * width);
                    height = SCROLL_HEIGHT;
                }
                icon = new ImageIcon(ZoomImage(width, height, image));
                imageLabel.setIcon(icon);
            }
        });
    }

    /**
     * This method to get input path to show in image
     */
    private void getInputPath() {
        // Path to save in file
        String path = "testdata/Test/Paths.txt";

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

        //
        pathCode = new ArrayList<String>();
        //
        FileInputStream fstream = null;
		
		try{
			fstream = new FileInputStream(path);
		}
		catch (IOException e){
			System.err.println("Unable opening file "+path+".\n"+e.getMessage());
			System.exit(1);
		}
		
		Scanner scanner = new Scanner(fstream);
		while (scanner.hasNextLine()){
			pathCode.add(scanner.nextLine());
		}
		scanner.close();
		try{
			fstream.close();
		}
		catch (IOException e){
			System.err.println("Error closing file "+path+".\n"+e.getMessage());
		}
    }

    /**
     * This method to get image after zoom in or zoom out
     * 
     * @param w : the new width of image
     * @param h : the new height of image
     * @param img : the image
     * @return : the image with new width and new height
     */
    private Image ZoomImage(int w, int h, Image img) {
        BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2d = buffer.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.drawImage(img, 0, 0, w, h, null);
        graphics2d.dispose();
        
        return buffer;
    }

    public static void main(String args[]){
        new View();
    }
}
