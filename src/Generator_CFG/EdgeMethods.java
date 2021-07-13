package Generator_CFG;
import java.util.*;
/**
 * 
 */
public class EdgeMethods {
    private List<List<Integer>> edgeStartLines = new ArrayList<List<Integer>>();
    private int currentIf = 0;

    /**
     * 
     * @param edges: list all the edges
     * @param startidx: the index of the start line
     * @param endidx: the index of the end line
     * @param labelString: label of this edges
     * @return list of all edges after add new edge
     */
    private List<Edge> addEdge(List<Edge> edges, int startidx, int endidx, String labelString){
		// Add new edge with start index, end index, label of edge
		edges.add(new Edge(startidx, endidx, labelString));
		// Return result
        return edges;
	}  

    /**
     * 
     * @param lines: list all lines in the source code
     * @param edges: list all the edges
     * @param openline: the first line of this block of statements
     * @param currentline: the current line has '}'
     * @return list all of the edges after add edges for loop
     */
    public List<Edge> addEdgesForLoops(List<String> lines, List<Edge> edges, int openline, int currentline) {
        // For while-loops and for-loops, add an edge back to the start
		if (lines.get(openline).toLowerCase().matches("^(for|while).*")){
            // Add an edge from start loop to the next line
            edges = addEdge(edges, openline, openline + 1, "True");
            // Add an edge from the last statement back to the start
			edges = addEdge(edges, getPrevLine(lines, currentline), openline, "");	
            // Add an edge when the condition in the loop wrong
            edges = addEdge(edges, openline, getNextLine(lines, currentline), "False");
		}
        // For do-while-loops
   		else{
            // Check the next line after current line is while statement
            if ((currentline + 1) >= lines.size() || lines.get(currentline + 1).toLowerCase().matches("^(while).")) {
                System.err.println("Not do_while statement");
                System.exit(2);
            }
            else {
                // Add edge before the do-while block to the first line in the block, skip "do" statement
                // NOTE: Need improve, this line code is not right
			    // edges = addEdge(edges, getPrevLine(lines, openline), getNextLine(lines, openline), "Unchecked");
                // Add edge from the last line in the block to the while statement
                edges = addEdge(edges, getPrevLine(lines, currentline), currentline + 1, "");
                // Add loop edge in do_while statement
                edges = addEdge(edges, currentline + 1, getNextLine(lines, openline), "True");
			    // Add exit edge when the condition wrong in do_while statement
                edges = addEdge(edges, currentline + 1, getNextLine(lines, currentline + 1), "False");
            }
		}
        // Return result
        return edges;
    }

    /**
     * 
     * @param lines: list all lines in the source code
     * @param edges: list all the edges
     * @param openline: the first line of this block of statements
     * @param currentline: the current line has '}' (the last line of this block)
     * @return list all of the edges after add edges for if-condition
     */
    public List<Edge> addEdgesForIfCondition(List<String> lines, List<Edge> edges, int openline, int currentline) {
        // 
        if (lines.get(openline).toLowerCase().matches("^(if).*")) {
            currentIf++;
            edgeStartLines.add(new ArrayList<Integer>());
        }
        // Check when the openline statement is the "else" or "else if" block, code had "if" block or not
        if (lines.get(openline).toLowerCase().matches("^(else).*")) {
            // Else without if when number of last lines of the before blocks equal zero
            if (currentIf == 0) {
                // Print error
                System.err.println("Else without if. Openline = " + openline);
                // Exit the program
                System.exit(2);
            }
        }
        
        // Add edge from this if-elseif-else condition to the next line
        edges = addEdge(edges, openline, openline + 1, "True");

        // If we're not done with the conditional block, save the start of this edge until we find the end of the block
        if (lines.size() > currentline + 1 && lines.get(currentline + 1).toLowerCase().matches("^else.*")){
            // Add to list number of lines of statement, which has out of the conditional block after that
            edgeStartLines.get(currentIf - 1).add(getPrevLine(lines, currentline));
            // Add edges from openline to the line has "else" or "else if", when the condition in openline false
            if (lines.get(currentline + 1).toLowerCase().matches("^(else if).*")) {
                // When the next conditional if the "else if" block
                edges = addEdge(edges, openline, currentline + 1, "False");
            }
            else {
                // When the next conditional is the "else" block
                // Add edge to the next line of the "else" line, cause the "else" line will be delete
                edges = addEdge(edges, openline, currentline + 2, "False");
            }
        }
        // When we done with conditional block, add edge from all last statements from all blocks to the line after conditional block
        else{
            for (Integer start: edgeStartLines.get(currentIf - 1)){
                // Add edge from last of a block to outside conditional block
                edges = addEdge(edges, start, getNextLine(lines, currentline), "");
            }
            // Remove all list number of statement, which has outside the conditional block
            edgeStartLines.remove(currentIf - 1);
            // Decrease number of conditional block inside conditional block
            currentIf--;
            // Add edges from last statement of the current block of conditional block to outside 
            // Don't add edges if this statement is "break" or "continue"
            if (!lines.get(getPrevLine(lines, currentline)).toLowerCase().matches("^(break;|continue;)")) {
                edges = addEdge(edges, getPrevLine(lines, currentline), getNextLine(lines, currentline), "");
            }
            // Add edge from openline to the line after conditional block when the conditional false in openline
            edges = addEdge(edges, openline, getNextLine(lines, currentline), "False");
        }
        // Return result
        return edges;
    }

