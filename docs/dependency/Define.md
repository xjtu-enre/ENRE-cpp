# Define


## Supported pattern
```yaml
name: Define Declaration
```
### Syntax: 

#### Examples: 

```cpp
//file.h
class Units{
};
```

```yaml
name: Define
entities:
    items:
        -   id: 0
            name: file.h
            loc: [ -1, -1 ]
            category: File
        -   id: 1
            name: Units
            loc: [ 1, 7]
            category: Class
dependencies:
    items:
        -   category: Define
            src: 0
            dest: 1
            loc: [1, 7]

```