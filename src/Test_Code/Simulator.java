package Test_Code;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Generator_CFG.Node;
import Generator_CFG.Edge;
import Generator_CFG.GraphViz;

public class Simulator {

    public static List<String> Nodes_Text = new ArrayList<String>();
    public static List<String> Edges_Text = new ArrayList<String>();
    public static Map<Integer, List<Integer>> graphMap = new HashMap<Integer, List<Integer>>();
    public static List<String> olderState = new ArrayList<String>();
    public static List<Integer> olderNode = new ArrayList<Integer>();
    
    private static List<Node> nodes = new ArrayList<Node>();
    private static List<Edge> edges = new ArrayList<Edge>();
    private static int currentNode = 1;
    private static int currentStep = 0;
    private static int backStep = 0;

    private static final int BACK_STEP_MAXIMUM = 5;
    private static final String out_path = "src/Test_Code/Test_Image.png";

    public static int getCurrentNode(){
        return Simulator.currentNode;
    }

    public static int getCurrentState(){
        currentStep++;
        return currentStep;
    }

    public static void decreaseCurrentStep() {
        currentStep--;
    }

    public static int getBackStep() {
        return backStep;
    }
    
    public static boolean isAbleToBackStep(){
        return backStep > 0;
    }

    public static void setUp() {
        // Read input cfg file
        // File input has pattern: many lines, one line has:
        // "Number1" "Label1X" "NumberX" ["Label1Y" "NumberY"]
        // No white space in the number, and may have whitespace in the label
        // Initialize
        graphMap = new HashMap<Integer, List<Integer>>();
        olderState = new ArrayList<String>();
        olderNode = new ArrayList<Integer>();
        Nodes_Text = new ArrayList<String>();
        Edges_Text = new ArrayList<String>();
        // Read input
        readInputText(Edges_Text, "testdata/Test/Edges.txt");
        // Setup currentNode
        currentNode = 1;
        // Setup currentState
        currentStep = 0;
        // Setup backStep
        backStep = 0;
        // Some variables to describe an edge
        int startIdx, endIdx;
        String label;
        // List of all edges
        edges = new ArrayList<Edge>();
		// Create Scanner object to get input from keyboard
        Scanner scanner = new Scanner(System.in);

		// Create the graph from cfg input file
        for (int i = 0; i < Edges_Text.size(); i++) {
            // Get a line in CFG_Text
            String line = Edges_Text.get(i);
            // Remove " from front and last of line in file
            line = line.substring(1, line.length() - 1);
            String token = "\" \"";
            // Tokenize in line
            String[] tokens = line.split(token);
            // Get the start node of an edge
            startIdx = Integer.parseInt(tokens[0]);
            // Create data struture to save index of end node
            graphMap.put(startIdx, new ArrayList<Integer>());
            // Get all edges with startnode has id startIdx
            for (int j=1; j<tokens.length; j++) {
                // Get label of an edge
                label = tokens[j];
                // 
                j++;
                // Get id of end node
                endIdx = Integer.parseInt(tokens[j]);
                // Add this edge to list
                edges.add(new Edge(startIdx, endIdx, label));
                // Add endIdx to list of node can go from startIdx
                graphMap.get(startIdx).add(endIdx);
            }
        }

        // Read input node file
        // File input has pattern: many lines, one line has:
        // "NumberNode" "SrcLine"
        readInputText(Nodes_Text, "testdata/Test/Nodes.txt");
        // Some variables to describe an edge
        int numberNode;
        String srcLine;
        // List of all edges
        nodes = new ArrayList<Node>();

		// Create the graph from cfg input file
        for (int i = 0; i < Nodes_Text.size(); i++) {
            // Get a line in CFG_Text
            String line = Nodes_Text.get(i);

            System.out.println(line);
            // Remove " from front and last of line in file
            line = line.substring(1, line.length() - 1);
            String token = "\" \"";
            // Tokenize line
            String[] tokens = line.split(token);
            // Get number of node
            numberNode = Integer.parseInt(tokens[0]);
            // Get source code line of node
            srcLine = tokens[1];
            // Add new node to data
            nodes.add(new Node(numberNode, srcLine, false, false));
        }

        // Create a file object
        File out = new File(out_path);
        // Get DOT string from list of node and list of edges of cfg_input file
        String strDOT = GenerateDOT(nodes, edges);
        // Get an object of class GraphViz --> to write graph to file
        GraphViz gv = new GraphViz();
        // Write graph from cfg_input file
        gv.writeGraphToFile(gv.getGraph(strDOT, "png"), out);

        // Add to currentNode to list
        olderNode.add(currentNode);
        // Add state to list
        olderState.add(strDOT);
        // Get firstnode
        currentNode = nodes.get(0).GetNodeNumber();
        
        // Close scanner
        scanner.close();
    }

