# Classes

## Supported pattern
```yaml
name: FileDeclaration
```
.h,.cpp,.cxx etc

#### Examples: 

- Example1:
```cpp
file.cpp
```
```yaml
    name: File
    entities:
        filter: File
        items:
            -   name: filename.cpp
                loc: [ -1, -1 ]
                kind: File
```