## Relation: Declares

Descriptions: `Declares Relation` signifies the declaration of entities like variables, functions, classes, etc., within a specific scope, like a function, class, or namespace.

### Supported Patterns

```yaml
name: Declares
```

#### Syntax: Declares Declaration

```text
DeclaresDeclaration :
    ScopeEntity '{' DeclaredEntities '}'
```

##### Examples

###### Function Declares Variables

```CPP
void myFunction() {
    int a;
    double b;
}
```

```yaml
name: Function Declares Variables
relation:
  type: Declares
  items:
    -   from: Function:'myFunction'
        to: Variable:'a'
        loc: file0:2:9
    -   from: Function:'myFunction'
        to: Variable:'b'
        loc: file0:3:13
```

###### Class Declares Members

```CPP
class MyClass {
public:
    int memberVar;
    void memberFunc() {}
};
```

```yaml
name: Class Declares Members
relation:
  type: Declares
  items:
    -   from: Class:'MyClass'
        to: Variable:'memberVar'
        loc: file0:3:9
    -   from: Class:'MyClass'
        to: Method:'memberFunc'
        loc: file0:4:10
```

###### Namespace Declares Function

```CPP
namespace MyNamespace {
    void someFunction() {}
}
```

```yaml
name: Namespace Declares Function
relation:
  type: Declares
  items:
    -   from: Namespace:'MyNamespace'
        to: Function:'someFunction'
        loc: file0:2:10
```

###### Method Declares Local Class

```CPP
void outerFunction() {
    class LocalClass {};
}
```

```yaml
name: Method Declares Local Class
relation:
  type: Declares
  items:
    -   from: Function:'outerFunction'
        to: Class:'LocalClass'
        loc: file0:2:11
```

###### Template Function Declares Variable
```CPP
template <typename T>
void templateFunction() {
    T var;
}
```

```yaml
name: Template Function Declares Variable
relation:
  type: Declares
  items:
    -   from: Template:'templateFunction'
        to: Variable:'var'
        loc: file0:3:7
```