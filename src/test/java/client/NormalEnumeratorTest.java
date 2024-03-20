package client;

import entity.*;
import relation.Relation;
import cdt.Processor;
import cdt.TemplateWork;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NormalEnumeratorTest {
	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
	private List<Entity> entities;
	private List<Relation> relations;

	/**
	  * execute ENRE-CPP and get generated entities and relations before every test cases
	  */
	@Before
	public void execute() throws Exception {
		String groupName = "enumerator";
		String caseName = "normal-enumerator";
		String[] args = {String.format("src/test/resources/cases/_%s/_%s/", groupName, caseName), String.format("_%s", caseName) };
		TemplateWork work = new TemplateWork();
		Processor processor = work.execute(args);
		this.relations = processor.getRelations();
		this.entities = new ArrayList<>(processor.getEntities().values());
	}

	/**
	  * clear ENRE-CPP result in memory
	  */
	@After
	public void clear() {
		judgeCate.clear();
	}

	/**
	  * contains Enumerator entity Color::red
	  */
	@Test
	public void containsEnumeratorEntityColor__red2() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("red") && x.getLocation().getStartLine() == 2);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {2, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity Color::green
	  */
	@Test
	public void containsEnumeratorEntityColor__green3() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("green") && x.getLocation().getStartLine() == 3);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {3, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity Color::blue
	  */
	@Test
	public void containsEnumeratorEntityColor__blue4() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("blue") && x.getLocation().getStartLine() == 4);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {4, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}


}
