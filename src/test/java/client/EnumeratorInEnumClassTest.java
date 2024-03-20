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

public class EnumeratorInEnumClassTest {
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
		String caseName = "enumerator-in-enum-class";
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
	  * contains Enumerator entity Diamonds
	  */
	@Test
	public void containsEnumeratorEntityDiamonds2() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("Diamonds"));
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
	}

	/**
	  * contains Enumerator entity Hearts
	  */
	@Test
	public void containsEnumeratorEntityHearts2() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("Hearts"));
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
	}

	/**
	  * contains Enumerator entity Clubs
	  */
	@Test
	public void containsEnumeratorEntityClubs2() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("Clubs"));
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
	}

	/**
	  * contains Enumerator entity Spades
	  */
	@Test
	public void containsEnumeratorEntitySpades2() {
		List<Entity> filteredEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("Spades"));
		Assert.assertEquals(1, filteredEntities.size());
		Entity ent = filteredEntities.get(0);
	}


}
