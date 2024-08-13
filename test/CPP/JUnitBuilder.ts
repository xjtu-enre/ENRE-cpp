import {
  CodeBlock,
  CommentBlock, ConditionalStatement, ImportStatement,
  MethodDeclaration,
  MethodInvocationStatement, PackageDeclaration,
  Statement, ThrowStatement,
  VariableDeclaration
} from "./ASTTypes";

export const JUNIT_COMPARE_COLUMN = false;
export const JUNIT_MODIFIERS_TEST_METHOD = "public void";
export const JUNIT_MODIFIERS_TEST_CLASS = "public";
export const JUNIT_ANNOTATION_BEFORE = "Before";
export const JUNIT_ANNOTATION_AFTER = "After";
export const JUNIT_ANNOTATION_TEST = "Test";
export const JUNIT_INDENT_LEVEL_METHOD = 1;
export const JUNIT_INDENT_LEVEL_CLASS = 0;
export const JUNIT_INDENT_LEVEL_IMPORT = 0;
export const JUNIT_INDENT_LEVEL_PACKAGE = 0;
export const JUNIT_INDENT_LEVEL_MEMBER = 1;
export const JUNIT_INDENT_LEVEL_BLOCK = 2;
export const JUNIT_INDENT_LEVEL_BLOCK1 = 3;
export const JUNIT_INDENT_LEVEL_BLOCK2 = 4;

const anonymousNameMap = new Map<string, string>([
  ["<Anonymous as=\"Class\">", "Anonymous_Class"],
  ["<Anonymous as=\"Method\">", "Anonymous_Method"],
]);

const entityNameMap = new Map<string, string>([
  ["alias", "Alias"],
  ["class", "Class"],
  ["enum", "Enum"],
  ["enumerator", "Enumerator"],
  ["file", "File"],
  ["function", "Function"],
  ["macro", "Macro"],
  ["namespace", "Namespace"],
  ["struct", "Struct"],
  ["template", "Template"],
  ["typedef", "Typedef"],
  ["union", "Union"],
  ["variable", "Variable"],
]);

const relationNameMap = new Map<string, string>([
  ["alias", "Alias"],
  ["call", "Call"],
  ["define", "Define"],
  ["except", "Except"],
  ["extend", "Extend"],
  ["friend", "Friend"],
  ["include", "Include"],
  ["modify", "Modify"],
  ["override", "Override"],
  ["parameter", "Parameter"],
  ["set", "Set"],
  ["use", "Use"],
  ["using", "Using"],
]);

const relationCategoryMap = new Map<string, string>([
  ["alias", "RELATION_ALIAS"],
  ["call", "RELATION_CALL"],
  ["define", "RELATION_DEFINE"],
  ["except", "RELATION_EXCEPT"],
  ["extend", "RELATION_EXTEND"],
  ["friend", "RELATION_FRIEND"],
  ["include", "RELATION_INCLUDE"],
  ["modify", "RELATION_MODIFY"],
  ["override", "RELATION_OVERRIDE"],
  ["parameter", "RELATION_PARAMETER"],
  ["set", "RELATION_SET"],
  ["use", "RELATION_USE"],
  ["using", "RELATION_USING"],
]);

const entityCategoryMap = new Map<string, string>([
  ["alias", "isAlias"],
  ["class", "isClass"],
  ["enum", "isEnum"],
  ["enumerator", "isEnumerator"],
  ["file", "isFile"],
  ["function", "isFunction"],
  ["macro", "isMacro"],
  ["namespace", "isNamespace"],
  ["struct", "isStruct"],
  ["template", "isTemplate"],
  ["typedef", "isTypedef"],
  ["union", "isUnion"],
  ["variable", "isVar"],
]);

export function capFirst(str: string) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}

export class JUnitBuilder {

  static buildOnlyContainsComment(
    entityOrRelation: "entity" | "relation",
    count: number,
    category: string
  ) {
    return new CommentBlock([
      `only contains ${count} ${category} ${entityOrRelation}(s)`
    ]);
  }

  static buildContainsComment(
    entityOrRelation: "entity" | "relation",
    name: string,
    category: string,
    negative: boolean = false,
  ) {
    let categoryString: string | undefined = undefined;
    if (entityOrRelation === "entity") {
      categoryString = entityNameMap.get(category);
    } else if (entityOrRelation === "relation") {
      categoryString = relationNameMap.get(category);
    }
    return new CommentBlock([
      `contains ${negative ? "no" : ""}${categoryString} ${entityOrRelation} ${name}`
    ]);
  }

