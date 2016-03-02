import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Zhu Bingjing
 * @date 2016年2月17日 下午5:30:27
 * @version 1.0
 */
public class TextBuddyTest {
	@Before
	public void setUp() throws Exception {
		String[] args = { "mytextfile.txt" };
		TextBuddy.main(args);
		TextBuddy.doCommand("clear");
	}

	@Test
	public void testClear() throws IOException {
		assertEquals("Invalid commands!", TextBuddy.doCommand("clear sldkfj"));
		// check if the “clear” command returns the right status message
		assertEquals("All content deleted from mytextfile.txt",
				TextBuddy.doCommand("clear"));
		// check if the file was actually cleared
		assertEquals(0, TextBuddy.readFile());
	}

	@Test
	public void testAdd() throws IOException {
		assertEquals(0, TextBuddy.readFile());
		// check if the “add” command returns the right status message
		assertEquals("Added to mytextfile.txt: \"item1\"",
				TextBuddy.doCommand("add item1"));
		// check if the item was actually added into the file
		assertEquals(1, TextBuddy.readFile());
		assertEquals("Added to mytextfile.txt: \"item2\"",
				TextBuddy.doCommand("+ item2"));
		// check if the item was actually added into the file
		assertEquals(2, TextBuddy.readFile());
		assertEquals("Added to mytextfile.txt: \"item2s\"",
				TextBuddy.doCommand("+ item2s"));
		// check if the item was actually added into the file
		assertEquals(3, TextBuddy.readFile());
	}

	@Test
	public void testDisplay() throws IOException {
		// check if the “display” command returns the right status message
		assertEquals("mytextfile.txt is empty.", TextBuddy.doCommand("display"));
		testAdd();
		assertEquals("", TextBuddy.doCommand("show"));
		assertEquals("Invalid commands!", TextBuddy.doCommand("show 3"));
	}

	@Test
	public void testDelete() throws IOException {
		testAdd();
		// check if the “delete” command returns the right status message
		assertEquals("Invalid commands!", TextBuddy.doCommand("delete item1"));
		// check if the item was actually added into the file
		assertEquals(3, TextBuddy.readFile());
		assertEquals("Invalid commands!", TextBuddy.doCommand("delete sldkfj"));
		assertEquals("Deleted from mytextfile.txt: \"item2\"",
				TextBuddy.doCommand("delete 2"));
		// check if the item was actually added into the file
		assertEquals(2, TextBuddy.readFile());
	}

	@Test
	public void testSort() throws IOException {
		TextBuddy.doCommand("add aadlfkj");
		TextBuddy.doCommand("add clskdfj");
		TextBuddy.doCommand("add blskdjf");
		TextBuddy.doCommand("display");
		assertEquals("Invalid commands!", TextBuddy.doCommand("sort item1"));
		TextBuddy.doCommand("display");
		assertEquals("", TextBuddy.doCommand("sort"));
		TextBuddy.doCommand("add aaaaaa");
		TextBuddy.doCommand("display");
		assertEquals("", TextBuddy.doCommand("sort"));
	}

	@Test
	public void testSearch() throws IOException {
		TextBuddy.doCommand("add item1");
		TextBuddy.doCommand("add aitem2");
		TextBuddy.doCommand("add ite4");
		TextBuddy.doCommand("display");
		assertEquals("Search done!", TextBuddy.doCommand("search item1"));
		assertEquals("Search done!", TextBuddy.doCommand("search item"));
		assertEquals("Search done!", TextBuddy.doCommand("search it"));
		assertEquals("Search done!", TextBuddy.doCommand("search sdf"));
		assertEquals("Search done!", TextBuddy.doCommand("find it"));
		assertEquals("Search done!", TextBuddy.doCommand("find i t"));
	}

	@Test
	public void testDeleteAfterSearch() throws IOException {
		TextBuddy.doCommand("add item1");
		TextBuddy.doCommand("add aitem2");
		TextBuddy.doCommand("add ite3");
		TextBuddy.doCommand("add ite4");
		TextBuddy.doCommand("display");
		assertEquals("Deleted from mytextfile.txt: \"ite4\"",
				TextBuddy.doCommand("delete 4"));
		TextBuddy.doCommand("display");
		assertEquals("Search done!", TextBuddy.doCommand("search item"));
		assertEquals("Deleted from mytextfile.txt: \"item1\"",
				TextBuddy.doCommand("delete 1"));
		TextBuddy.doCommand("display");
		assertEquals("Index not found.", TextBuddy.doCommand("delete 3"));
	}
}
