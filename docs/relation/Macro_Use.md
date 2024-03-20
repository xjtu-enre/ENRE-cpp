## Relation: Macro Use

Description:The `Macro Use` Relation in C++ is a way to capture the utilization of macros within the code. It identifies instances where macros, defined by the #define preprocessor directive, are employed.

### Supported Patterns

```yaml
name: Macro Use
```

#### Syntax: Macro Use Declaration

```text
MacroUseRelationDeclaration :
    macro-name
    usage-point
```

##### Examples

###### Simple Macro Use

```CPP
#define MAX 100
int main() {
    int maxValue = MAX;  // Use of 'MAX' macro
}
```

```yaml
name: Simple Macro Use
relation:
  type: Macro Use
  items:
    - from: Macro:'MAX'
      to: Function:'main'
      loc: file0:3:17
```

###### Macro Function Use

```CPP
#define SQUARE(x) ((x) * (x))
int main() {
    int result = SQUARE(5);  // Use of 'SQUARE' macro
}
```

```yaml
name: Macro Function Use
relation:
  type: Macro Use
  items:
    - from: Macro:'SQUARE'
      to: Function:'main'
      loc: file0:3:17
```

###### Conditional Compilation Macro Use

```CPP
#define DEBUG
#ifdef DEBUG
void debugFunction() {
    // Debugging code
}
#endif
```

```yaml
name: Conditional Compilation Macro Use
relation:
  type: Macro Use
  items:
    - from: Macro:'DEBUG'
      to: Function:'debugFunction'
      loc: file0:3:1
```

###### Macro Use in Macro Definition


```CPP
#define BASE 10
#define MULTIPLY_BASE(x) (BASE * (x))
```

```yaml
name: Macro Use in Macro Definition
relation:
  type: Macro Use
  items:
    - from: Macro:'BASE'
      to: Macro:'MULTIPLY_BASE'
      loc: file0:2:1
```