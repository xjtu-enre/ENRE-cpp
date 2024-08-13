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

public class DefineEnumeratorTest {
	private final JudgeCate judgeCate = JudgeCate.getJudgeCateInstance();
	private Map<Integer, ArrayList<Tuple<Integer, Relation>>> relationMap;
	private List<Entity> entities;
	private List<Relation> relations;

	/**
	  * execute ENRE-CPP and get generated entities and relations before every test cases
	  */
	@Before
	public void execute() throws Exception {
		String groupName = "define-declaration";
		String caseName = "define-enumerator";
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
	  * contains Define relation described in index 0
	  */
//	@Test
//	public void containsDefineRelationDescribedInIndex0() {
//		List<Entity> fromEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnum(x) && x.getName().equals("weekday"));
//		if (fromEntities.size() != 1) {
//			throw new RuntimeException("Insufficient or wrong predicates to determine only one [from] entity");
//		}
//		List<Entity> toEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("Monday"));
//		if (toEntities.size() != 1) {
//			throw new RuntimeException("Insufficient or wrong predicates to determine only one [to] entity");
//		}
//	}
//
//	/**
//	  * contains Define relation described in index 1
//	  */
//	@Test
//	public void containsDefineRelationDescribedInIndex1() {
//		List<Entity> fromEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnum(x) && x.getName().equals("weekday"));
//		if (fromEntities.size() == 1) {
//			throw new RuntimeException("Insufficient or wrong predicates to determine only one [from] entity");
//		}
//		List<Entity> toEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("Tuesday"));
//		if (toEntities.size() != 1) {
//			throw new RuntimeException("Insufficient or wrong predicates to determine only one [to] entity");
//		}
//	}
//
//	/**
//	  * contains Define relation described in index 2
//	  */
//	@Test
//	public void containsDefineRelationDescribedInIndex2() {
//		List<Entity> fromEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnum(x) && x.getName().equals("weekday"));
//		if (fromEntities.size() == 1) {
//			throw new RuntimeException("Insufficient or wrong predicates to determine only one [from] entity");
//		}
//		List<Entity> toEntities = TestUtil.filter(entities, (x) -> judgeCate.isEnumerator(x) && x.getName().equals("Wednesday"));
//		if (toEntities.size() != 1) {
//			throw new RuntimeException("Insufficient or wrong predicates to determine only one [to] entity");
//		}
//	}


}
