# Use

Use dependency can be divided into following kinds:
```yaml
name: Use Declaration
```
A Use dependency indicates a reference in an active region of code to a known C/C++ variable.

## Supported pattern
```
name: Use Declaration
```
### Syntax: ordinary Use

#### Examples: 

```cpp
extern int var1;
int func() {
    int local_var;
    local_var = var1; // use of var1
}
```

```yaml
name: Use
entities:
    items:
        -   id: 0
            name: var1
            category: Object
        -   id: 1
            name: func
            category: Function
        -   id: 2
            name: func::local_var
            category: Object
dependencies:
    items:
        -   category: Use
            src: 1
            dest: 0
        -   category: Set
            src: 1
            dest: 2
```

### Use Macrodefine
Use Macrodefine and Useby Macrodefine indicate a reference to a known entity in a macro definition.
#### Examples: 

```cpp
// in fileA.cpp
int func1();
#define MACRO (func1())
```

```yaml
name: Use MacroDefine
entities:
    items:
        -   id: 0
            name: fileA.cpp
            category: File
        -   id: 1
            name: func1
            category: Function
        -   id: 2
            name: MACRO
            category: Macro
dependencies:
    items:
        -   category: Use Macrodefine
            src: 0
            dest: 1
```
