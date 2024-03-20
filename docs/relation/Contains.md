## Relation: Contains

Descriptions: `Contains Relation` represents a hierarchical or structural relationship where an entity (like a class, struct, or namespace) includes or owns another entity (like methods, variables, or inner classes).

### Supported Patterns

```yaml
name: Contains
```

#### Syntax: Contains Declaration

```text
ContainsDeclaration :
    ContainerEntity '{' ContainedEntities '}'
```

##### Examples

###### Class Contains Methods and Variables

```CPP
class Animal {
public:
    void speak() {}
    int age;
};
```

```yaml
name: Class Contains Methods and Variables
relation:
  type: Contains
  items:
    -   from: Class:'Animal'
        to: Method:'speak'
        loc: file0:2:10
    -   from: Class:'Animal'
        to: Variable:'age'
        loc: file0:3:9
```

###### Namespace Contains Function

```CPP
namespace Tools {
    void runTool() {}
}

```

```yaml
name: Namespace Contains Function
relation:
  type: Contains
  items:
    -   from: Namespace:'Tools'
        to: Function:'runTool'
        loc: file0:2:10
```

###### Struct Contains Enumerator

```CPP
struct Options {
    enum Choice { FIRST, SECOND };
};
```

```yaml
name: Struct Contains Enumerator
relation:
  type: Contains
  items:
    -   from: Struct:'Options'
        to: Enumerator:'Choice'
        loc: file0:2:10
```

###### Class Contains Nested Class


```CPP
class Outer {
    class Inner {};
};
```

```yaml
name: Class Contains Nested Class
relation:
  type: Contains
  items:
    -   from: Class:'Outer'
        to: Class:'Inner'
        loc: file0:2:10
```

###### Template Class Contains Method
```CPP
template <typename T>
class TemplateClass {
    void doSomething() {}
};
```

```yaml
name: Template Class Contains Method
relation:
  type: Contains
  items:
    -   from: Template:'TemplateClass'
        to: Method:'doSomething'
        loc: file0:3:10
```


###### Class Contains Template Method

```CPP
class SomeClass {
    template <typename T>
    void genericMethod() {}
};
```

```yaml
name: Class Contains Template Method
relation:
  type: Contains
  items:
    -   from: Class:'SomeClass'
        to: Method:'genericMethod'
        loc: file0:3:10
```

###### Namespace Contains Template Class
```CPP
namespace MyNamespace {
    template <typename T>
    class MyTemplateClass {};
};
```

```yaml
name: Namespace Contains Template Class
relation:
  type: Contains
  items:
    -   from: Namespace:'MyNamespace'
        to: Template:'MyTemplateClass'
        loc: file0:3:10
```