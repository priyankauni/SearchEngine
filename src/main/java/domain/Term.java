package domain;

import java.io.File;

public class Term {
	
    private File source;
    private int noOfOccurences = 1;

    public Term(final File source) {
        this.source = source;
    }

    public void addCount() {
        this.noOfOccurences++;
    }

    public File getSource() {
        return source;
    }

    public int getNoOfOccurences() {
        return noOfOccurences;
    }

}
