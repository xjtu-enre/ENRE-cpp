## Relation: Flow To

Description:The `Flow To Relation` in C++ is used to represent the flow of data or control from one entity to another. It is especially relevant in the context of data flow analysis, where understanding how data moves through a program is crucial.

### Supported Patterns

```yaml
name: Flow To
```

#### Syntax: Flow to Declaration

```text
FlowToRelationDeclaration :
    source-entity ( variable, function, etc. )
    flow-to-entity ( variable, function, etc. )
```

##### Examples

###### Variable to Variable Flow

```CPP
void modifyValue(int* ptr) {
    *ptr = 10;
}

void example() {
    int value = 5;
    modifyValue(&value);
}
```

```yaml
name: Variable to Variable Flow
relation:
  type: Flow To
  items:
    - from: Variable:'value'
      to: Parameter Variable:'ptr'
      loc: file0:7:5
```
