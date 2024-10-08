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

public class FunctionPointerTypedefTest {
	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
	private List<Entity> entities;
	private List<Relation> relations;

	/**
	  * execute ENRE-CPP and get generated entities and relations before every test cases
	  */
	@Before
	public void execute() throws Exception {
		String groupName = "typedef";
		String caseName = "function-pointer-typedef";
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
	  * contains undefined entity t_somefunc
	  */
//	@Test
//	public void containsundefinedEntityt_somefunc1() {
//		List<Entity> filteredEntities =  && x.getName().equals("t_somefunc") && x.getLocation().getStartLine() == 1);
//		Assert.assertEquals(1, filteredEntities.size());
//		Entity ent = filteredEntities.get(0);
//		int[] gt = {1, -1, -1, -1};
//		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
//	}


}
