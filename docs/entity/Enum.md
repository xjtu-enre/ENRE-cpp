# Classes

## Supported pattern
```yaml
name: Enumeration Declaration
```
### Syntax
```text
enum-key attr(optional) enum-head-name(optional) enum-base(optional) {enumerator-list(optional)	(1)	
enum-key attr(optional) enum-head-name enum-base(optional) ;	(2)	
```
enum-key can be one of enum, enum class, or enum struct (since C++11).



#### Examples: 

- Example1: 
```cpp
    enum Color { 
        red, 
        green, 
        blue 
    };
```

```yaml
    name: Enum
    entity:
        filter: Enum
        items:
            -   name: Color
                loc: [ 1, 7 ]
                kind: Enum
                r:
                    d: xType
                    e: .
                    s: .
                    u: .
            -   name: Color::red
                loc: [ 1, 7 ]
                kind: Enumerator
                r:
                    d: xVar
                    e: .
                    s: enum constant
                    u: .
            -   name: Color::green
                loc: [ 1, 7 ]
                kind: Enumerator
                r:
                    d: xVar
                    e: .
                    s: enum constant
                    u: .
```

### Syntax: Scoped enumerations
```text
enum struct|class name { enumerator = constexpr , enumerator = constexpr , ... }	(1)	
enum struct|class name : type { enumerator = constexpr , enumerator = constexpr , ... }	(2)	
enum struct|class name ;	(3)	
enum struct|class name : type ;	(4)	
```

#### Examples: 

- Example1: 
```cpp
enum class Handle { 
        Invalid = 0 
};
```

```yaml
    name: Enum Class
    entity:
        filter: Enum Class
        items:
            -   name: Handle
                loc: [ 1, 7 ]
                kind: Enum
                r:
                    d: Type
                    e: .
                    s: .
                    u: .
            -   name: Handle::Invalid
                loc: [ 1, 7 ]
                kind: Enumerator
                r:
                    d: xVar
                    e: .
                    s: enum constant
                    u: .
```