    public static List<Integer> getNeighbor(int currentNode) {
        // Get neighbor by nodeId of near node
        List<Integer> neighbor = graphMap.get(currentNode);
        return neighbor;
    }

    public static void updateImage(int nextNode) {
        // Replace this edge to simulate
        edges = replaceEdge(edges, currentNode, nextNode);
        // Get DOT string from list all of edges
        String strDOT = GenerateDOT(nodes, edges);
        // Create an object of class GraphViz
        GraphViz gv = new GraphViz();
        // Write graph for the edge i in path to simulate
        gv.writeGraphToFile(gv.getGraph(strDOT, "png"), new File(out_path));
        // Set currentNode = nextNode for the next loop
        Simulator.currentNode = nextNode;
        // Add nextNode to the list
        olderNode.add(nextNode);
        // Add new State to the list
        olderState.add(strDOT);
        // Set backStep can go max = BACK_STEP_MAXIMUM
        if (backStep < BACK_STEP_MAXIMUM) {
            backStep++;
        }
    }

    public static void backImage() {
        // Decrease backStep 1: the capacity of back steps
        backStep--;
        // Decrease currentStep 1
        currentStep--;
        // Restore the edge has colored
        edges = restore(edges, olderNode.get(olderNode.size() - 2), currentNode);
        // Remove last node from olderNode
        olderNode.remove(olderNode.size() - 1);
        // Set currentNode from olderNode
        currentNode = olderNode.get(olderNode.size() - 1);
        // Remove last state from olderState
        olderState.remove(olderState.size() - 1);
        // Get the back state
        String strDOT = olderState.get(olderState.size() - 1);
        // Create an object of class GraphViz
        GraphViz gv = new GraphViz();
        // Write graph for the edge i in path to simulate
        gv.writeGraphToFile(gv.getGraph(strDOT, "png"), new File(out_path));
    }

