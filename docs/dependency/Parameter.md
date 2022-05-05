# Parameter

## Supported pattern
```yaml
name: Parameter Declaration
```
### Syntax: 

#### Examples: 

- Example1
```cpp
int function(int i){
    return i;
}
```

```yaml
name: Parameter 1
entity:
    items:
        -   id: 0
            name: function
            category: Function
        -   id: 1
            name: i
            category: Variable
relation:
    items:
        -   category: Parameter
            src: 0
            dest: 1
            r:
                d: .
                e: .
                s: .
                u: e/Parameter
```

- Example2
```cpp
int function(int array[])
    return -1;
}
```

```yaml
name: Parameter 2
entity:
    items:
        -   id: 0
            name: function
            category: Function
        -   id: 1
            name: array
            category: Variable
relation:
    items:
        -   category: Parameter
            src: 0
            dest: 1
            r:
                d: .
                e: .
                s: .
                u: e/Parameter
```