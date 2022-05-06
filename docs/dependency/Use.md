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
                d: xUse
                e: .
                s: x
                u: .
```

- Example2
```cpp
int function(int i){
    int a,*b=0;
    a = *b; 
}
```

```yaml
name: Deref Use
entity:
    items:
        -   id: 0
            name: function
            category: Function
        -   id: 1
            name: a
            category: Variable
        -   id: 2
            name: b
            category: Variable
relation:
    items:
        -   category: Use
            src: 0
            dest: 2
            r:
                d: .
                e: .
                s: x
                u: Deref Use
```


- Example3
```cpp
class A{
public:
    int x;
    int func(){
        return x;
    }
}
```

```yaml
name: Class Member Use
entity:
    items:
        -   id: 0
            name: A
            category: Class
        -   id: 1
            name: A::x
            category: Variable
        -   id: 2
            name: A::func
            category: Function
relation:
    items:
        -   category: Use
            src: 2
            dest: 1
            r:
                d: .
                e: .
                s: x
                u: Use
        -   category: Define
            src: 0
            dest: 1
            r:
                d: .
                e: .
                s: x
                u: .
        -   category: Define
            src: 0
            dest: 2
            r:
                d: x
                e: x
                s: x
                u: .
```