    public static void main(String args[]) {
        // Read input cfg file
        // File input has pattern: many lines, one line has:
        // "Number1" "Label1X" "NumberX" ["Label1Y" "NumberY"]
        // No white space in the number, and may have whitespace in the label
        readInputText(Edges_Text, "testdata/Test/Edges.txt");
        // Some variables to describe an edge
        int startIdx, endIdx;
        String label;
        // List of all edges
        List<Edge> edges = new ArrayList<Edge>();
		// Create Scanner object to get input from keyboard
        Scanner scanner = new Scanner(System.in);

		// Create the graph from cfg input file
        for (int i = 0; i < Edges_Text.size(); i++) {
            // Get a line in CFG_Text
            String line = Edges_Text.get(i);
            // Remove " from front and last of line in file
            line = line.substring(1, line.length() - 1);
            String token = "\" \"";
            // Tokenize in line
            String[] tokens = line.split(token);
            // Get the start node of an edge
            startIdx = Integer.parseInt(tokens[0]);
            // Create data struture to save index of end node
            graphMap.put(startIdx, new ArrayList<Integer>());
            // Get all edges with startnode has id startIdx
            for (int j=1; j<tokens.length; j++) {
                // Get label of an edge
                label = tokens[j];
                // 
                j++;
                // Get id of end node
                endIdx = Integer.parseInt(tokens[j]);
                // Add this edge to list
                edges.add(new Edge(startIdx, endIdx, label));
                // Add endIdx to list of node can go from startIdx
                graphMap.get(startIdx).add(endIdx);
            }
        }

        // Read input node file
        // File input has pattern: many lines, one line has:
        // "NumberNode" "SrcLine"
        readInputText(Nodes_Text, "testdata/Test/Nodes.txt");
        // Some variables to describe an edge
        int numberNode;
        String srcLine;
        // List of all edges
        List<Node> nodes = new ArrayList<Node>();

		// Create the graph from cfg input file
        for (int i = 0; i < Nodes_Text.size(); i++) {
            // Get a line in CFG_Text
            String line = Nodes_Text.get(i);

            System.out.println(line);
            // Remove " from front and last of line in file
            line = line.substring(1, line.length() - 1);
            String token = "\" \"";
            // Tokenize line
            String[] tokens = line.split(token);
            // Get number of node
            numberNode = Integer.parseInt(tokens[0]);
            // Get source code line of node
            srcLine = tokens[1];
            // Add new node to data
            nodes.add(new Node(numberNode, srcLine, false, false));
        }

        // This code to check if graphMap keyset is insert right
        /*
        for (int key : graphMap.keySet()) {
            System.out.println();
            System.out.println("Key = " + key);
            System.out.println("Number of neighbor = " + graphMap.get(key).size());
            for (int value : graphMap.get(key)) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
        */

        // String path to save file
        String out_path = "src/Test_Code/Test_Image.png";
        // Create a file object
        File out = new File(out_path);
        // Get DOT string from list edges of cfg_input file
        String strDOT = GenerateDOT(nodes, edges);
        // Get an object of class GraphViz --> to write graph to file
        GraphViz gv = new GraphViz();
        // Write graph from cfg_input file
        gv.writeGraphToFile(gv.getGraph(strDOT, "png"), out);


        // Get start node (or current node), default = 1
        // After that, we can create for user to input the start node
        int currentNode = 1;
        // Note
        System.out.print("Input start node = ");
        // Input start node
        String startNode = scanner.nextLine();
        // Check input is number
        if (!startNode.matches("[0-9]+")) {
            // Print error and default
            System.out.println("Wrong input. Default start node = " + currentNode);
        }
        else {
            // Set number of start node
            currentNode = Integer.parseInt(startNode);
            // Print notification in console
            System.out.println("Start input = " + currentNode);
        }
        System.out.println();
        // Next node for the end of edge
        int nextNode = -1;
        // Create simulator animation with choice from console
        while (true) {
			// Set nextNode equal -1 to check after
            nextNode = -1;
			// Print currentNode to console
            System.out.println("Current node: " + currentNode);
            // Get list neighbor of currentNode
			List<Integer> neighbor = graphMap.get(currentNode);
            // Check if neighbor null (means has no neighbor)
			if (neighbor == null) {
				// Notify end of simulate
                System.out.println("Simulator end.");
                // Out of loop
				break;
            }
			// Check if neighbor list has been created but not have any element
            if (neighbor.size() == 0) {
				// Notify end of simulate
                System.out.println("Simulator end.");
                // Out of loop
				break;
            }
			// Check if this has one neighbor
            if (neighbor.size() == 1) {
				// Get that neighbor is nextNode
                nextNode = neighbor.get(0);
				// Print nextNode to console
                System.out.println("Next node = " + nextNode);
            }
			// When currentNode has more than one neighbor
            else {
				// Print number of neighbor of this currentNode
                System.out.print("From current node: " + neighbor.size() + " ways to go: ");
                // Print each of neighbor
				for (int output : neighbor) {
                    System.out.print(output + " ");
                }
                System.out.println("\n");
				// Create loopInput to check input for nextNode
                boolean loopInput = true;
                do {
                    System.out.print("Where do you want to go: ");
					// Input from keyboard
                    String input = scanner.nextLine();
                    // Check input is a number
					if (!input.matches("[0-9]+")) {
                        System.out.println("Please input a number.");
                    }
					// When input is a number
                    else {
						// Convert string to number
                        int inputNode = Integer.parseInt(input);
                        // Check if inputNode is a neighbor
						for (int output : neighbor) {
							// When inputNode is a neighbor
                            if (inputNode == output) {
								// Set that neighbor to nextNode
                                nextNode = output;
								// Print number of nextNode
                                System.out.println("Next node = " + nextNode);
                                // Set loopInput to false to stop loop do...while
								loopInput = false;
								// Break from this loop to check input in neighbor
                                break;
                            }
                        }
						// After check all, if not found neighbor 
                        if (nextNode == -1) {
							// Print error
                            System.out.println("Please input a neighbor node in list.");
                        }
                    }
                } while (loopInput);
            }
            // Replace this edge to simulate
            edges = replaceEdge(edges, currentNode, nextNode);
            // Get DOT string from list all of edges
            strDOT = GenerateDOT(nodes, edges);
            // Write graph for the edge i in path to simulate
            gv.writeGraphToFile(gv.getGraph(strDOT, "png"), new File(out_path));
            // Set currentNode = nextNode for the next loop
            currentNode = nextNode;
            // Print a blank line
            System.out.print("\nPress enter key to continue.\n");
            // For user input
			scanner.nextLine();
        }
		// Close scanner
        scanner.close();
    }
    
