## Relation: Unresolved

Descriptions: `Unresolved Relation` identifies the relationship where a reference in the code cannot be resolved to its declaration due to various reasons like scope, visibility, or compilation errors.

### Supported Patterns

```yaml
name: Unresolved
```

#### Syntax: Unresolved Declaration

```
UnresolvedDeclaration :
    Reference 'unresolved'
```

##### Examples

###### Unresolved Variable Reference

```CPP
void myFunction() {
    int x = unresolvedVar;
}
```

```yaml
name: Unresolved Variable Reference
relation:
  type: Unresolved
  items:
    -   from: Function:'myFunction'
        to: Variable:'unresolvedVar'
        loc: file0:2:9
```

###### Unresolved Function Call

```CPP
void someFunction() {
    unresolvedFunc();
}
```

```yaml
name: Unresolved Function Call
relation:
  type: Unresolved
  items:
    -   from: Function:'someFunction'
        to: Function:'unresolvedFunc'
        loc: file0:2:5
```

###### Unresolved Class Method Call

```CPP
class MyClass {
public:
    void callMethod() {
        unresolvedMethod();
    }
};
```

```yaml
name: Unresolved Class Method Call
relation:
  type: Unresolved
  items:
    -   from: Method:'callMethod'
        to: Method:'unresolvedMethod'
        loc: file0:3:9
```

###### Unresolved Template Reference

```CPP
template<typename T>
void templateFunction() {
    T unresolvedTemplateVar;
}
```

```yaml
name: Unresolved Template Reference
relation:
  type: Unresolved
  items:
    -   from: Template:'templateFunction'
        to: Variable:'unresolvedTemplateVar'
        loc: file0:3:7
```

###### Unresolved Namespace Reference
```CPP
void anotherFunction() {
    using namespace UnresolvedNamespace;
}
```

```yaml
name: Unresolved Namespace Reference
relation:
  type: Unresolved
  items:
    -   from: Function:'anotherFunction'
        to: Namespace:'UnresolvedNamespace'
        loc: file0:2:5
```