    /**
     * 
     * @param lines: list all lines in the source code
     * @param edges: list all the edges
     * @param openline: the first line of this block of statements
     * @param currentline: the current line has '}' (the last line of this block)
     * @return list all of the edges after add edges for switch-condition
     */
    public List<Edge> addEdgesForSwitchCondition(List<String> lines, List<Edge> edges, int openline, int currentline) {
        // Iterate through the case statement
        for (int k = openline; k < currentline; k++){
            // If this line is "case" or "default" line
            if (lines.get(k).matches("^(case|default).*")){
                // Add edge from "switch" line to the next line of "case" or "default" line
                edges = addEdge(edges, openline, getNextLine(lines, k), lines.get(k));
            }
            // If this line is "break" line
            else if (lines.get(k).matches("^break;")) {
                // Add edge from the line before "break" line to outside "switch" block
                edges = addEdge(edges, k, getNextLine(lines, currentline), "");
            }
            // If this line is a normal line
            else {
                // Add edge from this line to the next line (not "case", "default" line)
                edges = addEdge(edges, k, getNextLine(lines,k), "");
            }
        }
        // Return result
        return edges;
    }

    /**
     * 
     * @param lines: list all lines in the source code
     * @param edges: list all the edges
     * @param currentline: the current line has "continue" statement
     * @return list all of the edges after add edges for continue statement
     */
    public List<Edge> addEdgesForContinueStatement(List<String> lines, List<Edge> edges, int currentline) {
        // Find the openline of the loop where "continue" statement into
        int openline = currentline;
        // Number of bracelets 
        int outside = 0;
        // Find the openline
        while (openline >= 0) {
            // Check if that line is the end of a block (contains '}')
            if (lines.get(openline).contains("}")) {
                // Increse outside number
                outside++;
            }
            // Check if that line is the open of a block (contain '{')
            else if (lines.get(openline).contains("{")) {
                // Decrease outside number
                outside--;
            }
            // Check if that line is start of a loop and this loop contain the "continue" statement
            if (lines.get(openline).toLowerCase().matches("^(for|while|do).*") && outside < 0) {
                // Found open loop - break
                break;
            }
            openline--;
        }
        // Check if continue in a loop 
        if (openline == -1) {
            // Print error
            System.err.println("Continue without a loop");
            // Exit
            System.exit(2);
        }
        // When openline is a start line for a loop
        else {
            // Find the close line of this loop
            int closeline = openline + 1;
            int depth = 0;
            // When the line in lines
            while (closeline < lines.size()) {
                // Check if it open a new block
                if (lines.get(closeline).contains("{")) {
                    // Increse depth (number of blocks inside a block)
                    depth++;
                }
                // Check if it close a block
                else if (lines.get(closeline).contains("}")) {
                    // Check if it is this loop block
                    if (depth == 0) {
                        // Found closeline - break
                        break;
                    }
                    // Or it is a block inside the loop block
                    else {
                        depth--;
                    }
                }
                // Increse the variable
                closeline++;
            }
            // Check if close line of this loop has been found
            if (closeline == lines.size()) {
                // Not found
                System.err.println("Loop without closing bracelet: Openline = " + openline);
                // Exit
                System.exit(2);
            }
            // When find the close of this loop
            else {
                // Remove all edges start from "continue" statement
                for (int i=0; i < edges.size(); i++) {
                    // Remove all edges start from this "continue" statement
                    if (edges.get(i).GetStart() == currentline) {
                        // Remove it
                        edges.remove(i);
                        // Decrease i because we delete line i, so line i+1 --> i
                        i--;
                    }
                }
                // If the last statement is "forcenode" (when for loop --> while loop)
                if (lines.get(closeline - 1).toLowerCase().contains("forcenode")) {
                    // Add an edge from "continue" statement to here
                    edges = addEdge(edges, currentline, closeline - 1, "");
                }
                // If not (while or do-while loop)
                else {
                    // Add an edge from "continue" stetament to start loop
                    edges = addEdge(edges, currentline, getNextLine(lines, openline - 1), "");
                }
            }
        }
        // Return result
        return edges;
    }

