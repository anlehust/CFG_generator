package Simulator;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class Animation_View {
    
    private JFrame mainFrame;
    private JTextArea textArea;
    private JScrollPane textScrollPane;
    private JLabel imageLabel;
    private JScrollPane imageScrollPane;

    private JButton backButton;
    private JButton nextButton;
    private JButton restartButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton fitImageButton;

    private final int MAIN_FRAME_WIDTH = 1200;
    private final int MAIN_FRAME_HEIGHT = 700;
    private final int SCROLL_WIDTH = 500;
    private final int SCROLL_HEIGHT = 500;
    private final int BUTTON_WIDTH = 150;
    private final int BUTTON_HEIGHT = 50;

    private String pathname = "testdata/Simulator/Animation_Example.png";
    private BufferedImage image;
    private int width;
    private int height;

    /**
     * This method to create new object of class (contructor)
     */
    private Animation_View(){
        preparedGUI();
        Animation_Simulator.setUp();
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

        // Set the "Restart" button
        restartButton = new JButton("Restart");
        restartButton.setBounds(225, 600, BUTTON_WIDTH, BUTTON_HEIGHT);

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
        mainFrame.add(restartButton);
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
                if(JOptionPane.showConfirmDialog(null, "Do you want to go back?\nBack steps available: " + Animation_Simulator.getBackStep(), "Message", JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
                    if (Animation_Simulator.isAbleToBackStep()) {
                        Animation_Simulator.backImage();
                        getImage();
                        textArea.setText(textArea.getText().substring(0, textArea.getText().lastIndexOf('\n', textArea.getText().length() - 2) + 1));
                        JOptionPane.showMessageDialog(null, "Back step successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } 
                    else {
                        JOptionPane.showMessageDialog(null, "Can't go back", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int currentNode = Animation_Simulator.getCurrentNode();
                List<Integer> neighbor = Animation_Simulator.getNeighbor(currentNode);
                if ((neighbor == null) || (neighbor.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "This node has no next node", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else {
                    Object[] answers = new Object[] {};
                    for (int i : neighbor) {
                        answers = appendValue(answers, i);
                    }
                    String message = "Current node: " + currentNode;
                    int response = JOptionPane.showOptionDialog(null, message, "Title", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                                                            null, answers, answers[0]);
                    if (response >= 0) {
                        textArea.append("Step " + Animation_Simulator.getCurrentState() + ": " + currentNode + " -> " + neighbor.get(response) + ".\n");
                        currentNode = neighbor.get(response);
                        Animation_Simulator.updateImage(currentNode);
                    }
                }
                getImage();
            }
        });
        
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Animation_Simulator.setUp();
                textArea.setText("");
                getImage();
            }
        });

        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (width < 2000 && height < 2000) {
                    width = (int) (width * 1.25);
                    height = (int) (height * 1.25);
                    ImageIcon icon = new ImageIcon(ZoomImage(width, height, image));
                    imageLabel.setIcon(icon);
                }       
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (width > 100 && height > 100) {
                    width = (int) (width * 0.8);
                    height = (int) (height * 0.8);
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
        new Animation_View();
    }
}
