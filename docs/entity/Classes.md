# Classes

## Supported pattern
```
name: ClassDeclaration(include class and struct), UnionDeclaration
```
### Syntax
``` cpp
class-key attr class-head-name { member-specification }	(1)	
class-key attr class-head-name : base-specifier-list { member-specification }	(2)	
```
class-key can be one of class, struct, union.


#### Examples: 

- Example1: class and struct without base-specifier-list
    ``` cpp
    class Matrix {
    };
    struct MyStruct {
        int value;
    };
    ```

    ``` 
    entities:
        filter: classes
        items:
            -   name: Matrix
                loc: [ 1, 7 ]
                kind: Class
            -   name: MyStruct
                loc: [ 1, 7 ]
                kind: Struct
            -   name: MyStruct::value
                loc: [ 1, 7 ]
                kind: Object
    ```

- Example2
    ``` cpp
    class Vector final {
    };
    ```

    ``` 
    entities:
        filter: classes
        items:
            -   name: Vector
                loc: [ 1, 7 ]
                kind: Class
    ```

### Syntax: Type alias
``` cpp
union attr class-head-name { member-specification }		
```


#### Examples: 

- Example1:
    ``` cpp
    union month
    {
        std::uint16_t s[2]; // occupies 4 bytes
        std::uint8_t c;     // occupies 1 byte
    };  
    ```

    ``` 
    entities:
        filter: classes
        items:
            -   name: month
                loc: [ 1, 7 ]
                kind: Union
            -   name: month::s
                loc: [ 1, 7 ]
                kind: Object
            -   name: month::c
                loc: [ 1, 7 ]
                kind: Object
    ```