    /**
     * 
     * @param lines: list all lines in the source code
     * @param edges: list all the edges
     * @param currentline: the current line has "break" statement
     * @return: list all of the edges after add edges for break statement
     */
    public List<Edge> addEdgesForBreakStatement(List<String> lines, List<Edge> edges, int currentline) {
        // Find the openline of the loop where "break" statement into
        int openline = currentline;
        // Number of bracelets 
        int outside = 0;
        // Find the openline
        while (openline >= 0) {
            // Check if that line is the end of a block (contains '}')
            if (lines.get(openline).contains("}")) {
                // Increse outside number
                outside++;
            }
            // Check if that line is the open of a block (contain '{')
            else if (lines.get(openline).contains("{")) {
                // Decrease outside number
                outside--;
            }
            // Check if that line is start of a switch-case condition
            if (lines.get(openline).toLowerCase().matches("^(switch).*") && outside < 0) {
                // Break in the switch-condition
                // It had been add edges in method addEdgeForSwitchCondition
                // Return result
                return edges;
            }
            // Check if that line is start of a loop
            if (lines.get(openline).toLowerCase().matches("^(for|while|do).*") && outside < 0) {
                // Found open loop - break
                break;
            }
            openline--;
        }
        // Check if continue in a loop 
        if (openline == -1) {
            // Print error
            System.err.println("Continue without a loop");
            // Exit
            System.exit(2);
        }
        // When openline is a start line for a loop
        else {
            // Find the close line of this loop
            int closeline = openline + 1;
            // Set depth of bracelets equal zero
            int depth = 0;
            // When the line in lines
            while (closeline < lines.size()) {
                // Check if it open a new block
                if (lines.get(closeline).contains("{")) {
                    // Increse depth (number of blocks inside a block)
                    depth++;
                }
                // Check if it close a block
                else if (lines.get(closeline).contains("}")) {
                    // Check if it is this loop block
                    if (depth == 0) {
                        // Found closeline - break
                        break;
                    }
                    // Or it is a block inside the loop block
                    else {
                        depth--;
                    }
                }
                // Increse the variable
                closeline++;
            }
            // Check if close line of this loop has been found
            if (closeline == lines.size()) {
                // Not found
                System.err.println("Loop without closing bracelet: Openline = " + openline);
                // Exit
                System.exit(2);
            }
            // When find the close of this loop
            else {
                // Remove all edge start from "break" statement
                for (int i=0; i < edges.size(); i++) {
                    // Get all edges start from this "break" statement
                    if (edges.get(i).GetStart() == currentline) {
                        // Remove it
                        edges.remove(i);
                        // Decrease i, because we have delete line i, and line i+1 --> i
                        i--;
                    }
                }
                // Add an edge from "break" statement to outside this loop
                edges = addEdge(edges, currentline, getNextLine(lines, closeline), "");
            }
        }
        // Return result
        return edges;
    }

    /**
     * 
     * @param edges: list all edges
     * @return: list all edges after remove entry edges (edges start from node with id < 0)
     */
    public List<Edge> removeEntryEdges(List<Edge> edges) {
        // Iterator list of edges
        for (int i=0;i<edges.size();i++) {
            // Check if edges start without a node
            if (edges.get(i).GetStart() < 0) {
                // Remove it
                edges.remove(i);
            }
        }
		// Return result
        return edges;
    }

    /**
     * 
     * @param edges: list all edges
     * @return: list all edges after remove duplicate edges (if it connect the same nodes, and same label
     * or one edge doesn't have a label)
     */
    public List<Edge> removeDuplicateEdges(List<Edge> edges) {
        // Iterator in list of edges
        for (int i=0; i<edges.size();i++){
			for (int j=i+1; j<edges.size(); j++){
                // If two edges connect the same nodes
				if (edges.get(j).GetStart() == edges.get(i).GetStart() && edges.get(j).GetEnd() == edges.get(i).GetEnd()) {
					// If edge j not contain a label
                    if (edges.get(j).GetLabel().equals("")) {
						// Remove edge j
                        edges.remove(j);
                        // Edge j+1 --> i
                        j--;
					}
                    // If edge i not contain a label
					else if (edges.get(i).GetLabel().equals("")) {
						// Remove edge i
                        edges.remove(i);
                        // Edge i+1 --> i
						i--;
                        // Edge j+1 --> j
                        j--;
					}
                    // If two edges have the same label
                    else if (edges.get(i).GetLabel().equals(edges.get(j).GetLabel())) {
                        // Remove edge j
                        edges.remove(j);
                        // Edge j+1 --> j
                        j--;
                    }
				}
			}
		}
        // Return result
        return edges;
    }

    /**
     * 
     * @param lines: list all lines in source code
     * @param start: this current line to start
     * @return: the line of code before this current line
     */
    private int getPrevLine(List<String> lines, int start){
		
		int prevEdge=start-1;
		
		while (prevEdge > -1 && lines.get(prevEdge).equals("}")) {
            prevEdge--;
        }
		
		return prevEdge;
		
	}

    /**
     * 
     * @param lines: list all lines in source code
     * @param start: this current line to start
     * @return: the line of code after this current line, which will become a node
     */
    private int getNextLine(List<String> lines, int start){
		
		int nextEdge=start+1;
		
		while (nextEdge < lines.size() && (lines.get(nextEdge).equals("}") ||
			lines.get(nextEdge).toLowerCase().matches("^(do|case|default).*"))) {
                nextEdge++;
            }
		
		return nextEdge;
	
	}

}
