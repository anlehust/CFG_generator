package Generator_CFG;

import java.util.*;

/**
 * The class clean the code input from file
 */

public class SourceCodeMethods {
    
    /**
     * 
     * @param lines
     * @return
     */
    public List<String> trimAllLines(List<String> lines) {
        // All the lines
        for (int i=0; i<lines.size(); i++){
			// Trim the line (remove all whitespaces in two heads of the line)
            lines.set(i, lines.get(i).trim());
		}
        // Return results
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> removeBlankLines(List<String> lines) {
        // Remove all the blank lines in source code
        lines.removeAll(Collections.singleton(""));
        // Return results
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> removeComments(List<String> lines) {
        // Remove all comment
        // NEED IMPROVE: Remove comment with '/*'
        for (int i=0; i<lines.size(); i++){
			int idx = lines.get(i).indexOf("//"); 
			if ( idx >= 0){
				lines.set(i, lines.get(i).substring(0,idx)); 
			}
		}
        // Return results
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> moveOpeningBracelets(List<String> lines) {
        // Move opening braces on their own line to the previous line
		for (int i=lines.size()-1; i>=0; i--){
			if (lines.get(i).equals("{")){
				lines.set(i-1, lines.get(i-1) + "{");
				lines.remove(i);
			}			
		}
        // Move any code after an opening brace to the next line
		for (int i=0; i<lines.size(); i++){
			int idx = lines.get(i).indexOf("{");
			if (idx > -1 && idx < lines.get(i).length()-1 && lines.get(i).length() > idx-1){ //this means there is text after the {
				lines.add(i+1,lines.get(i).substring(idx+1)); //insert the text right of the { as the next line
				lines.set(i,lines.get(i).substring(0,idx+1)); //remove the text right of the { on the current line
			}
		}
        // Return result
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> moveClosingBracelets(List<String> lines) {
        // Move closing braces NOT starting a line to the next line
		for (int i=0; i<lines.size(); i++){
			int idx = lines.get(i).indexOf("}"); 
			if (idx > 1){ //this means the } is not starting a line
				lines.add(i+1,lines.get(i).substring(idx)); //insert the text starting with the } as the next line
				lines.set(i,lines.get(i).substring(0,idx)); //remove the text starting with the } on the current line
			}
		}
        // Move any code after a closing brace to the next line
		for (int i=0; i<lines.size(); i++){
			int idx = lines.get(i).indexOf("}"); 
			if (idx > -1 && lines.get(i).length() > 1){ //this means there is text after the {
				lines.add(i+1,lines.get(i).substring(1)); //insert the text right of the { as the next line
				lines.set(i,lines.get(i).substring(0,1)); //remove the text right of the { on the current line
			}
		}
        // Return result
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> separateStatements(List<String> lines) {
        // Separate lines with containing semicolons except at the end
		for (int i=0; i<lines.size(); i++){
			List<String> spl = new ArrayList<String>(Arrays.asList(lines.get(i).split(";")));
						
			if (spl.size() > 1){
								
				boolean lastsc = false;
				if (lines.get(i).matches("^.*;$")) lastsc = true;
				lines.set(i,spl.get(0)+";");
				for (int j=1; j<spl.size(); j++){
					if (j<spl.size()-1) lines.add(i+j,spl.get(j)+";");
					else lines.add(i+j,spl.get(j)+(lastsc?";":""));
				}
			}
		}
        // Return result
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> combineStatements(List<String> lines) {
        // Combine any multi-line statements
        int i=0;
		while (i<lines.size()){
			while (!lines.get(i).contains(";") && !lines.get(i).contains("{") && !lines.get(i).contains("}")){
				lines.set(i, lines.get(i) + lines.get(i+1));
				lines.remove(i+1);
			}
			i++;
		}
        // Return result
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> turnForLoopsToWhileLoops(List<String> lines) {
        // Turn for loops to while loops
        for (int i=0; i<lines.size(); i++){
			if (lines.get(i).matches("^for.+$")){
				
				//find the closing
				int j=i+3;
				int closeline =-1;
				int depth=0;
				while (j<lines.size() && closeline==-1){
					if (lines.get(j).contains("{")) depth++;
					if (lines.get(j).contains("}")){
						if (depth==0) closeline = j;
						else depth--;
					}
					j++;
				}
				if (closeline==-1){
					System.err.println("Braces are not balanced");
					System.exit(2);
				}
				
				int idx = lines.get(i).indexOf("(");
				lines.add(i, "%forcenode%" + lines.get(i).substring(idx+1)); //move the initialization before the loop
				i++; //adjust for insertion
				idx = lines.get(i+2).indexOf(")");
				lines.add(closeline+1, "%forcenode%" + lines.get(i+2).substring(0, idx) + ";"); //move the iterator to just before the close
				lines.remove(i+2);
				lines.set(i, "while ("+lines.get(i+1).substring(0, lines.get(i+1).length()-1).trim()+"){");
				lines.remove(i+1);			
			}
		}
        // Return result
        return lines;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public List<String> separateCaseStatement(List<String> lines) {
        // Separate case statement with next line
        // NOTE: this method is unchecked
        for (int i=0; i<lines.size(); i++){
			if (lines.get(i).matches("[case|default].*:.*")){
				int idx = lines.get(i).indexOf(":");
				if (idx < lines.get(i).length()-1){
					lines.add(i+1, lines.get(i).substring(idx+1));
					lines.set(i, lines.get(i).substring(0, idx+1));
				}
			}	
		}
        // Return result
        return lines;
    }
}
