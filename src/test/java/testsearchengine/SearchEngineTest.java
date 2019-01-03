package testsearchengine;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import engine.SearchEngine;


/**
 * Unit test for SearchEngine.
 */

public class SearchEngineTest {

	public static final String TEST_DOCS = "src/main/resources/documents";
	private List<File> files;
	private SearchEngine in;
	
	@Before
	public void setUp() throws IOException {
		files = Files.walk(Paths.get(TEST_DOCS)).filter(it -> Files.isRegularFile(it)).map(it -> it.toFile())
				.collect(Collectors.toList());
		 in = new SearchEngine(files);
		
	}

	@Test
	public void searchForBrown() {
		
		List<String> searchResult = in.search("lazy");
	    assertEquals("document2.txt", searchResult.get(0).toString());
	    assertEquals("document3.txt", searchResult.get(1).toString());
	}
	
	@Test
	public void searchForFox() {
		
		List<String> searchResult = in.search("fox");
	    assertEquals("document1.txt", searchResult.get(0).toString());
	    assertEquals("document3.txt", searchResult.get(1).toString());
	}

}
