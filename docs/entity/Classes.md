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

    ``` json
    entities:
        filter: Alias
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
    ```

- Example2
    ``` cpp
    class Vector final {
    };
    ```

    ``` json
    entities:
        filter: Alias
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
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

    ``` json
    entities:
        filter: Alias
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
    ```