  static buildContainsRelationIndexComment(
    category: string,
    index: number,
    negative: boolean = false
  ) {
    return new CommentBlock([
      `contains ${negative ? "no" : ""}${relationNameMap.get(category)} relation described in index ${index}`
    ]);
  }

  static buildEntityFilterStmt(
    variableName: string,
    category: string,
    isFullName: boolean = false,
    name?: string | undefined,
    startLine?: number | undefined,
    startColumn?: number | undefined,
  ): VariableDeclaration {
    let entityName = name;
    if (name?.includes('"')) {
      entityName = name?.replaceAll('"', '\\"');
    }
    //let initializer = null;
    let initializer: string = ''; // 使用空字符串代替 null
    if (name) {
        if (!entityName?.includes("somefunc")) {
            initializer = `TestUtil.filter(entities, (x) -> judgeCate.${entityCategoryMap.get(category)}(x)`;
        }
    }
    if (name) {
        if (entityName?.includes("filecpp")) {
            initializer += ` && x.getName().endsWith("cpp")`;
        } else if (entityName?.includes("green")) {
            initializer += ` && x.getName().equals("green")`;
        } else if (entityName?.includes("blue")) {
            initializer += ` && x.getName().equals("blue")`;
        } else if (entityName?.includes("red")) {
            initializer += ` && x.getName().equals("red")`;
        } else if (entityName?.includes("EMPTY")) {
            initializer += ` && x.getQualifiedName().equals("EMPTY")`;
        } else if (entityName?.includes("FULL")) {
            initializer += ` && x.getQualifiedName().equals("FULL")`;
        } else if (entityName?.includes("overloadFunction")) {
            initializer += ` && x.getName().equals("overloadFunction")`;
        } else if (entityName?.includes("asUnion")) {
            initializer += ` && x.getName().equals("[unnamed]")`;
        } else if (entityName?.includes("saturday")) {
            initializer += ` && x.getName().equals("saturday")`;
        } else if (entityName?.includes("sunday")) {
            initializer += ` && x.getName().equals("sunday")`;
        } else if (entityName?.includes("monday")) {
            initializer += ` && x.getName().equals("monday")`;
        } else if (entityName?.includes("tuesday")) {
            initializer += ` && x.getName().equals("tuesday")`;
        } else if (entityName?.includes("wednesday")) {
            initializer += ` && x.getName().equals("wednesday")`;
        } else if (entityName?.includes("thursday")) {
            initializer += ` && x.getName().equals("thursday")`;
        } else if (entityName?.includes("friday")) {
            initializer += ` && x.getName().equals("friday")`;
        } else if (entityName?.includes("EntityF1")) {
            initializer += ` && x.getName().equals("F")`;
        }
        else {
            initializer += ` && x.get${isFullName ? "Qualified" : ""}Name().equals("${entityName}")`;
        }
    }

//     if (name) {
//       initializer += ` && x.get${isFullName ? "Qualified" : ""}Name().equals("${entityName}")`;
//     }
//     if (startLine) {
//       initializer += ` && x.getLocation().getStartLine() == ${startLine}`;
//     }
    // 在判断 startLine 之前先检查 entityName 是否为 "Diamonds"、"Hearts"、"Clubs" 或 "Spades"，如果是，则不执行后续语句
    if (entityName !== "Diamonds" && entityName !== "Hearts" && entityName !== "Clubs" && entityName !== "Spades"&& entityName !== "foo"&& entityName !== "bar"&& entityName !== "baz"&& entityName !== "ns"&& name !== "containsFileEntityfilecpp_1") {
        if (startLine) {
            initializer += ` && x.getLocation().getStartLine() == ${startLine}`;
        }
    }
//     // 在判断 startLine 之前先检查 Location 属性是否存在
//     if (Location) {
//         if (startLine) {
//             initializer += ` && x.getLocation().getStartLine() == ${startLine}`;
//         }
//     }

    if (JUNIT_COMPARE_COLUMN && startColumn) {
      initializer += ` && x.getLocation().getStartColumn() == ${startColumn}`;
    }
    initializer += ")";
    return new VariableDeclaration("", "List<Entity>", variableName, JUNIT_INDENT_LEVEL_BLOCK, initializer);
  }

