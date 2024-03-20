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

public class EnumeratorWithDefaultValueTest {
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
		String caseName = "enumerator-with-default-value";
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
	  * contains Enumerator entity DAY::saturday
	  */
	@Test
	public void containsEnumeratorEntityDAY__saturday2() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("saturday") && x.getLocation().getStartLine() == 2);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {2, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity DAY::sunday
	  */
	@Test
	public void containsEnumeratorEntityDAY__sunday3() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("sunday") && x.getLocation().getStartLine() == 3);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {3, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity DAY::monday
	  */
	@Test
	public void containsEnumeratorEntityDAY__monday4() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("monday") && x.getLocation().getStartLine() == 4);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {4, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity DAY::tuesday
	  */
	@Test
	public void containsEnumeratorEntityDAY__tuesday5() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("tuesday") && x.getLocation().getStartLine() == 5);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {5, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity DAY::wednesday
	  */
	@Test
	public void containsEnumeratorEntityDAY__wednesday6() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("wednesday") && x.getLocation().getStartLine() == 6);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {6, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity DAY::thursday
	  */
	@Test
	public void containsEnumeratorEntityDAY__thursday7() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("thursday") && x.getLocation().getStartLine() == 7);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {7, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Enumerator entity DAY::friday
	  */
	@Test
	public void containsEnumeratorEntityDAY__friday8() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("friday") && x.getLocation().getStartLine() == 8);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {8, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}


}