    /**
     * This method return node with nodeNumber
     * @param nodeNumber: index of this node
     * @return node: has index equal nodeNumber
     */
    public static Node getNode(int nodeNumber) {
        return nodes.get(nodeNumber);
    }

    /**
     * This method return edge has index of startNode and endNode
     * @param currentNode: index of startNode
     * @param nextNode: index of endNode
     * @return edge: edge
     */
    public static Edge getEdge(int currentNode, int nextNode) {
        if (nextNode < 0) {
            return null;
        }
        // Find the edge to simulate, remove old edge and add new edge
        for (int j = 0; j < edges.size(); j++) {
            if (edges.get(j).GetStart() == currentNode && edges.get(j).GetEnd() == nextNode) {
                return edges.get(j);
            }
        }
        // Return the result
        return null;
    }
    
    /**
     * This method use to color the edge to red color to simulate path
     * 
     * @param edges: List all edges
     * @param currentNode: The start of edge need to replace
     * @param nextNode: The end of edge need to replace
     * @return edges: List all edges after replace
     */
    public static List<Edge> replaceEdge(List<Edge> edges, int currentNode, int nextNode) {
        if (nextNode < 0) {
            return edges;
        }
        // Find the edge to simulate, remove old edge and add new edge
        for (int j = 0; j < edges.size(); j++) {
            if (edges.get(j).GetStart() == currentNode && edges.get(j).GetEnd() == nextNode) {
                // Get the label of this edges
                String label = edges.get(j).GetLabel();
                // Add property to set edge with red color
                label = label + " color = \"red\"";
                // Remove this edge
                edges.remove(j);
                // Add new edge to simulate in list of edges
                edges.add(j, new Edge(currentNode, nextNode, label));
                // Out of this loop --> Read next line in path
                break;
            }
        }
        // Return the result
        return edges;
    }

    /**
     * This method use to restore the edge to simulate path
     * 
     * @param edges: List all edges
     * @param currentNode: The start of edge need to restore
     * @param nextNode: The end of edge need to restore
     * @return edges: List all edges after restore
     */
    public static List<Edge> restore(List<Edge> edges, int currentNode, int nextNode) {
        if (nextNode < 0) {
            return edges;
        }
        // Find the edge to simulate, remove old edge and add new edge
        for (int j = 0; j < edges.size(); j++) {
            if (edges.get(j).GetStart() == currentNode && edges.get(j).GetEnd() == nextNode) {
                // Get the label of this edges
                String label = edges.get(j).GetLabel();
                // Get index of set color
                int index = label.indexOf(" color");
                // Remove color attribute from label
                label = label.substring(0, index);
                // Remove this edge
                edges.remove(j);
                // Add new edge to simulate in list of edges
                edges.add(j, new Edge(currentNode, nextNode, label));
                // Out of this loop --> Read next line in path
                break;
            }
        }
        // Return the result
        return edges;
    }

