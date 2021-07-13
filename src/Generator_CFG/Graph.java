package Generator_CFG;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Graph {
	private static final int _0 = 0;
    private List<Node> nodes;		// Node list
	private List<Edge> edges;		// Edge list
	private List<String> lines;
	
	private boolean printDebug;
	
	public Graph() {
		
		lines = new ArrayList<String>();
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
	
		printDebug = false;
	}
	
	public void setDebug(boolean d){ printDebug = d; }
	
	// Add a line of source code
	public void AddSrcLine(String line){
		lines.add(line);		
	}
	
	// Add a node to the graph
	public void AddNode(Node _node) {
		
		nodes.add(_node);
		
	}
	
	// Add an edge to the graph
	public void AddEdge(Edge _edge) {
		edges.add(_edge);
	}
	
	// Get the first entry node
	public Node GetEntryNode() {
		Iterator<Node> iterator = nodes.iterator();
		Node node;
		while(iterator.hasNext()) {
			node = iterator.next();
			//System.out.println("GetEntryNode = " + node);
			if(node.isEntry() == true) {
				return node;
			}
		}
		return null;
	}
	
	// Get all the entry node list
	public List<Node> GetEntryNodeList() {
		List<Node> node_list = new LinkedList<Node>();
		Iterator<Node> iterator = nodes.iterator();
		Node node;
		while(iterator.hasNext()) {
			node = iterator.next();
			//System.out.println("GetEntryNode = " + node);
			if(node.isEntry() == true) {
				node_list.add(node);
			}
		}
		return node_list;
	}
	
	// Get the first exit node
	public Node GetExitNode() {
		Iterator<Node> iterator = nodes.iterator();
		Node node;
		while(iterator.hasNext()) {
			node = iterator.next();
			//System.out.println(node);
			if(node.isExit() == true) {
				return node;
			}
		}
		return null;
	}
	
	// Get all the exit node list
	public List<Node> GetExitNodeList() {
		List<Node> node_list = new LinkedList<Node>();
		Iterator<Node> iterator = nodes.iterator();
		Node node;
		while(iterator.hasNext()) {
			node = iterator.next();
			//System.out.println(node);
			if(node.isExit() == true) {
				node_list.add(node);
			}
		}
		return node_list;
	}
	
	// Get edge list that start from Node "_node"
	public List<Edge> GetEdgeStartFrom(Node _node) {
		List<Edge> chosenEdges = new LinkedList<Edge>();
		Iterator<Edge> iterator = edges.iterator();
		Edge edge;
		while(iterator.hasNext()) {
			edge = iterator.next();
			if(edge.GetStart() == _node.GetNodeNumber()) {
				chosenEdges.add(edge);
				//System.out.println("Node num = " + _node.GetNodeNumber() + ", Edge = " + edge);
			}
		}
		return chosenEdges;
	}
	
	// Get the node with a specified node number
	public Node GetNode(int _node_num) {
		Iterator<Node> iterator = nodes.iterator();
		Node node;
		while(iterator.hasNext()) {
			node = iterator.next();
			//System.out.println(node);
			if(node.GetNodeNumber() == _node_num) {
				return node;
			}
		}
		return null;
	}
	
	public void PrintNodes() {
		Iterator<Node> iterator = nodes.iterator();
		Node node;
		while(iterator.hasNext()) {
			node = iterator.next();
			System.out.println(node);
		}
	}
	
	public void PrintEdges() {
		Iterator<Edge> iterator = edges.iterator();
		Edge edge;
		while(iterator.hasNext()) {
			edge = iterator.next();
			System.out.println(edge);
		}
	}
	
	public void build(){
		
		cleanup();
		addDummyNodes();
		getNodes();
		removeDummyNodes();
		numberNodes();
		//combineNodes();
		fixNumbering();
		cleanCode();
		
	}

	public void writeData(String path) {

		System.out.println("----- Write data to file -----");
		File outEdge = new File(path + "/Edges.txt");

		try {
			// Create FileWriter to write into file
			FileWriter fileWriter = new FileWriter(outEdge);

			// Sort edge with number of start node incresing
			edges.sort(new Comparator<Edge>(){
				// Compare two edge to sort
				@Override
				public int compare(Edge o1, Edge o2) {
					// If start node indentical
					if (o1.GetStart() - o2.GetStart() == 0) {
						// Sort by end node
						return o1.GetEnd() - o2.GetEnd();
					}
					// Else, sort by start node
					return o1.GetStart() - o2.GetStart();
				}

			});

			// Variable = start node of last edge
			int previousStartNode = -1;

			// All edges
			for (Edge edge : edges) {
				System.out.println(edge.toString());

				// Get information of an edge
				int startNode = edge.GetStart();
				int endNode = edge.GetEnd();
				String labelEdge = edge.GetLabel();

				// Variable content string to write into file
				String writeLine;

				// Check if startNode of this edge is identical with last edge
				if (startNode == previousStartNode) {
					// Append to last line in file
					writeLine = " \"" + labelEdge + "\" " + "\"" + endNode + "\"";
				}
				// Start node of this edge different with start node of the previous edge
				else {
					if (previousStartNode == -1) {
						// It must be first line in file, no line brake
						writeLine = "\"" + startNode + "\" " + "\"" + labelEdge + "\" " + "\"" + endNode + "\"";
					}
					else {
						// It must be another line, add a line break in front
						writeLine = "\n\"" + startNode + "\" " + "\"" + labelEdge + "\" " + "\"" + endNode + "\"";
					}
					// Update previousStartNode after that
					previousStartNode = startNode;
				}

				// Write to the file
				fileWriter.write(writeLine);
			}

			// Close this FileWriter
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File outNode = new File(path + "/Nodes.txt");

		try {
			FileWriter fileWriter = new FileWriter(outNode);

			for (Node node : nodes) {
				String writeLine = "\"" + node.GetNodeNumber() + "\" \"" + node.GetSrcLine() + "\"\n";

				fileWriter.write(writeLine);
			}

			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("----- End write data -----");
	}
	
	public void writePng(String path){
		
		String strDOT = generateDOT();
		
		if (printDebug) System.out.println("\n***** Generated DOT Code:\n\n"+strDOT+"\n\n");
				
		File out = new File(path);
		
		GraphViz gv = new GraphViz();
		gv.writeGraphToFile(gv.getGraph(strDOT, "png"), out);
		
	}
	
	//	CURRENT FORMAT CONSTRAINTS:
	//  Must use surrounding braces for all loops and conditionals
	//  do-while, for, while loop supported
	
	private int cleanup(){
		SourceCodeMethods sourceCodeMethods = new SourceCodeMethods();
		
		//trim all lines (remove indents and other leading/trailing whitespace)
		lines = sourceCodeMethods.trimAllLines(lines);
		// printAllLine();
		
		//remove blank lines
		lines = sourceCodeMethods.removeBlankLines(lines);
		// printAllLine();
		
		//eliminate comments
		lines = sourceCodeMethods.removeComments(lines);
		// printAllLine();

		//move opening braces on their own line to the previous line
		//move any code after an opening brace to the next line
		lines = sourceCodeMethods.moveOpeningBracelets(lines);
		// printAllLine();		

		//move closing braces NOT starting a line to the next line
		//move any code after a closing brace to the next line
		lines = sourceCodeMethods.moveClosingBracelets(lines);
		// printAllLine();

		// At this point, all opening braces end a line and all closing braces are on their own line;
				
		//Separate lines with containing semicolons except at the end
		lines = sourceCodeMethods.separateStatements(lines);
		// printAllLine();

		//Combine any multi-line statements
		lines = sourceCodeMethods.combineStatements(lines);
		// printAllLine();

		//again, trim all lines (remove indents and other leading/trailing whitespace)
		lines = sourceCodeMethods.trimAllLines(lines);
		// printAllLine();

		//turn for loops into while loops
		lines = sourceCodeMethods.turnForLoopsToWhileLoops(lines);
		// printAllLine();

		//separate case statements with next line
		lines = sourceCodeMethods.separateCaseStatement(lines);
		// printAllLine();

		//again, trim all lines (remove indents and other leading/trailing whitespace)
		lines = sourceCodeMethods.trimAllLines(lines);
		printAllLine();

		return _0;
	}
	
	private void addDummyNodes(){
		
		for (int i=0; i<lines.size(); i++){
		
			String line = lines.get(i);
			
			if (line.matches("}")){
				
				//find the opening
				int j=i-1;
				int openline=-1;
				int depth=0;
				while (j>=0 & openline==-1){
					if (lines.get(j).contains("}")) depth++;
					if (lines.get(j).contains("{")){
						if (depth==0) openline = j;
						else depth--;
					}
					j--;
				}
				if (j<-1){
					System.err.println("Braces are not balanced");
					System.exit(2);
				}

				if (lines.get(i-1).equals("}")) {
					lines.add(i, "dummy_node;");
					i--; // adjust i due to insertion
				}
			}
		}
	}
	
	private void getNodes(){	
		
		if (printDebug){
			String outlines="\n***** Processed Source Code:\n\n";
			for (int i=0; i<lines.size(); i++) outlines += i+": "+lines.get(i)+"\n";
			System.out.printf("%s\n", outlines);
		}

		EdgeMethods modifyEdge = new EdgeMethods();

		for (int i=0; i<lines.size(); i++){
			
			String line = lines.get(i);
			
			//if we find a close brace, need to figure out where to go from here
			if (line.matches("}")){
				
				//find the opening
				int j=i-1;
				int openline=-1;
				int depth=0;
				while (j>=0 & openline==-1){
					if (lines.get(j).contains("}")) depth++;
					if (lines.get(j).contains("{")){
						if (depth==0) openline = j;
						else depth--;
					}
					j--;
				}
				if (openline == -1){
					System.err.println("Braces are not balanced");
					System.exit(2);
				}
				
				// For loop blocks
				if (lines.get(openline).toLowerCase().matches("^(for|while|do).*")){
					// Add all the edge for loop
					edges = modifyEdge.addEdgesForLoops(lines, edges, openline, i);
				}
				
				// For conditional (if, else, else if) blocks
				else if (lines.get(openline).toLowerCase().matches("^(if|else).*")){
					// Add all the edges for if-condition
					edges = modifyEdge.addEdgesForIfCondition(lines, edges, openline, i);
				}

				// For switch-case block
				else if (lines.get(openline).toLowerCase().matches("^switch.*")){
					// Add all the edges for switch-condition
					edges = modifyEdge.addEdgesForSwitchCondition(lines, edges, openline, i);
				}
			}
			
			// If not close bracelets
			else{
				// If this is a continue statement
				if (lines.get(i).toLowerCase().matches("^continue;")) {
					edges = modifyEdge.addEdgesForContinueStatement(lines, edges, i);
				}
				// If this is a break statement
				if (lines.get(i).toLowerCase().matches("^break;")) {
					edges = modifyEdge.addEdgesForBreakStatement(lines, edges, i);
				}
				// We'll add a node and an edge unless these are not executable lines
				if (!lines.get(i).toLowerCase().matches("^(do|else|case|default).*")){
					addNode(line, i);
					if (i>0 && !lines.get(getPrevLine(i)).toLowerCase().matches("^(do|else|case|default|continue|break).*") && !lines.get(i-1).equals("}")){
						addEdge(getPrevLine(i), i, "");
					}
				}
				// Add a node for the "else if" line
				if (lines.get(i).toLowerCase().matches("^(else if).*")) {
					addNode(line, i);
				}
			}
						
		}
		
		// remove entry edges
		edges = modifyEdge.removeEntryEdges(edges);

		// remove any duplicate edges.
		edges = modifyEdge.removeDuplicateEdges(edges);
		
		//fix any returns before the last line
		for (int i=0; i<nodes.size(); i++){
			
			if (nodes.get(i).GetSrcLine().contains("return")){
				
				//mark node as an exit node
				Node n = nodes.get(i);
				n.SetExit(true);
				nodes.set(i,n);
				
				//remove any lines coming from that node
				for (int j=0; j<edges.size(); j++){
					if (edges.get(j).GetStart() == n.GetSrcLineIdx()) edges.remove(j);
				}
				
			}
			
		}
		
		if (printDebug){
			System.out.print("\n***** Edges:\n   - numbers correspond to processed source code line numbers (above)\n   - basic block nodes not yet combined\n\n");
			for (Edge e: edges) System.out.println("("+e.GetStart()+","+e.GetEnd()+"): " + e.GetLabel());
		}
		
				
	}
	
	/**
	 * This method is used to removing all nodes contains "break", "continue", "dummy_node"
	 * It removes all edges start or end it these nodes, and create new edges connect two nodes
	 * where these nodes are intermediate node between this two nodes.
	 */
	private void removeDummyNodes() {
		// Edge start from "break", "continue" or "dummy" nodes (It has only one edge.)
		Edge startFromDummyNodes = null;
		// Edges end to "break", "continue" or "dummy" nodes (It can be several edges, so use list.)
		List<Edge> endAtDummyNodes = new ArrayList<Edge>();
		// Iterator all edges
		for (int currentline = 0; currentline < lines.size(); currentline++) {
			// Find line with "break", "continue" or "dummy_node" statement
			if (lines.get(currentline).toLowerCase().matches("^(dummy_node;|break;|continue;)")) {
				// Find all edge connect this line (arrive and leave from this node)
				for (int i = 0; i < edges.size(); i++) {
					// If edge start from this line --> 1 line
					if (edges.get(i).GetStart() == currentline) {
						// This edge leave this node
						startFromDummyNodes = edges.get(i);
						// Remove this edge from the list
						edges.remove(i);
						// Decrease i cause we delete edge i, so edge i+1 --> i
						i--;
					}
					// If edge go to this line --> 1 or more lines --> add to the list
					else if (edges.get(i).GetEnd() == currentline) {
						// Add edges go to this node
						endAtDummyNodes.add(edges.get(i));
						// Remove this edge from the list
						edges.remove(i);
						// Decrease i cause we delete edge i, so edge i+1 --> i
						i--;
					}
				}
				// List all edges end to this node of current line
				for (int i = 0; i < endAtDummyNodes.size(); i++) {
					// Add new edge with start is start of an edge in the list of all edges end to this "dummy" node
					// and with end is the end of the edge start from this "dummy" node
					edges.add(new Edge(endAtDummyNodes.get(i).GetStart(), startFromDummyNodes.GetEnd(),
						 endAtDummyNodes.get(i).GetLabel()));
				}
				// Remove "start" edge for new loop
				startFromDummyNodes = null;
				// Remove "end" edges for new loop
				endAtDummyNodes.clear();
			}
		}
		// 
		for (int i = 0; i < nodes.size(); i++) {
			// Check all node with code is "break", "continue", "dummy_node"
			if (nodes.get(i).GetSrcLine().toLowerCase().matches("^(break;|continue;|dummy_node;)")) {
				// Remove that node
				nodes.remove(i);
				// Need to decrease i because we delete node i, so node i+1 --> i in the list
				i--;
			}
		}
	}

	private void combineNodes(){
	

		//add entry edge temporarily to prevent combination of loop nodes
		addEdge(-1,0,"");
	
		//add dummy end nodes if needed
		for (Edge e: edges){
		boolean foundEnd=false;
			for (Node n: nodes){
				if (e.GetEnd() == n.GetNodeNumber()) foundEnd=true;				
			}
			if (!foundEnd){
				Node n = new Node();
				n.SetSrcLine("");
				n.SetNodeNumber(e.GetEnd());
				nodes.add(n);
			}
		}
		
		//figure out how many edges each node has (to and from the node)
		for (int i=0; i<nodes.size(); i++){
			for (Edge e: edges){
				if (e.GetStart() == nodes.get(i).GetNodeNumber()) nodes.get(i).IncEdgesFrom();
				if (e.GetEnd() == nodes.get(i).GetNodeNumber()) nodes.get(i).IncEdgesTo();
			}
		}
		
		// for any pair of consecutive nodes that have only 1 edge between, combine them
		
		for (int i=0; i<nodes.size(); i++){
			
			// if there's more than one edge (or no edges) leaving this node, we can't combine
			if (nodes.get(i).GetEdgesFrom() != 1 || nodes.get(i).GetSrcLine().contains("%forcenode%")) continue;
			
			// find the edge leaving this node
			int midEdge = 0;
			while (midEdge < edges.size() && edges.get(midEdge).GetStart() != nodes.get(i).GetNodeNumber()) midEdge++;
			int nextNode = 0;
			while (nodes.get(nextNode).GetNodeNumber() != edges.get(midEdge).GetEnd()) nextNode++;
			
			// if there's more than one edge entering the next node, we can't combine
			if (nodes.get(nextNode).GetEdgesTo() > 1 || nodes.get(nextNode).GetSrcLine().contains("%forcenode%")) continue;
			
			// if it's a self-loop we can't combine
                        if (nextNode == i) continue;	

			// If we got here we can combine the nodes
						
			//copy the sourceline (we'll delete nextNode)
			nodes.get(i).SetSrcLine(nodes.get(i).GetSrcLine()+"\n"+nodes.get(nextNode).GetSrcLine());
				
			// get all the edges leaving the next node
			List<Integer> outEdges = new ArrayList<Integer>();
			for (int j=0; j<edges.size(); j++){
				if (edges.get(j).GetStart() == nodes.get(nextNode).GetNodeNumber()) outEdges.add(j);
			}
			nodes.get(i).ClearEdgesFrom();
			if (outEdges.size() > 0){ //if false, this is the last node
				// relink the outbound edges to start at the first node
				for (int idx: outEdges){
					edges.set(idx, new Edge(nodes.get(i).GetNodeNumber(), edges.get(idx).GetEnd(), edges.get(idx).GetLabel()));
					nodes.get(i).IncEdgesFrom();
				}
			}
			
			// remove old middle edge and second node
			edges.remove(midEdge);
			nodes.remove(nextNode);
 
			//keep the current node as start until we can't combine any more
			i--;
			
		}

		//delete the temporary entry edge
		edges.remove(edges.size()-1);
		
	}

	private void numberNodes(){
		
		//save the oldedges and clear edges
		List<Edge> oldedges = new ArrayList<Edge>();
		for (Edge e: edges) oldedges.add(new Edge(e.GetStart(), e.GetEnd(), e.GetLabel()));
		edges.clear();
		
		//number the nodes and add edges with new numbers
		
		//First assign node numbers
		for (int i=0; i<nodes.size(); i++){
			Node n = nodes.get(i);
			n.SetNodeNumber(i);
			nodes.set(i, n);
		}
		
		//add edges using node_numbers instead of source line index
		for (int i=0; i<oldedges.size(); i++){
			int newStart=0;
			int newEnd=0;
			
			while (newStart < nodes.size() && nodes.get(newStart).GetSrcLineIdx() != oldedges.get(i).GetStart()) newStart++;
			while (newEnd < nodes.size() && nodes.get(newEnd).GetSrcLineIdx() != oldedges.get(i).GetEnd()) newEnd++;
			
			// NOTE: unchecked --> newStart and newEnd greater than all number_nodes
			if ((newStart < nodes.size()) && (newEnd < nodes.size())) {
				addEdge(newStart, newEnd, oldedges.get(i).GetLabel());
			} 
			//addEdge(newStart,newEnd);
		}
		
		if (printDebug) {
			System.out.print("\n***** Edges:\n   - numbers correspond to node\n\n");
			for (Edge e: edges) System.out.println("("+e.GetStart()+","+e.GetEnd()+")");
			
			System.out.print("\n***** Nodes:\n   - numbers correspond to node\n\n");
			for (Node n: nodes) System.out.println(""+n.GetNodeNumber()+": "+n.GetSrcLine()+"");
		}
	}

	private void fixNumbering(){
		
		//Renumber the nodes, and the edges accordingly
		for (int i=0; i<nodes.size(); i++){
			Node n = nodes.get(i);
			for (int j=0; j<edges.size(); j++){
				if (edges.get(j).GetStart() == n.GetNodeNumber()) edges.set(j, new Edge(i, edges.get(j).GetEnd(), edges.get(j).GetLabel()));
				if (edges.get(j).GetEnd() == n.GetNodeNumber()) edges.set(j, new Edge(edges.get(j).GetStart(), i, edges.get(j).GetLabel()));
			}
			n.SetNodeNumber(i);
			nodes.set(i, n);
		}
	
		nodes.get(0).SetEntry(true);
	
		// mark entry and exits
		for (int i=0;i<nodes.size(); i++){
			
			boolean exit = true;
			
			for (Edge e: edges){
				if (e.GetStart() == nodes.get(i).GetNodeNumber()) exit = false;
			}
			
			if (!nodes.get(i).isExit()) nodes.get(i).SetExit(exit); //make sure override stays for return nodes
			
		}
		
	}
	
	/**
	 * This method clean code in node
	 */
	private void cleanCode() {
		for (Node node: nodes) {
			// Get source line
			String srcLine = node.GetSrcLine();
			// If it has tag forcenode
			if (srcLine.contains("%forcenode%")) {
				srcLine = srcLine.replace("%forcenode%", "").trim();
			}
			// If last character is ';' or '{'
			if (srcLine.contains(";") || srcLine.contains("{")) {
				srcLine = srcLine.substring(0, srcLine.length() - 1).trim();
			}
			// Set srcLine
			node.SetSrcLine(srcLine);
		}
	}

	private String generateDOT(){
		
		String strDOT = "digraph cfg{\n";
		
		for (Node n: nodes){
			
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
		
		for (Edge e: edges){
			strDOT += "\t"+e.GetStart()+" -> "+e.GetEnd() + "[label = \"" + e.GetLabel() + "\"]";
			
			// attributes
			
			strDOT += ";\n";
		}
		
		strDOT += "}";
		
		return strDOT;		
		
	}
	
	private void addNode(String line, int lineidx){
		
		Node node = new Node(0,line,false,false);
		node.SetSrcLineIdx(lineidx);
		nodes.add(node);
	
	}

	private void addEdge(int startidx, int endidx, String labelString){
		
		edges.add(new Edge(startidx, endidx, labelString));
		
	}

	private int getPrevLine(int start){
		
		int prevEdge=start-1;
		
		while (prevEdge > -1 && lines.get(prevEdge).equals("}")) prevEdge--;
		
		return prevEdge;
		
	}
	
	/*
	private String removeTags(String line){
		
		line = line.replace("%forcenode%", "");
		line = line.replace("%forcelabel%", "");
		
		return line;
		
	}
	*/
		
	private void printAllLine() {
		System.out.println();
		System.out.println("----------");
		for (int i=0; i<lines.size(); i++) {
			System.out.println(lines.get(i));
		}
		System.out.println("----------");
		System.out.println();
	}
}