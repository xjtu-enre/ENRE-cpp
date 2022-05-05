# Define


## Supported pattern
```yaml
name: Define Declaration
```
### Syntax: 

#### Examples: 

- Example1
```cpp
//file.h
class Units{
    int x;
};
```

```yaml
name: Define
entity:
    items:
        -   id: 0
            name: file.h
            category: File
        -   id: 1
            name: Units
            category: Class
        -   id: 2
            name: Units::x
            category: Variable
relation:
    items:
        -   category: Define
            src: 0
            dest: 1
            r:
                d: x
                e: x
                s: x
                u: .
        -   category: Define
            src: 1
            dest: 2
            r:
                d: x
                e: .
                s: x
                u: .
```