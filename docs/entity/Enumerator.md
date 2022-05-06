# Classes

## Supported pattern
```yaml
name: Enumerator Declaration
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
    name: Enumerator
    entity:
        filter: Enum & Enumerator
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