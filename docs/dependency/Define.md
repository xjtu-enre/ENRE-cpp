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
};
```

```yaml
name: Define
entity:
    items:
        -   id: 0
            name: file.h
            loc: [ -1, -1 ]
            category: File
        -   id: 1
            name: Units
            loc: [ 1, 7]
            category: Class
relation:
    items:
        -   category: Define
            src: 0
            dest: 1
            loc: [1, 7]
            r:
                d: x
                e: x
                s: x
                u: .

```