    /**
     * This method read all line from input file into list<String> And one element
     * of list is a line from input file
     * 
     * @param variable: Variable to read input file in
     * @param path:     Path of the input file
     */
    public static void readInputText(List<String> variable, String path) {
        // Create variable to stream to input file
        FileInputStream fileInputStream = null;

        try {
            // Open file with path
            fileInputStream = new FileInputStream(path);
        } catch (IOException e) {
            // When can not open file
            System.out.println("Can't open file stream");
            ;
            // Exit the system
            System.exit(1);
        }
        // Create scanner to read line from fileInputStream
        Scanner scanner = new Scanner(fileInputStream);
        // When not end of file
        while (scanner.hasNextLine()) {
            // Add line to list<String>
            variable.add(scanner.nextLine());
        }
        // Close the scanner
        scanner.close();

        try {
            // Close file input
            fileInputStream.close();
        } catch (IOException e) {
            // If close file errored
            System.out.println("Can't close file stream.");
        }
    }

    /**
     * This method create file DOT from list of edges. File DOT will become input
     * for library GraphViz to paint control_flow_graph
     * 
     * @param edges: list all of edges
     * @return: A string contains DOT_file pattern
     */
    public static String GenerateDOT(List<Edge> edges) {
        String strDOT = "digraph cfg{\n";

        for (Edge e : edges) {

            strDOT += "\t" + e.GetStart() + " -> " + e.GetEnd();

            if (e.GetLabel().length() > 0) {
                int index = e.GetLabel().indexOf(" color");
                if (index >= 0) {
                    strDOT += "[label = \"" + e.GetLabel().substring(0, index) + "\"" +  e.GetLabel().substring(index) + "]";
                }
                else {
                    strDOT += "[label = \"" + e.GetLabel() + "\"]";
                }
            }

            // attributes

            strDOT += ";\n";
        }

        strDOT += "}";

        System.out.println(strDOT);

        return strDOT;
    }

    /**
     * This method create file DOT from list of edges. File DOT will become input
     * for library GraphViz to paint control_flow_graph
     * 
     * @param nodes: list all of nodes
     * @param edges: list all of edges
     * @return: A string contains DOT_file pattern
     */
    public static String GenerateDOT(List<Node> nodes, List<Edge> edges) {
        String strDOT = "digraph cfg{\n";

        for (Node n: nodes){

            System.out.println(n.toString());
			
			String line = "";
			
			//attributes
			if (n.isEntry()){
				line += "\tstart [style=invis];\n\tstart -> "+n.GetNodeNumber()+";\n"; // invisible entry node required to draw the entry arrow
				
			}

			if (n.GetSrcLine().contains("%forcelabel%")){
			//	line += "\t"+n.node_number+" [xlabel=\"" + removeTags(n.srcline).trim() + "\",labelloc=\"c\"]"; // label the node if forced
			}

			// Modify statement
			if (n.GetSrcLine().length() > 0) {
				if (n.isExit()) {
					line += "\t" + n.GetNodeNumber() + "\t[label=\"" + n.GetSrcLine() + "\" penwidth=4]\n";
				}
				else if (n.GetSrcLine().toLowerCase().matches("^(while|if|else).*")) {
					line += "\t" + n.GetNodeNumber() + "\t[label=\"" + n.GetSrcLine() + "\" shape=\"diamond\"]\n";
				}
				else {
					line += "\t" + n.GetNodeNumber() + "\t[label=\"" + n.GetSrcLine() + "\"]\n";
				}
			}
			
			if (line.length() > 0) strDOT += line;
			
		}

        for (Edge e : edges) {

            strDOT += "\t" + e.GetStart() + " -> " + e.GetEnd();

            if (e.GetLabel().length() > 0) {
                int index = e.GetLabel().indexOf(" color");
                if (index >= 0) {
                    strDOT += "[label = \"" + e.GetLabel().substring(0, index) + "\"" +  e.GetLabel().substring(index) + "]";
                }
                else {
                    strDOT += "[label = \"" + e.GetLabel() + "\"]";
                }
            }

            // attributes

            strDOT += ";\n";
        }

        strDOT += "}";

        System.out.println(strDOT);

        return strDOT;
    }
}
