# Classes

## Supported pattern
```
name: Enumeration Declaration, Enumerator Declaration
```
### Syntax
``` cpp
enum-key attr(optional) enum-head-name(optional) enum-base(optional) {enumerator-list(optional)	(1)	
enum-key attr(optional) enum-head-name enum-base(optional) ;	(2)	
```
enum-key can be one of enum, enum class, or enum struct (since C++11).



#### Examples: 

- Example1: 
    ``` cpp
    enum Color { 
        red, 
        green, 
        blue 
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

### Syntax: Scoped enumerations
``` cpp
enum struct|class name { enumerator = constexpr , enumerator = constexpr , ... }	(1)	
enum struct|class name : type { enumerator = constexpr , enumerator = constexpr , ... }	(2)	
enum struct|class name ;	(3)	
enum struct|class name : type ;	(4)	
```

#### Examples: 

- Example1: 
    ``` cpp
    enum class Handle : std::uint32_t { 
        Invalid = 0 
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



