package engine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import domain.Term;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchEngine {

	private Multimap<String, Term> indexMap = ArrayListMultimap.create();
	List<File> documents;

	public SearchEngine(List<File> files) {
		if (null == files) {
			throw new IllegalArgumentException("Null passed as an argument for structure building. ");
		}
		documents = new ArrayList<>(files);
		files.forEach(this::invertedIndexMap);
	}

	public SearchEngine(File file) {
		invertedIndexMap(file);
	}

	/**
	 * Return the list of documents (filenames) where given term occurs sorted by
	 * tf-idf
	 *
	 * @param query Word to look for in the index structure
	 * @return List of files where given query was found (just the filenames)
	 */
	public List<String> search(String query) {
		if (null == query) {
			throw new IllegalArgumentException("Not possible to execute query-based search with empty query. ");
		}

		final List<Term> terms = findPaths(query);
		sort(query, terms);
		return getFilenames(terms);
	}

	private void invertedIndexMap(File file) {
		try (Scanner scanner = new Scanner(file)) {
			scanner.useDelimiter(" +");
			while (scanner.hasNext()) {
				String word = scanner.next();
				if (indexMap.containsKey(word) && wordComesFromFile(word, file)) {
					addCountInMapForWord(word, file);
				} else {
					indexMap.put(word, new Term(file));
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error during opening file: " + e.getMessage());
		}
	}

	private boolean wordComesFromFile(final String word, final File file) {
		return indexMap.get(word).stream().anyMatch(term -> term.getSource() == file);
	}

	private void addCountInMapForWord(final String word, final File file) {
		indexMap.get(word).stream().filter(it -> it.getSource().equals(file)).findFirst().ifPresent(Term::addCount);
	}

	private List<Term> findPaths(String query) {
		return (List<Term>) indexMap.get(query);
	}

	/**
	 * Sort a list of documents by TF-IDF
	 * 
	 * @param query
	 * @param terms
	 */
	private void sort(String query, final List<Term> terms) {

		Collections.sort(terms, new Comparator<Term>() {
			@Override
			public int compare(Term term1, Term term2) {
				double tfidf1 = tfidf(query, term1);
				double tfidf2 = tfidf(query, term2);
				return Double.compare(tfidf1, tfidf2);
			}
		});
	}

	/**
	 * Calculate the TF-IDF statistic for a word in a document
	 * 
	 * @param query 
	 * @param term
	 * @return TF-IDF statistic as a double
	 */
	private double tfidf(String query, Term term) {

		double tf = (double) term.getNoOfOccurences() / getDocumentLength(term);
		double idf = Math.log10((double) documents.size() / (1 + indexMap.get(query).size()));

		return tf * idf;
	}

	/**
	 * Tokenize a document into tokens/words
	 * 
	 * @param data Data in the document
	 * @return String array of tokens/words
	 */
	private String[] tokenize(String data) {
		String[] words = data.split("\\P{L}+");
		return words;
	}
	
	/**
	 * Get the total number of terms in the document
	 * @param term
	 * @return Length of the document
	 */

	private int getDocumentLength(Term term) {
		Stream<String> lines = null;
		try {
			lines = Files.lines(term.getSource().toPath());
		} catch (IOException e) {
			System.err.println("Error during opening file: " + e.getMessage());
		}
		String data = lines.collect(Collectors.joining("\n"));
		lines.close();
		return tokenize(data).length;
	}

	private List<String> getFilenames(final List<Term> paths) {
		return paths.stream().map(Term::getSource).map(File::getName).collect(Collectors.toList());
	}

}