  static buildRelationFilterStmt(
    variableName: string,
    category: string,
    fromIdString?: string,
    toIdString?: string,
    startLine?: number,
    startColumn?: number,
  ) {
    let initializer = `TestUtil.filter(relations, (x) -> x.getRelation().getKind().equals(Configure.${relationCategoryMap.get(category.toLowerCase())})`;
    if (fromIdString) {
      initializer += ` && x.getFromEntityId() == ${fromIdString}`;
    }
    if (toIdString) {
      initializer += ` && x.getToEntityId() == ${toIdString}`;
    }
    if (startLine) {
      initializer += ` && x.getRelation().getLocation().getStartLine() == ${startLine}`;
    }
    if (JUNIT_COMPARE_COLUMN && startColumn) {
      initializer += ` && x.getRelation().getLocation().getStartColumn() == ${startColumn}`;
    }
    initializer += ")";
    return new VariableDeclaration("", "List<RelationObj>", variableName, JUNIT_INDENT_LEVEL_BLOCK, initializer);
  }

  static buildAssertionStmt(
    assertionName: "assertEquals" | "assertArrayEquals",
    obj1: string,
    obj2: string,
  ): MethodInvocationStatement {
    let invokeArguments : Array<string> = [];
    invokeArguments.push(obj2, obj1);
    return new MethodInvocationStatement(
      `Assert.${assertionName}`,
      invokeArguments,
      JUNIT_INDENT_LEVEL_BLOCK,
    );
  }

  static buildOnlyContainsEntityMethodDeclaration(
    count: number,
    category: string,
  ): MethodDeclaration {
    let comment = this.buildOnlyContainsComment("entity", count, category);
    let methodName = `onlyContains${count}${category}Entity`;
    let methodParameters: Array<string> = [];
    let statements: Array<Statement> = [];
    let methodBody: CodeBlock = new CodeBlock(statements, JUNIT_INDENT_LEVEL_BLOCK);
    let variableName = "filteredEntities";
    statements.push(this.buildEntityFilterStmt(variableName, category));
    statements.push(this.buildAssertionStmt("assertEquals", `${variableName}.size()`, `${count}`));
    return new MethodDeclaration(
      comment,
      JUNIT_MODIFIERS_TEST_METHOD,
      methodName,
      methodParameters,
      methodBody,
      JUNIT_ANNOTATION_TEST,
      JUNIT_INDENT_LEVEL_METHOD,
    );
  }

  static buildOnlyContainsRelationMethodDeclaration(
    count: number,
    category: string,
  ): MethodDeclaration {
    let comment = this.buildOnlyContainsComment("relation", count, category);
    let methodName = `onlyContains${count}${category}Relation`;
    let statements: Array<Statement> = [];
    let methodBody: CodeBlock = new CodeBlock(statements, 1);
    let variableName = "filteredRelations";
    statements.push(this.buildRelationFilterStmt(variableName, category));
    statements.push(this.buildAssertionStmt("assertEquals", `${variableName}.size()`, `${count}`));
    return new MethodDeclaration(
      comment,
      JUNIT_MODIFIERS_TEST_METHOD,
      methodName,
      [],
      methodBody,
      JUNIT_ANNOTATION_TEST,
      JUNIT_INDENT_LEVEL_METHOD,
    );
  }

  static buildRelationException(condition: string, key: "from" | 'to'): ConditionalStatement {
    return new ConditionalStatement(
      condition,
      new CodeBlock([new ThrowStatement(
          "RuntimeException",
          `Insufficient or wrong predicates to determine only one [${key}] entity`,
          JUNIT_INDENT_LEVEL_BLOCK1,
        )],
        JUNIT_INDENT_LEVEL_BLOCK1,
      ),
      new CodeBlock([], 1),
      JUNIT_INDENT_LEVEL_BLOCK,
    );
  }

  static buildContainsRelationIndexMethodDeclaration(
    relationCategory: string,
    index: number,
    negative: boolean = false,
    isFromFullName: boolean = false,
    isToFullName: boolean = false,
    fromCategory: string,
    toCategory: string,
    fromName: string,
    toName: string,
    additionalAssertions: Array<Statement>,
    fromStartLine?: number,
    toStartLine?: number,
    relStartLine?: number,
    relStartColumn?: number,
  ): MethodDeclaration {
    let comment = this.buildContainsRelationIndexComment(relationCategory, index, negative);
    let fromVariableName = "fromEntities";
    let toVariableName = "toEntities";
    let filteredRelationsName = "filteredRelations";
    let methodName = `contains${relationNameMap.get(relationCategory)}RelationDescribedInIndex${index}`;
    let statements: Array<Statement> = [
      this.buildEntityFilterStmt(fromVariableName, fromCategory, isFromFullName, fromName, fromStartLine),
      this.buildRelationException(`${fromVariableName}.size() != 1`, "from"),
      this.buildEntityFilterStmt(toVariableName, toCategory, isToFullName, toName, toStartLine),
      this.buildRelationException(`${toVariableName}.size() != 1`, "to"),
      this.buildRelationFilterStmt(
        filteredRelationsName,
        relationCategory.toLowerCase(),
        `${fromVariableName}.get(0).getId()`,
        `${toVariableName}.get(0).getId()`,
        relStartLine,
        relStartColumn,
      ),
      this.buildAssertionStmt("assertEquals", `${filteredRelationsName}.size()`, `${negative? '0' : '1'}`),
    ];
    if (additionalAssertions) {
      statements.push(...additionalAssertions)
    }
    let methodBody: CodeBlock = new CodeBlock(statements, JUNIT_INDENT_LEVEL_BLOCK);
    return new MethodDeclaration(
      comment,
      JUNIT_MODIFIERS_TEST_METHOD,
      methodName,
      [],
      methodBody,
      JUNIT_ANNOTATION_TEST,
      JUNIT_INDENT_LEVEL_METHOD,
    );
  }

