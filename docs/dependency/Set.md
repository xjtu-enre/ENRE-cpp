# Set

## Supported pattern
```yaml
name: Set Declaration
```
A set dependency indicates any explicit assignment of a variable.

### Syntax:


#### Examples: 

- Example1
```cpp
int function(int i){
    int j;
    j = i;
}
```

```yaml
name: Set
entity:
    items:
        -   id: 0
            name: function
            category: Function
        -   id: 1
            name: i
            category: Variable
        -   id: 2
            name: j
            category: Variable
relation:
    items:
        -   category: Set
            src: 0
            dest: 2
            r:
                d: x
                e: .
                s: x
                u: .
        -   category: Use
            src: 0
            dest: 1
            r:
                d: .
                e: .
                s: x
                u: .
```

- Example2
```cpp
int function(int i){
    int *a,b=0;
    *a = b; 
}
```

```yaml
name: Deref Set
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
        -   category: Set
            src: 0
            dest: 2
            r:
                d: x
                e: .
                s: x
                u: Deref Set
```
