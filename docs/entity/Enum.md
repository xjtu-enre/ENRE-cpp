## Entity: Enum
Description: An `enumeration` is a user-defined type that consists of a set of named integral constants that are known as enumerators.

### Supported Patterns

```yaml
name: Enumeration
```

#### Syntax: Enumeration Declaration
```text
(1) enumeration
enum-key attr(optional) enum-head-name(optional) enum-base(optional) {enumerator-list(optional)	(1)	
enum-key attr(optional) enum-head-name enum-base(optional) ;	(2)	
enum-key can be one of enum, enum class, or enum struct (since C++11).
(2) Scoped enumerations
enum struct|class name { enumerator = constexpr , enumerator = constexpr , ... }	(1)	
enum struct|class name : type { enumerator = constexpr , enumerator = constexpr , ... }	(2)	
enum struct|class name ;	(3)	
enum struct|class name : type ;	(4)	
```
##### Examples

###### Enum
```cpp
enum Color {};
```

```yaml
    name: Enum
    entity:
        items:
            -   name: Color
                loc: 1:6:1:10
                type: Enum
```

###### Enum Class
```cpp
enum class Handle {};
```

```yaml
    name: Enum Class
    entity:
        items:
            -   name: Handle
                loc: 1:12:1:17
                type: Enum
```



