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

- Example1
```cpp
extern int var1;
int func() {
    int local_var;
    local_var = var1; // use of var1
}
```

```yaml
name: Use
entity:
    items:
        -   id: 0
            name: var1
            category: Object
        -   id: 1
            name: func
            category: Function
        -   id: 2
            name: func::local_var
            category: Variable
relation:
    items:
        -   category: Use
            src: 1
            dest: 0
            r:
                d: .
                e: .
                s: .
                u: .
        -   category: Set
            src: 1
            dest: 2
            r:
                d: r/Use
                e: .
                s: x
                u: .
```