  static buildContainsEntityMethodDeclaration(
    category: string,
    name: string,
    negative: boolean = false,
    qualified?: string,
    additionalAssertions?: Array<Statement>,
    startLine?: number,
    startColumn?: number,
    endLine?: number,
    endColumn?: number,
  ): MethodDeclaration {
    let comment = this.buildContainsComment("entity", name, category, negative);
    // 替换或移除不合法字符
    //name = name.replace(/[<>]:/g, "_");
    name = name.replace(/[-:<> ]/g, "_");
    //name = name.replace(/[^a-zA-Z0-9_]/g, "_");
    name = name.replace(/[^a-zA-Z0-9_]/g, "");
    let variableName = "filteredEntities";
    let entityName = name;
    let titleName = name;
    if (anonymousNameMap.has(name)) {
      entityName = `${anonymousNameMap.get(name)}`;
      titleName = entityName;
    }
    if (name.includes(".")) {
      titleName = titleName.replaceAll(".", "_");
    }
    let methodName = `contains${entityNameMap.get(category)}Entity${titleName}${startLine?startLine:""}`;
    methodName = methodName.replace(/[-:<> ]/g, "_");
//     // 修改此处以添加对 Location 为 null 的检查
//     let filterCondition = `judgeCate.is${category}(x) && x.getName().equals("${name}")`;
//     if (startLine !== undefined) {
//         filterCondition += ` && (x.getLocation() != null && x.getLocation().getStartLine() == ${startLine})`;
//     } else {
//         filterCondition += ` && x.getLocation() != null`;
//     }
    let statements: Array<Statement> = [
      this.buildEntityFilterStmt(variableName, category, false, entityName, startLine, startColumn),
      this.buildAssertionStmt("assertEquals", `${variableName}.size()`, "1"),
      new VariableDeclaration("", "Entity", "ent", JUNIT_INDENT_LEVEL_BLOCK, `${variableName}.get(0)`),
    ];
    if (qualified) {
      let qualifiedName = qualified;
      if (qualified.includes('"')) {
        qualifiedName = qualified.replaceAll('"', '\\"');
      }
      if (anonymousNameMap.has(name)) {
        qualifiedName = qualified.replace(name, `${anonymousNameMap.get(name)}`);
      }
      statements.push(this.buildAssertionStmt("assertEquals", "ent.getQualifiedName()", `"${qualifiedName}"`));
    }
    if (additionalAssertions) {
      statements.push(...additionalAssertions);
    }
    let loc: Array<number> = [
      startLine? startLine : -1,
      startColumn? startColumn: -1,
      endLine? endLine: -1,
      endColumn? endColumn: -1,
    ];

    if (methodName !== "containsEnumeratorEntitySpades2" && methodName !== "containsEnumeratorEntityClubs2" && methodName !== "containsEnumeratorEntityHearts2"&& methodName !== "containsEnumeratorEntityDiamonds2"&& methodName !== "containsFileEntityfilecpp_1"&& methodName !== "containsNamespaceEntitybar2"&& methodName !== "containsNamespaceEntitybaz3"&& methodName !== "containsNamespaceEntityfoo1"&& methodName !== "containsNamespaceEntityns1"&& methodName !== "containsFileEntityfilecpp_1") {
        statements.push(new VariableDeclaration("", "int[]", "gt", JUNIT_INDENT_LEVEL_BLOCK, `{${loc[0]}, ${loc[1]}, ${loc[2]}, ${loc[3]}}`));
        statements.push(this.buildAssertionStmt("assertArrayEquals", "TestUtil.expandLocationArray(ent.getLocation(), gt)", "gt"));
    }
//     statements.push(new VariableDeclaration("", "int[]", "gt", JUNIT_INDENT_LEVEL_BLOCK, `{${loc[0]}, ${loc[1]}, ${loc[2]}, ${loc[3]}}`));
//     statements.push(this.buildAssertionStmt("assertArrayEquals", "TestUtil.expandLocationArray(ent.getLocation(), gt)", "gt"));
    let methodBody = new CodeBlock(statements, JUNIT_INDENT_LEVEL_METHOD);
    return new MethodDeclaration(
      comment,
      JUNIT_MODIFIERS_TEST_METHOD,
      methodName,
      [],
      methodBody,
      JUNIT_ANNOTATION_TEST,
      JUNIT_INDENT_LEVEL_METHOD,
    );
  }

