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

public class ConstructorsTest {
	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
	private List<Entity> entities;
	private List<Relation> relations;

	/**
	  * execute ENRE-CPP and get generated entities and relations before every test cases
	  */
	@Before
	public void execute() throws Exception {
		String groupName = "function";
		String caseName = "constructors";
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
	  * contains Function entity Box
	  */
	@Test
	public void containsFunctionEntityBox4() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("Box") && x.getLocation().getStartLine() == 4);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {4, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Function entity Box
	  */
	@Test
	public void containsFunctionEntityBox6() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("Box") && x.getLocation().getStartLine() == 6);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {6, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	  * contains Function entity Box
	  */
	@Test
	public void containsFunctionEntityBox10() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("Box") && x.getLocation().getStartLine() == 10);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {10, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}


}
