## Relation: Extern Declare

Description:The `Extern Declare Relation` in C++ identifies the declaration of external variables or functions. These are declared with the extern keyword, indicating that their definitions are in another file.

### Supported Patterns

```yaml
name: Extern Declare
```

#### Syntax: Extern Declare Declaration

```text
ExternDeclaration :
    extern type-specifier declarator;
```

##### Examples

###### External Variable Declaration

```CPP
// In file1.cpp
int globalVar;

// In file2.cpp
extern int globalVar;  // Extern declare relation
```


```yaml
name: External Variable Declaration
relation:
  type: Extern Declare
  items:
    - from: File:'file2.cpp'
      to: Variable:'globalVar'
      loc: file2.cpp:1:12
```

###### External Function Declaration

```CPP
void externalFunction() {
    // Function definition
}

extern void externalFunction();  // Extern declare relation
```

```yaml
name: External Function Declaration
relation:
  type: Extern Declare
  items:
    - from: File:'file0'
      to: Function:'externalFunction'
      loc: file0:5:13
```