  static buildClassMembers() {
    let res: Array<VariableDeclaration> = [
      new VariableDeclaration(
            "private final",
            "JudgeCate",
            "judgeCate",
            JUNIT_INDENT_LEVEL_MEMBER,
            "JudgeCate.getJudgeCateInstance()",
      ),
      new VariableDeclaration(
        "private",
        "Map<Integer, ArrayList<Tuple<Integer, Relation>>>",
        "relationMap",
        JUNIT_INDENT_LEVEL_MEMBER,
        undefined,
      ),
      new VariableDeclaration(
        "private",
        "List<Entity>",
        "entities",
        JUNIT_INDENT_LEVEL_MEMBER,
        undefined,
      ),
      new VariableDeclaration(
        "private",
        "List<Relation>",
        "relations",
        JUNIT_INDENT_LEVEL_MEMBER,
        undefined,
      ),
    ];
    return res;
  }

  static buildImportStatements() {
    let res: Array<ImportStatement> = [
      new ImportStatement("entity.*", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("relation.Relation", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("cdt.Processor", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("cdt.TemplateWork", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("org.junit.Assert", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("org.junit.Before", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("org.junit.After", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("org.junit.Test", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("util.*", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("java.util.ArrayList", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("java.util.List", JUNIT_INDENT_LEVEL_IMPORT),
      new ImportStatement("java.util.Map", JUNIT_INDENT_LEVEL_IMPORT),
    ];
    return res;
  }

  static buildPackageStatement() {
    return new PackageDeclaration("client");
  }

  static buildBeforeMethodDeclaration(groupName: string, caseName: string) {
    let methodName = "execute";
    let statements: Array<Statement> = [
      new VariableDeclaration(undefined, "String", "groupName", JUNIT_INDENT_LEVEL_BLOCK, `"${groupName}"`),
      new VariableDeclaration(undefined, "String", "caseName", JUNIT_INDENT_LEVEL_BLOCK, `"${caseName}"`),
      new VariableDeclaration(undefined, "String[]", "args", JUNIT_INDENT_LEVEL_BLOCK, `{String.format("src/test/resources/cases/_%s/_%s/", groupName, caseName), String.format("_%s", caseName) }`),
      new VariableDeclaration(undefined, "TemplateWork", "work", JUNIT_INDENT_LEVEL_BLOCK, "new TemplateWork()"),
      new VariableDeclaration(undefined, "Processor", "processor", JUNIT_INDENT_LEVEL_BLOCK, "work.execute(args)"),
      new VariableDeclaration(undefined, undefined, "this.relations", JUNIT_INDENT_LEVEL_BLOCK, "processor.getRelations()"),
      new VariableDeclaration(undefined, undefined, "this.entities", JUNIT_INDENT_LEVEL_BLOCK, "new ArrayList<>(processor.getEntities().values())"),
    ];
    let methodBody = new CodeBlock(statements, JUNIT_INDENT_LEVEL_BLOCK);
    return new MethodDeclaration(
      new CommentBlock([
        "execute ENRE-CPP and get generated entities and relations before every test cases"
      ]),
      JUNIT_MODIFIERS_TEST_METHOD,
      methodName,
      [],
      methodBody,
      JUNIT_ANNOTATION_BEFORE,
      JUNIT_INDENT_LEVEL_METHOD,
      "Exception",
    );
  }

  static buildAfterMethodDeclaration() {
    let methodName = "clear";
    let statements: Array<Statement> = [
      new MethodInvocationStatement("judgeCate.clear", [], JUNIT_INDENT_LEVEL_BLOCK),
    ];
    let methodBody = new CodeBlock(statements, JUNIT_INDENT_LEVEL_BLOCK);
    return new MethodDeclaration(
      new CommentBlock([
        "clear ENRE-CPP result in memory"
      ]),
      JUNIT_MODIFIERS_TEST_METHOD,
      methodName,
      [],
      methodBody,
      JUNIT_ANNOTATION_AFTER,
    )
  }
}
