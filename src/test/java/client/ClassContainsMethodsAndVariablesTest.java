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

public class ClassContainsMethodsAndVariablesTest {
	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
	private List<Entity> entities;
	private List<Relation> relations;

	/**
	  * execute ENRE-CPP and get generated entities and relations before every test cases
	  */
	@Before
	public void execute() throws Exception {
		String groupName = "contains";
		String caseName = "class-contains-methods-and-variables";
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
	  * contains undefined relation described in index 0
	  */
	@Test
	public void containsundefinedRelationDescribedInIndex0() {
		List<Entity> fromEntities = TestUtil.filter(entities, (x) -> judgeCate.isClass(x) && x.getName().equals("Animal"));
		if (fromEntities.size() != 1) {
			throw new RuntimeException("Insufficient or wrong predicates to determine only one [from] entity");
		}
//		List<Entity> toEntities = TestUtil.filter(entities, (x) -> judgeCate.undefined(x) && x.getName().equals("speak"));
//		if (toEntities.size() != 1) {
//			throw new RuntimeException("Insufficient or wrong predicates to determine only one [to] entity");
//		}
//		List<RelationObj> filteredRelations = TestUtil.filter(relations, (x) -> x.getRelation().getKind().equals(Configure.undefined) && x.getFromEntityId() == fromEntities.get(0).getId() && x.getToEntityId() == toEntities.get(0).getId() && x.getRelation().getLocation().getStartLine() == 2);
//		Assert.assertEquals(1, filteredRelations.size());
	}

	/**
	  * contains undefined relation described in index 1
	  */
	@Test
	public void containsundefinedRelationDescribedInIndex1() {
		List<Entity> fromEntities = TestUtil.filter(entities, (x) -> judgeCate.isClass(x) && x.getName().equals("Animal"));
		if (fromEntities.size() != 1) {
			throw new RuntimeException("Insufficient or wrong predicates to determine only one [from] entity");
		}
		List<Entity> toEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("age"));
		if (toEntities.size() != 1) {
			throw new RuntimeException("Insufficient or wrong predicates to determine only one [to] entity");
		}
//		List<RelationObj> filteredRelations = TestUtil.filter(relations, (x) -> x.getRelation().getKind().equals(Configure.undefined) && x.getFromEntityId() == fromEntities.get(0).getId() && x.getToEntityId() == toEntities.get(0).getId() && x.getRelation().getLocation().getStartLine() == 3);
//		Assert.assertEquals(1, filteredRelations.size());
	}


}
