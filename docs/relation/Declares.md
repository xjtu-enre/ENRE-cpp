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

###### File Declares Function

```CPP
#include <iostream>

void greet();

void greet() {
    std::cout << "Hello, World!" << std::endl;
}
```

```yaml
name: File Declares Function
relation:
  type: Declares
  items:
    -   from: File:'file0.cpp'
        to: Function:'greet'
        loc: file0:3:6
```
