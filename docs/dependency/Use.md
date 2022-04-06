# Use

Use dependency can be divided into following kinds:
``` 
'Asm Use', 'Use Ptr', 'Addr Use', 'Addr Use Return', 'Cast Use', 'Use Return', 'Use Macrodefine'
```
A Use dependency indicates a reference in an active region of code to a known C/C++ variable.

## Supported pattern
```
name: Use Declaration
```
### Syntax: ordinary Use

#### Examples: 

``` cpp
extern int var1;
int func() {
    int local_var;
    local_var = var1; // use of var1
}
```

``` 
entities:
    items:
        -   id: 0
            name: var1
            kind: Object
        -   id: 1
            name: func
            kind: Function
        -   id: 2
            name: func::local_var
            kind: Object
dependencies:
    items:
        -   kind: Use
            src: 1
            dest: 0
        -   kind: Set
            src: 1
            dest: 2
```

### Use Macrodefine
Use Macrodefine and Useby Macrodefine indicate a reference to a known entity in a macro definition.
#### Examples: 

``` cpp
// in fileA.cpp
int func1();
#define MACRO (func1())
```

``` 
entities:
    items:
        -   id: 0
            name: fileA.cpp
            kind: File
        -   id: 1
            name: func1
            kind: Function
        -   id: 2
            name: MACRO
            kind: Macro
dependencies:
    items:
        -   kind: Use Macrodefine
            src: 0
            dest: 1
```
