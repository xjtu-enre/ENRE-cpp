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
    entity:
        items:
            -   name: filename.cpp
                loc: [ -1, -1 ]
                category: File
                r:
                    d: .
                    e: .
                    s: .
                    u: .
```