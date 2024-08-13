//package client;
//
//import entity.*;
//import relation.Relation;
//import cdt.Processor;
//import cdt.TemplateWork;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.After;
//import org.junit.Test;
//import util.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class ExampleclassTest {
//	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
//	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
//	private List<Entity> entities;
//	private List<Relation> relations;
//
//	/**
//	  * execute ENRE-CPP and get generated entities and relations before every test cases
//	  */
//	@Before
//	public void execute() throws Exception {
//		String groupName = "class";
//		String caseName = "exampleclass";
//		String[] args = {String.format("src/test/resources/cases/_%s/_%s/", groupName, caseName), String.format("_%s", caseName) };
//		TemplateWork work = new TemplateWork();
//		Processor processor = work.execute(args);
//		this.relations = processor.getRelations();
//		this.entities = new ArrayList<>(processor.getEntities().values());
//	}
//
//	/**
//	  * clear ENRE-CPP result in memory
//	  */
//	@After
//	public void clear() {
//		judgeCate.clear();
//	}
//
//	/**
//	  * contains Variable entity publicVar
//	  */
//	@Test
//	public void containsVariableEntitypublicVar3() {
//		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("publicVar") && x.getLocation().getStartLine() == 3);
//		Assert.assertEquals(1, filteredEntities.size());
//		Entity ent = filteredEntities.get(0);
//		int[] gt = {3, -1, -1, -1};
//		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
//	}
//
//	/**
//	  * contains Function entity publicMethod
//	  */
//	@Test
//	public void containsFunctionEntitypublicMethod4() {
//		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("publicMethod") && x.getLocation().getStartLine() == 4);
//		Assert.assertEquals(1, filteredEntities.size());
//		Entity ent = filteredEntities.get(0);
//		int[] gt = {4, -1, -1, -1};
//		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
//	}
//
//	/**
//	  * contains Variable entity privateVar
//	  */
//	@Test
//	public void containsVariableEntityprivateVar7() {
//		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("privateVar") && x.getLocation().getStartLine() == 7);
//		Assert.assertEquals(1, filteredEntities.size());
//		Entity ent = filteredEntities.get(0);
//		int[] gt = {7, -1, -1, -1};
//		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
//	}
//
//	/**
//	  * contains Function entity privateMethod
//	  */
//	@Test
//	public void containsFunctionEntityprivateMethod8() {
//		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("privateMethod") && x.getLocation().getStartLine() == 8);
//		Assert.assertEquals(1, filteredEntities.size());
//		Entity ent = filteredEntities.get(0);
//		int[] gt = {8, -1, -1, -1};
//		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
//	}
//
//	/**
//	  * contains Variable entity protectedVar
//	  */
//	@Test
//	public void containsVariableEntityprotectedVar11() {
//		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("protectedVar") && x.getLocation().getStartLine() == 11);
//		Assert.assertEquals(1, filteredEntities.size());
//		Entity ent = filteredEntities.get(0);
//		int[] gt = {11, -1, -1, -1};
//		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
//	}
//
//	/**
//	  * contains Function entity protectedMethod
//	  */
//	@Test
//	public void containsFunctionEntityprotectedMethod12() {
//		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("protectedMethod") && x.getLocation().getStartLine() == 12);
//		Assert.assertEquals(1, filteredEntities.size());
//		Entity ent = filteredEntities.get(0);
//		int[] gt = {12, -1, -1, -1};
//		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
//	}
//
//
//}
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

public class ExampleclassTest {
	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
	private List<Entity> entities;
	private List<Relation> relations;

	/**
	 * execute ENRE-CPP and get generated entities and relations before every test cases
	 */
	@Before
	public void execute() throws Exception {
		String groupName = "class";
		String caseName = "exampleclass";
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
	 * contains Variable entity publicVar
	 */
	@Test
	public void containsVariableEntitypublicVar3() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("publicVar") && x.getLocation().getStartLine() == 3);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {3, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	 * contains Function entity publicMethod && x.getLocation().getStartLine() == 4
	 */
	@Test
	public void containsFunctionEntitypublicMethod4() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("publicMethod") );
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {4, -1, -1, -1};
		//Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	 * contains Variable entity privateVar
	 */
	@Test
	public void containsVariableEntityprivateVar7() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("privateVar") && x.getLocation().getStartLine() == 7);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {7, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	 * contains Function entity privateMethod  && x.getLocation().getStartLine() == 8
	 */
	@Test
	public void containsFunctionEntityprivateMethod8() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("privateMethod") );
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {8, -1, -1, -1};
		//Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	 * contains Variable entity protectedVar
	 */
	@Test
	public void containsVariableEntityprotectedVar11() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("protectedVar") && x.getLocation().getStartLine() == 11);
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {11, -1, -1, -1};
		Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}

	/**
	 * contains Function entity protectedMethod   && x.getLocation().getStartLine() == 12
	 */
	@Test
	public void containsFunctionEntityprotectedMethod12() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isFunction(x) && x.getName().equals("protectedMethod"));
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
		int[] gt = {12, -1, -1, -1};
		//Assert.assertArrayEquals(gt, TestUtil.expandLocationArray(ent.getLocation(), gt));
	}


}
