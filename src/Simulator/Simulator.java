package Simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import Generator_CFG.Edge;
import Generator_CFG.GraphViz;

public class Simulator {
    
    public static List<String> CFG_Text = new ArrayList<String>();
    public static List<String> Path_Text = new ArrayList<String>();

    public static void main(String args[]) {
        // Read input cfg file
        // File input has pattern: many lines, one line has:
        // Number1 "Label1X" NumberX ["Label1Y" NumberY]
        // No white space in the number, and no whitespace in the label
        readInputText(CFG_Text, "testdata/Simulator/CFG_Example.txt");
        // Read input path file
        // File input has pattern: many lines, one line has:
        // NumberX "LabelXY" NumberY
        // No white space in the number, and no whitespace in the label
        readInputText(Path_Text, "testdata/Simulator/Path_Example.txt");
        // Create the graph from cfg input file
        int startIdx, endIdx;
        String label;
        // List of all edges
        List<Edge> edges = new ArrayList<Edge>();
        
        for (int i = 0; i < CFG_Text.size(); i++) {
            // Get a line in CFG_Text
            String line = CFG_Text.get(i);
            // Tokenize the line with whitespace
            StringTokenizer stk = new StringTokenizer(line);
            // The first token is the start index of edges
            startIdx = Integer.parseInt(stk.nextToken());
            // The after tokens go with pair
            while (stk.hasMoreTokens()) {
                // The first of pair is the label of an edge
                label = stk.nextToken();
                // The second of pair is the finish of an edge
                endIdx = Integer.parseInt(stk.nextToken());
                // Add this edge to list
                edges.add(new Edge(startIdx, endIdx, label));
            }
        }
        // String path to save file
        String out_path = "testdata/Simulator/CFG_Example";
        // Create a file object
        File out = new File(out_path + ".png");
        // Get DOT string from list edges of cfg_input file
        String strDOT = GenerateDOT(edges);
		// Get an object of class GraphViz --> to write graph to file
		GraphViz gv = new GraphViz();
        // Write graph from cfg_input file
		gv.writeGraphToFile(gv.getGraph(strDOT, "png"), out);

        // Create simulator picture for path input file
        for (int i=0; i<Path_Text.size(); i++) {
            // Get a line in Path_Text
            String line = Path_Text.get(i);
            // Tokenize a line with whitespace --> a line has three tokens
            StringTokenizer stk = new StringTokenizer(line);
            // The first token is the start index of the edge
            startIdx = Integer.parseInt(stk.nextToken());
            // The second token is the label of the edge --> add color red to MARK
            label = stk.nextToken() + " color = \"red\"";
            // The third token is the end index of the edge
            endIdx = Integer.parseInt(stk.nextToken());
            //edges.add(new Edge(startIdx, endIdx, label));
            
            // Edge to save old edge for 
            // Edge old_edge = null;
            // Index where old_edge in list
            // int old_index = -1;
            //
            // Find the edge to simulate, remove old edge and add new edge
            for (int j=0; j<edges.size(); j++) {
                if (edges.get(j).GetStart() == startIdx && edges.get(j).GetEnd() == endIdx) {
                    // Get the old edge
                    // old_edge = edges.get(j);
                    // Set the index to remove after
                    // old_index = j;
                    //
                    // Remove this edge 
                    edges.remove(j);
                    // Add new edge to simulate in list of edges 
                    edges.add(j, new Edge(startIdx, endIdx, label));
                    // Out of this loop --> Read next line in path
                    break;
                }
            }

            // Get DOT string from list all of edges
            strDOT = GenerateDOT(edges);
            // Write graph for the edge i in path to simulate
            gv.writeGraphToFile(gv.getGraph(strDOT, "png"), new File(out_path + "_" + (i + 1) + ".png"));
            // Restore the edge has been remove to first stage
            // Remove the edge replace with red color
            // edges.remove(old_index);
            // Restore this edge from start
            // edges.add(old_index, old_edge);
            //
        }
    }

    /**
     * This method read all line from input file into list<String>
     * And one element of list is a line from input file
     * 
     * @param variable: Variable to read input file in
     * @param path: Path of the input file
     */
    public static void readInputText(List<String> variable, String path) {
        // Create variable to stream to input file
        FileInputStream fileInputStream = null;

        try {
            // Open file with path
            fileInputStream = new FileInputStream(path);
        }
        catch (IOException e){
            // When can not open file
            System.out.println("Can't open file stream");;
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
     * This method create file DOT from list of edges. File DOT will become
     * input for library GraphViz to paint control_flow_graph
     * 
     * @param edges: list all of edges
     * @return: A string contains DOT_file pattern
     */
    public static String GenerateDOT(List<Edge> edges) {
        String strDOT = "digraph cfg{\n";

        for (Edge e: edges){
			// strDOT += "\t"+e.GetStart()+" -> "+e.GetEnd() + "[label = " + e.GetLabel() + "]";
			
            strDOT += "\t"+e.GetStart()+" -> "+e.GetEnd() + "[label = " + e.GetLabel() + "]";

			// attributes
			
			strDOT += ";\n";
		}
		
		strDOT += "}";
		
        System.out.println(strDOT);

		return strDOT;		
    }
}
