# Classes

## Supported pattern
```
name: FileDeclaration
```
.h,.cpp,.cxx etc

#### Examples: 

- Example1:
    ``` 
    entities:
        filter: File
        items:
            -   name: filename.cpp
                loc: [ -1, -1 ]
                kind: File
    ```