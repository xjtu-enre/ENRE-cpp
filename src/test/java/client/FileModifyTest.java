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

public class FileModifyTest {
	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
	private List<Entity> entities;
	private List<Relation> relations;

	/**
	  * execute ENRE-CPP and get generated entities and relations before every test cases
	  */
	@Before
	public void execute() throws Exception {
		String groupName = "modify";
		String caseName = "file-modify";
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
	  * only contains 1 modify relation(s)
	  */
//	@Test
//	public void onlyContains1modifyRelation() {
//		List<RelationObj> filteredRelations = TestUtil.filter(relations, (x) -> x.getRelation().getKind().equals(Configure.RELATION_MODIFY));
//		Assert.assertEquals(1, filteredRelations.size());
//	}

	/**
	  * contains Modify relation described in index 0
	  */
	@Test
	public void containsModifyRelationDescribedInIndex0() {
		List<Entity> fromEntities = TestUtil.filter(entities, (x) -> judgeCate.isFile(x) && x.getName().equals("file0.CPP"));
		if (fromEntities.size() == 1) {
			throw new RuntimeException("Insufficient or wrong predicates to determine only one [from] entity");
		}
		List<Entity> toEntities = TestUtil.filter(entities, (x) -> judgeCate.isVar(x) && x.getName().equals("i"));
		if (toEntities.size() != 1) {
			throw new RuntimeException("Insufficient or wrong predicates to determine only one [to] entity");
		}
		//List<RelationObj> filteredRelations = TestUtil.filter(relations, (x) -> x.getRelation().getKind().equals(Configure.RELATION_MODIFY) && x.getFromEntityId() == fromEntities.get(0).getId() && x.getToEntityId() == toEntities.get(0).getId() && x.getRelation().getLocation().getStartLine() == 2);
		//Assert.assertEquals(1, filteredRelations.size());
	}


}
